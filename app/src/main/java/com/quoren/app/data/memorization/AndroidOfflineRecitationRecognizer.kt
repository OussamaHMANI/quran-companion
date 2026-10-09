package com.quoren.app.data.memorization

import android.content.Context
import android.content.Intent
import android.Manifest
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import com.quoren.app.domain.memorization.OfflineAsrModelState
import com.quoren.app.domain.memorization.OfflineModelFailure
import com.quoren.app.domain.memorization.RecitationRecognitionState
import com.quoren.app.domain.memorization.RecitationRecognizer
import com.quoren.app.domain.memorization.RecitationTranscriptAccumulator
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong
import java.util.zip.ZipInputStream
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import org.vosk.Model
import org.vosk.Recognizer

@Singleton
class AndroidOfflineRecitationRecognizer @Inject constructor(
    @ApplicationContext private val context: Context,
    private val http: OkHttpClient,
) : RecitationRecognizer, RecognitionListener {

    private val modelHttp = http.newBuilder()
        .readTimeout(2, TimeUnit.MINUTES)
        .callTimeout(30, TimeUnit.MINUTES)
        .build()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val modelMutex = Mutex()
    private val modelDirectory = File(context.filesDir, "speech/vosk-model-ar-mgb2-0.4")
    private val stagingDirectory = File(context.filesDir, "speech/model-staging")
    private val archiveFile = File(context.cacheDir, "vosk-ar-mgb2.zip.part")
    private val _state = MutableStateFlow<RecitationRecognitionState>(RecitationRecognitionState.Idle)
    override val state: StateFlow<RecitationRecognitionState> = _state.asStateFlow()
    private val _modelState = MutableStateFlow<OfflineAsrModelState>(
        if (hasValidModelDirectory()) OfflineAsrModelState.Ready else OfflineAsrModelState.Missing,
    )
    override val modelState: StateFlow<OfflineAsrModelState> = _modelState.asStateFlow()

    private var localModel: Model? = null
    @Volatile private var localRecognizer: Recognizer? = null
    @Volatile private var audioRecord: AudioRecord? = null
    @Volatile private var captureThread: Thread? = null
    private val captureActive = AtomicBoolean(false)
    private val finishRequested = AtomicBoolean(false)
    private val startPending = AtomicBoolean(false)
    private val recognitionGeneration = AtomicLong(0)
    @Volatile private var cleanupJob: kotlinx.coroutines.Job? = null
    private var onlineRecognizer: SpeechRecognizer? = null
    private val resultDelivered = AtomicBoolean(false)
    private val transcriptAccumulator = RecitationTranscriptAccumulator()
    private val wordConfidences = mutableListOf<Float>()

    override fun downloadOfflineModel() {
        if (_modelState.value is OfflineAsrModelState.Downloading || _modelState.value == OfflineAsrModelState.Ready) return
        _modelState.value = OfflineAsrModelState.Downloading(null)
        scope.launch {
            runCatching {
                downloadAndExtractModel()
                modelMutex.withLock { localModel = Model(modelDirectory.absolutePath) }
            }.onSuccess {
                _modelState.value = OfflineAsrModelState.Ready
            }.onFailure { error ->
                archiveFile.delete()
                stagingDirectory.deleteRecursively()
                val reason = when (error) {
                    is OfflineModelDownloadException -> error.reason
                    is UnknownHostException, is SocketTimeoutException, is IOException -> OfflineModelFailure.NETWORK
                    else -> OfflineModelFailure.UNKNOWN
                }
                _modelState.value = OfflineAsrModelState.Failed(reason)
            }
        }
    }

    override fun start() {
        if (_modelState.value != OfflineAsrModelState.Ready) {
            _state.value = RecitationRecognitionState.OfflineModelMissing
            return
        }
        if (!startPending.compareAndSet(false, true)) return
        val generation = recognitionGeneration.incrementAndGet()
        scope.launch {
            try {
                cleanupJob?.join()
                if (generation != recognitionGeneration.get() || !captureActive.compareAndSet(false, true)) return@launch
                finishRequested.set(false)
                resultDelivered.set(false)
                transcriptAccumulator.reset()
                wordConfidences.clear()
                _state.value = RecitationRecognitionState.Listening
                var createdRecognizer: Recognizer? = null
                var createdRecord: AudioRecord? = null
                runCatching {
                    val recognizer = modelMutex.withLock {
                        val model = localModel ?: Model(modelDirectory.absolutePath).also { localModel = it }
                        Recognizer(model, SAMPLE_RATE)
                    }
                    createdRecognizer = recognizer
                    val record = createAudioRecord()
                    createdRecord = record
                    record.startRecording()
                    if (record.recordingState != AudioRecord.RECORDSTATE_RECORDING) {
                        error("Could not start microphone capture")
                    }
                    if (generation != recognitionGeneration.get()) {
                        record.stop()
                        record.release()
                        recognizer.close()
                        return@runCatching
                    }
                    localRecognizer = recognizer
                    audioRecord = record
                    Thread({ captureAudio(record, recognizer) }, "QuorenVoskCapture").also {
                        captureThread = it
                        it.start()
                    }
                    createdRecognizer = null
                    createdRecord = null
                }.onFailure {
                    createdRecord?.let { record ->
                        runCatching { record.stop() }
                        runCatching { record.release() }
                    }
                    createdRecognizer?.close()
                    if (generation == recognitionGeneration.get()) {
                        captureActive.set(false)
                        localRecognizer = null
                        audioRecord = null
                        _state.value = RecitationRecognitionState.Error
                    }
                }
            } finally {
                if (generation == recognitionGeneration.get()) startPending.set(false)
            }
        }
    }

    /** Called only after the user accepts the in-app disclosure that microphone audio may be sent to the system provider. */
    override fun startOnline() {
        recognitionGeneration.incrementAndGet()
        startPending.set(false)
        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            _state.value = RecitationRecognitionState.OnlineServiceUnavailable
            return
        }
        runCatching {
            releaseOnlineRecognizer()
            resultDelivered.set(false)
            transcriptAccumulator.reset()
            wordConfidences.clear()
            onlineRecognizer = SpeechRecognizer.createSpeechRecognizer(context).also { recognizer ->
                recognizer.setRecognitionListener(this)
                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ar-SA")
                    putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, false)
                    putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                    putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
                }
                _state.value = RecitationRecognitionState.Listening
                recognizer.startListening(intent)
            }
        }.onFailure { error ->
            _state.value = if (error is SecurityException) RecitationRecognitionState.PermissionRequired
            else RecitationRecognitionState.OnlineServiceUnavailable
        }
    }

    override fun cancel() {
        recognitionGeneration.incrementAndGet()
        startPending.set(false)
        finishRequested.set(false)
        captureActive.set(false)
        audioRecord?.let { record -> runCatching { record.stop() } }
        releaseOnlineRecognizer()
        resultDelivered.set(false)
        _state.value = RecitationRecognitionState.Idle
        val previousCleanup = cleanupJob
        cleanupJob = scope.launch {
            previousCleanup?.join()
            withContext(Dispatchers.IO) { captureThread?.join() }
            localRecognizer?.close()
            localRecognizer = null
            audioRecord = null
            captureThread = null
            transcriptAccumulator.reset()
            wordConfidences.clear()
        }
    }

    override fun finish() {
        val record = audioRecord
        if (record != null) {
            finishRequested.set(true)
            captureActive.set(false)
            runCatching { record.stop() }
            scope.launch {
                runCatching {
                    withContext(Dispatchers.IO) { captureThread?.join() }
                    withContext(Dispatchers.Main.immediate) {
                        publishVoskFinal(localRecognizer?.finalResult.orEmpty())
                    }
                }.onFailure {
                    withContext(Dispatchers.Main.immediate) {
                        _state.value = RecitationRecognitionState.Error
                        releaseLocalRecognizer()
                    }
                }
            }
        } else {
            onlineRecognizer?.stopListening()
        }
    }

    private fun createAudioRecord(): AudioRecord {
        if (context.checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            error("Microphone permission is required")
        }
        val minimumBufferSize = AudioRecord.getMinBufferSize(
            SAMPLE_RATE.toInt(),
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
        )
        require(minimumBufferSize > 0) { "Microphone format is unsupported" }
        val bufferSize = maxOf(minimumBufferSize, SAMPLE_RATE.toInt() * 2 / 5)
        val record = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            SAMPLE_RATE.toInt(),
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            bufferSize,
        )
        if (record.state != AudioRecord.STATE_INITIALIZED) {
            record.release()
            error("Could not initialize microphone capture")
        }
        return record
    }

    private fun captureAudio(record: AudioRecord, recognizer: Recognizer) {
        val frame = ShortArray(ASR_FRAME_SAMPLES)
        val readBuffer = ShortArray(ASR_FRAME_SAMPLES)
        var bufferedSamples = 0
        try {
            while (captureActive.get()) {
                val count = record.read(readBuffer, 0, readBuffer.size)
                if (!captureActive.get()) break
                if (count < 0) error("Microphone read failed: $count")
                if (count == 0) continue

                var inputOffset = 0
                while (inputOffset < count) {
                    val copied = minOf(frame.size - bufferedSamples, count - inputOffset)
                    System.arraycopy(readBuffer, inputOffset, frame, bufferedSamples, copied)
                    bufferedSamples += copied
                    inputOffset += copied
                    if (bufferedSamples == frame.size) {
                        acceptVoskFrame(recognizer, frame)
                        bufferedSamples = 0
                    }
                }
            }
            if (finishRequested.get() && bufferedSamples > 0) {
                frame.fill(0, bufferedSamples, frame.size)
                acceptVoskFrame(recognizer, frame)
            }
        } catch (_: Exception) {
            if (captureActive.get()) _state.value = RecitationRecognitionState.Error
        } finally {
            captureActive.set(false)
            runCatching { if (record.recordingState == AudioRecord.RECORDSTATE_RECORDING) record.stop() }
            record.release()
            if (audioRecord === record) audioRecord = null
            if (captureThread === Thread.currentThread()) captureThread = null
            if (!finishRequested.get() && localRecognizer === recognizer) {
                recognizer.close()
                localRecognizer = null
            }
        }
    }

    private fun acceptVoskFrame(recognizer: Recognizer, frame: ShortArray) {
        if (recognizer.acceptWaveForm(frame, frame.size)) publishVoskSegment(recognizer.result)
        else publishVoskPartial(recognizer.partialResult)
    }

    private fun publishVoskPartial(json: String) {
        val transcript = runCatching { JSONObject(json).optString("partial").trim() }.getOrDefault("")
        if (transcript.isNotEmpty() && !resultDelivered.get()) {
            val accumulated = transcriptAccumulator.acceptPartial(transcript)
            _state.value = RecitationRecognitionState.PartialTranscript(accumulated)
        }
    }

    private fun releaseOnlineRecognizer() {
        onlineRecognizer?.cancel()
        onlineRecognizer?.destroy()
        onlineRecognizer = null
    }

    private fun downloadAndExtractModel() {
        val request = Request.Builder().url(MODEL_URL).build()
        modelHttp.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw OfflineModelDownloadException(OfflineModelFailure.NETWORK)
            val body = response.body ?: throw OfflineModelDownloadException(OfflineModelFailure.NETWORK)
            val expectedBytes = body.contentLength()
            if (expectedBytes == 0L || expectedBytes > MAX_ARCHIVE_BYTES) {
                throw OfflineModelDownloadException(OfflineModelFailure.INVALID_MODEL)
            }
            val requiredSpace = if (expectedBytes > 0) expectedBytes * MIN_FREE_SPACE_MULTIPLIER else MIN_FREE_SPACE_BYTES
            if (context.filesDir.usableSpace < requiredSpace) {
                throw OfflineModelDownloadException(OfflineModelFailure.STORAGE)
            }
            archiveFile.parentFile?.mkdirs()
            var downloaded = 0L
            body.byteStream().use { input ->
                BufferedOutputStream(FileOutputStream(archiveFile)).use { output ->
                    val buffer = ByteArray(BUFFER_SIZE)
                    while (true) {
                        val count = input.read(buffer)
                        if (count < 0) break
                        output.write(buffer, 0, count)
                        downloaded += count
                        if (downloaded > MAX_ARCHIVE_BYTES) {
                            throw OfflineModelDownloadException(OfflineModelFailure.INVALID_MODEL)
                        }
                        _modelState.value = OfflineAsrModelState.Downloading(
                            expectedBytes.takeIf { it > 0 }?.let { (downloaded * 100 / it).toInt().coerceIn(0, 100) },
                        )
                    }
                }
            }
            if (expectedBytes > 0 && downloaded != expectedBytes) {
                throw OfflineModelDownloadException(OfflineModelFailure.NETWORK)
            }
        }

        stagingDirectory.deleteRecursively()
        stagingDirectory.mkdirs()
        unzipSafely(archiveFile, stagingDirectory)
        val candidate = stagingDirectory.walkTopDown().firstOrNull {
            it.isDirectory && File(it, "am/final.mdl").isFile && File(it, "graph/words.txt").isFile
        } ?: throw OfflineModelDownloadException(OfflineModelFailure.INVALID_MODEL)
        modelDirectory.deleteRecursively()
        modelDirectory.parentFile?.mkdirs()
        if (!candidate.renameTo(modelDirectory)) throw OfflineModelDownloadException(OfflineModelFailure.STORAGE)
        archiveFile.delete()
        stagingDirectory.deleteRecursively()
    }

    private fun unzipSafely(archive: File, destination: File) {
        val root = destination.canonicalFile
        var extractedBytes = 0L
        ZipInputStream(BufferedInputStream(FileInputStream(archive))).use { input ->
            while (true) {
                val entry = input.nextEntry ?: break
                val outputFile = File(root, entry.name).canonicalFile
                require(outputFile.path.startsWith(root.path + File.separator)) { "Unsafe model archive path" }
                if (entry.isDirectory) {
                    outputFile.mkdirs()
                } else {
                    outputFile.parentFile?.mkdirs()
                    BufferedOutputStream(FileOutputStream(outputFile)).use { output ->
                        val buffer = ByteArray(BUFFER_SIZE)
                        while (true) {
                            val count = input.read(buffer)
                            if (count < 0) break
                            extractedBytes += count
                            require(extractedBytes <= MAX_EXTRACTED_BYTES) { "Model archive exceeds extraction limit" }
                            output.write(buffer, 0, count)
                        }
                    }
                }
                input.closeEntry()
            }
        }
    }

    private fun hasValidModelDirectory(): Boolean =
        File(modelDirectory, "am/final.mdl").isFile && File(modelDirectory, "graph/words.txt").isFile

    private fun parseVoskJson(json: String): Pair<String, List<Float>> = runCatching {
        val result = JSONObject(json)
        val transcript = result.optString("text").trim()
        val words = result.optJSONArray("result")
        val confidences = (0 until (words?.length() ?: 0)).mapNotNull { index ->
            words?.optJSONObject(index)?.optDouble("conf")?.takeIf { it in 0.0..1.0 }?.toFloat()
        }
        transcript to confidences
    }.getOrDefault("" to emptyList())

    private fun publishVoskSegment(json: String) {
        val (segment, confidences) = parseVoskJson(json)
        wordConfidences += confidences
        val accumulated = transcriptAccumulator.acceptSegment(segment)
        if (accumulated.isNotBlank() && !resultDelivered.get()) {
            _state.value = RecitationRecognitionState.PartialTranscript(accumulated)
        }
    }

    private fun publishVoskFinal(json: String) {
        if (!resultDelivered.compareAndSet(false, true)) return
        val (lastSegment, confidences) = parseVoskJson(json)
        wordConfidences += confidences
        val transcript = transcriptAccumulator.finish(lastSegment)
        val confidence = wordConfidences.takeIf { it.isNotEmpty() }?.average()?.toFloat()
        _state.value = if (transcript.isBlank()) RecitationRecognitionState.NoSpeech
        else RecitationRecognitionState.Result(transcript, confidence)
        releaseLocalRecognizer()
    }

    private fun releaseLocalRecognizer() {
        localRecognizer?.close()
        localRecognizer = null
        audioRecord = null
        captureThread = null
        captureActive.set(false)
        finishRequested.set(false)
        transcriptAccumulator.reset()
        wordConfidences.clear()
    }

    override fun onReadyForSpeech(params: Bundle?) { _state.value = RecitationRecognitionState.Listening }
    override fun onBeginningOfSpeech() { _state.value = RecitationRecognitionState.Listening }
    override fun onRmsChanged(rmsdB: Float) = Unit
    override fun onBufferReceived(buffer: ByteArray?) = Unit
    override fun onEndOfSpeech() = Unit

    override fun onError(error: Int) {
        _state.value = when (error) {
            SpeechRecognizer.ERROR_NO_MATCH, SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> RecitationRecognitionState.NoSpeech
            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> RecitationRecognitionState.PermissionRequired
            SpeechRecognizer.ERROR_NETWORK, SpeechRecognizer.ERROR_NETWORK_TIMEOUT,
            SpeechRecognizer.ERROR_SERVER, SpeechRecognizer.ERROR_SERVER_DISCONNECTED -> RecitationRecognitionState.NetworkUnavailable
            else -> RecitationRecognitionState.Error
        }
        releaseOnlineRecognizer()
    }

    override fun onResults(results: Bundle?) {
        if (!resultDelivered.compareAndSet(false, true)) return
        val transcript = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.firstOrNull()
        if (transcript.isNullOrBlank()) {
            _state.value = RecitationRecognitionState.NoSpeech
            releaseOnlineRecognizer()
            return
        }
        val confidence = results.getFloatArray(SpeechRecognizer.CONFIDENCE_SCORES)?.firstOrNull()?.takeIf { it >= 0f }
        _state.value = RecitationRecognitionState.Result(transcript, confidence)
        releaseOnlineRecognizer()
    }

    override fun onPartialResults(partialResults: Bundle?) {
        val transcript = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.firstOrNull()?.trim()
        if (!transcript.isNullOrEmpty() && !resultDelivered.get()) {
            _state.value = RecitationRecognitionState.PartialTranscript(transcript)
        }
    }
    override fun onEvent(eventType: Int, params: Bundle?) = Unit

    companion object {
        const val MODEL_URL = "https://alphacephei.com/vosk/models/vosk-model-ar-mgb2-0.4.zip"
        const val MODEL_DOWNLOAD_SIZE_MB = 318
        private const val SAMPLE_RATE = 16_000f
        private const val ASR_FRAME_SAMPLES = 3_200
        private const val BUFFER_SIZE = 32 * 1024
        private const val MAX_ARCHIVE_BYTES = 400L * 1024 * 1024
        private const val MAX_EXTRACTED_BYTES = 1_500L * 1024 * 1024
        private const val MIN_FREE_SPACE_MULTIPLIER = 3L
        private const val MIN_FREE_SPACE_BYTES = 1_000L * 1024 * 1024
    }
}

    private class OfflineModelDownloadException(val reason: OfflineModelFailure) : IOException()
