package com.quoren.app.data.audio

import com.quoren.app.domain.model.StreamingQuality
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class OfflinePlaybackTest {

    @get:Rule val tmp = TemporaryFolder()

    private fun store() = AudioFileStore(tmp.root)

    @Test
    fun downloadedAyahResolvesToLocalFileAheadOfStreaming() = runTest {
        val store = store()
        store.ayahFile("alafasy", 1, 2).apply { parentFile!!.mkdirs(); writeBytes(byteArrayOf(1, 2, 3)) }

        val source = AudioSourceProviderImpl(store).resolve("alafasy", 1, 2, StreamingQuality.HIGH)

        assertNotNull(source!!.localPath)
        assertEquals(store.ayahFile("alafasy", 1, 2).absolutePath, source.localPath)
        assertEquals("https://everyayah.com/data/Alafasy_128kbps/001002.mp3", source.remoteUrl)
    }

    @Test
    fun missingEmptyOrPartialFilesFallBackToStreaming() = runTest {
        val store = store()
        val dir = store.surahDir("alafasy", 1).apply { mkdirs() }
        File(dir, "001002.mp3").writeBytes(ByteArray(0))
        File(dir, "001003.mp3.part").writeBytes(byteArrayOf(1))

        val provider = AudioSourceProviderImpl(store)
        assertNull(provider.resolve("alafasy", 1, 1, StreamingQuality.HIGH)!!.localPath)
        assertNull(provider.resolve("alafasy", 1, 2, StreamingQuality.HIGH)!!.localPath)
        assertNull(provider.resolve("alafasy", 1, 3, StreamingQuality.HIGH)!!.localPath)
    }

    @Test
    fun lowQualityUsesLowBitrateUrlWhenAvailable() = runTest {
        val provider = AudioSourceProviderImpl(store())
        assertEquals(
            "https://everyayah.com/data/Alafasy_64kbps/001001.mp3",
            provider.resolve("alafasy", 1, 1, StreamingQuality.LOW)!!.remoteUrl,
        )
        assertEquals(
            "https://everyayah.com/data/Ghamadi_40kbps/001001.mp3",
            provider.resolve("ghamdi", 1, 1, StreamingQuality.LOW)!!.remoteUrl,
        )
        assertNull(provider.resolve("unknown", 1, 1, StreamingQuality.HIGH))
    }

    @Test
    fun deletingSurahDirectoryRemovesAllItsFiles() {
        val store = store()
        store.ayahFile("maher", 3, 1).apply { parentFile!!.mkdirs(); writeBytes(byteArrayOf(1)) }
        store.surahDir("maher", 3).deleteRecursively()
        assertNull(store.existingAyahFile("maher", 3, 1))
    }
}
