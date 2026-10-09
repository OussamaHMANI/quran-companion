package com.quoren.app.data.backup

import androidx.room.withTransaction
import com.quoren.app.data.local.BookmarkEntity
import com.quoren.app.data.local.DailyGoalCompletionEntity
import com.quoren.app.data.local.DailyGoalEntity
import com.quoren.app.data.local.MemorizationProgressEntity
import com.quoren.app.data.local.MemorizationSessionEntity
import com.quoren.app.data.local.QuranDatabase
import com.quoren.app.data.local.ReadingProgressEntity
import com.quoren.app.data.local.ReviewHistoryEntity
import com.quoren.app.domain.model.AdhanSound
import com.quoren.app.domain.model.AsrMadhab
import com.quoren.app.domain.model.AudioSettings
import com.quoren.app.domain.model.DifficultyRating
import com.quoren.app.domain.model.MemorizationMode
import com.quoren.app.domain.memorization.MemorizationStatus
import com.quoren.app.domain.model.Mosque
import com.quoren.app.domain.model.PrayerCalculationMethod
import com.quoren.app.domain.model.PrayerLocation
import com.quoren.app.domain.model.PrayerOffsets
import com.quoren.app.domain.model.PrayerSettings
import com.quoren.app.domain.model.QuranStructure
import com.quoren.app.domain.model.ReaderSettings
import com.quoren.app.domain.model.RepeatCount
import com.quoren.app.domain.model.SilentModeBehavior
import com.quoren.app.domain.model.StreamingQuality
import com.quoren.app.domain.model.ThemeMode
import com.quoren.app.domain.repository.AudioSettingsRepository
import com.quoren.app.domain.repository.LocalBackupRepository
import com.quoren.app.domain.repository.MosqueRepository
import com.quoren.app.domain.repository.PrayerConfiguration
import com.quoren.app.domain.repository.PrayerRepository
import com.quoren.app.domain.repository.ReaderSettingsRepository
import dagger.Reusable
import java.time.ZoneId
import javax.inject.Inject
import kotlinx.coroutines.flow.first
import org.json.JSONArray
import org.json.JSONObject

@Reusable
class LocalBackupRepositoryImpl @Inject constructor(
    private val db: QuranDatabase,
    private val readerSettings: ReaderSettingsRepository,
    private val audioSettings: AudioSettingsRepository,
    private val prayerRepository: PrayerRepository,
    private val mosqueRepository: MosqueRepository,
) : LocalBackupRepository {

    override suspend fun exportUserData(): String {
        val configuration = prayerRepository.observeConfiguration().first()
        val memorization = db.memorizationDao()
        val root = JSONObject()
            .put("format", FORMAT)
            .put("version", VERSION)
            .put("exportedAt", System.currentTimeMillis())
            .put("readerSettings", readerSettingsJson(readerSettings.observe().first()))
            .put("audioSettings", audioSettingsJson(audioSettings.observe().first()))
            .put("prayerConfiguration", prayerConfigurationJson(configuration))
            .put("bookmarks", JSONArray().apply { db.bookmarkDao().exportAll().forEach { put(bookmarkJson(it)) } })
            .put("readingProgress", db.progressDao().exportCurrent()?.let(::readingProgressJson) ?: JSONObject.NULL)
            .put("memorizationProgress", JSONArray().apply { memorization.exportProgress().forEach { put(progressJson(it)) } })
            .put("memorizationSessions", JSONArray().apply { memorization.exportSessions().forEach { put(sessionJson(it)) } })
            .put("reviewHistory", JSONArray().apply { memorization.exportReviews().forEach { put(reviewJson(it)) } })
            .put("dailyGoal", memorization.exportDailyGoal()?.let(::dailyGoalJson) ?: JSONObject.NULL)
            .put("dailyGoalCompletions", JSONArray().apply { memorization.exportGoalCompletions().forEach { put(completionJson(it)) } })
            .put("mosqueFavorites", JSONArray().apply { mosqueRepository.observeFavorites().first().forEach { put(mosqueJson(it)) } })
        return root.toString()
    }

    override suspend fun importUserData(json: String) {
        require(json.length <= MAX_BACKUP_CHARS) { "Backup file is too large" }
        val root = JSONObject(json)
        require(root.optString("format") == FORMAT) { "Unsupported backup format" }
        require(root.optInt("version", -1) == VERSION) { "Unsupported backup version" }

        val bookmarks = root.array("bookmarks", MAX_BOOKMARKS).map { item ->
            BookmarkEntity(
                id = item.optLong("id", 0L).coerceAtLeast(0L),
                ayahId = item.optInt("ayahId").requireAyahId(),
                createdAt = item.optLong("createdAt").coerceAtLeast(0L),
                note = item.optStringOrNull("note"),
            )
        }.distinctBy { it.ayahId }
        val readingProgress = root.optJSONObject("readingProgress")?.let { item ->
            val surahId = item.optInt("surahId")
            require(surahId in 1..114)
            ReadingProgressEntity(
                id = 0,
                surahId = surahId,
                ayahId = item.optInt("ayahId").requireAyahId(),
                page = item.optInt("page").also { require(it in 1..604) },
                updatedAt = item.optLong("updatedAt").coerceAtLeast(0L),
            )
        }
        val progress = root.array("memorizationProgress", MAX_MEMORIZATION_ITEMS).map { item ->
            val status = item.optString("status").also { require(it in MemorizationStatus.entries.map(Enum<*>::name)) }
            MemorizationProgressEntity(
                ayahId = item.optInt("ayahId").requireAyahId(),
                status = status,
                scheduleStage = item.optInt("scheduleStage").coerceIn(0, 8),
                dueEpochDay = item.optLong("dueEpochDay"),
                lastReviewedEpochDay = item.optNullableLong("lastReviewedEpochDay"),
                reviewCount = item.optInt("reviewCount").coerceAtLeast(0),
                consecutiveHardRatings = item.optInt("consecutiveHardRatings").coerceAtLeast(0),
                isWeak = item.optBoolean("isWeak"),
            )
        }.distinctBy { it.ayahId }
        val sessions = root.array("memorizationSessions", MAX_SESSIONS).map { item ->
            val surahId = item.optInt("surahId")
            require(surahId in 1..114)
            val start = item.optInt("startAyah")
            val end = item.optInt("endAyah")
            require(start > 0 && end in start..QuranStructure.AYAH_COUNT)
            val current = item.optInt("currentAyah")
            require(current in start..end)
            MemorizationSessionEntity(
                sessionId = item.optString("sessionId").also { require(it.isNotBlank() && it.length <= 100) },
                surahId = surahId,
                startAyah = start,
                endAyah = end,
                reciterId = item.optString("reciterId").take(100),
                repeatCount = item.optString("repeatCount").also { require(it in RepeatCount.entries.map(Enum<*>::name)) },
                mode = item.optString("mode").also { require(it in MemorizationMode.entries.map(Enum<*>::name)) },
                currentAyah = current,
                createdAt = item.optLong("createdAt").coerceAtLeast(0L),
                lastOpenedAt = item.optLong("lastOpenedAt").coerceAtLeast(0L),
                completedAt = item.optNullableLong("completedAt"),
            )
        }.distinctBy { it.sessionId }
        val reviews = root.array("reviewHistory", MAX_REVIEWS).map { item ->
            ReviewHistoryEntity(
                id = item.optLong("id", 0L).coerceAtLeast(0L),
                ayahId = item.optInt("ayahId").requireAyahId(),
                sessionId = item.optStringOrNull("sessionId"),
                difficulty = item.optString("difficulty").also { require(it in DifficultyRating.entries.map(Enum<*>::name)) },
                reviewedAt = item.optLong("reviewedAt").coerceAtLeast(0L),
                dueEpochDay = item.optLong("dueEpochDay"),
            )
        }
        val goal = root.optJSONObject("dailyGoal")?.let { item ->
            DailyGoalEntity(
                id = 0,
                ayahTarget = item.optInt("ayahTarget").coerceIn(1, QuranStructure.AYAH_COUNT),
                completedAyahs = item.optInt("completedAyahs").coerceIn(0, QuranStructure.AYAH_COUNT),
                epochDay = item.optLong("epochDay"),
            )
        }
        val completions = root.array("dailyGoalCompletions", MAX_MEMORIZATION_ITEMS).map { item ->
            DailyGoalCompletionEntity(item.optLong("epochDay"), item.optInt("ayahId").requireAyahId())
        }.distinctBy { it.epochDay to it.ayahId }
        val userSettings = parseSettings(root)
        val favorites = root.array("mosqueFavorites", MAX_FAVORITES).map { item ->
            Mosque(
                id = item.optString("id").also { require(it.isNotBlank() && it.length <= 200) },
                name = item.optString("name").also { require(it.isNotBlank() && it.length <= 200) },
                latitude = item.optDouble("latitude").also { require(it.isFinite() && it in -90.0..90.0) },
                longitude = item.optDouble("longitude").also { require(it.isFinite() && it in -180.0..180.0) },
                address = item.optStringOrNull("address"),
                phone = item.optStringOrNull("phone"),
                website = item.optStringOrNull("website")?.takeIf { it.startsWith("https://") || it.startsWith("http://") },
                distanceMeters = item.optDouble("distanceMeters", 0.0).coerceAtLeast(0.0),
                rating = item.optNullableDouble("rating")?.toFloat()?.takeIf { it in 0f..5f },
                isFavorite = true,
            )
        }.distinctBy { it.id }

        val memo = db.memorizationDao()
        db.withTransaction {
            db.bookmarkDao().clearForRestore()
            db.bookmarkDao().restoreAll(bookmarks)
            db.progressDao().clearForRestore()
            readingProgress?.let { db.progressDao().upsert(it) }
            memo.clearProgressForRestore()
            memo.restoreProgress(progress)
            memo.clearSessionsForRestore()
            memo.restoreSessions(sessions)
            memo.clearReviewsForRestore()
            memo.restoreReviews(reviews)
            memo.clearDailyGoalForRestore()
            goal?.let { memo.restoreDailyGoal(it) }
            memo.clearGoalCompletionsForRestore()
            memo.restoreGoalCompletions(completions)
        }
        readerSettings.update { userSettings.reader }
        audioSettings.update { userSettings.audio }
        prayerRepository.saveLocation(userSettings.prayer.location)
        prayerRepository.updateSettings(userSettings.prayer.settings)
        mosqueRepository.replaceFavorites(favorites)
    }

    private suspend fun parseSettings(root: JSONObject): BackupSettings {
        val reader = root.optJSONObject("readerSettings") ?: JSONObject()
        val audio = root.optJSONObject("audioSettings") ?: JSONObject()
        val prayer = root.optJSONObject("prayerConfiguration") ?: JSONObject()
        val locationJson = prayer.optJSONObject("location")
        val location = locationJson?.let {
            PrayerLocation(
                latitude = it.optDouble("latitude").also { value -> require(value.isFinite() && value in -90.0..90.0) },
                longitude = it.optDouble("longitude").also { value -> require(value.isFinite() && value in -180.0..180.0) },
                city = it.optString("city").also { value -> require(value.isNotBlank() && value.length <= 200) },
                region = it.optString("region").take(200),
                country = it.optString("country").take(200),
                timeZoneId = it.optString("timeZoneId").also { value -> require(runCatching { ZoneId.of(value) }.isSuccess) },
                mosqueName = it.optStringOrNull("mosqueName"),
            )
        }
        val settings = prayer.optJSONObject("settings") ?: JSONObject()
        val offsets = settings.optJSONObject("offsets") ?: JSONObject()
        return BackupSettings(
            reader = ReaderSettings(
                fontSizeSp = reader.optDouble("fontSizeSp", ReaderSettings.DEFAULT_FONT_SIZE_SP.toDouble()).toFloat()
                    .coerceIn(ReaderSettings.MIN_FONT_SIZE_SP, ReaderSettings.MAX_FONT_SIZE_SP),
                lineSpacing = reader.optDouble("lineSpacing", ReaderSettings.DEFAULT_LINE_SPACING.toDouble()).toFloat()
                    .coerceIn(ReaderSettings.MIN_LINE_SPACING, ReaderSettings.MAX_LINE_SPACING),
                themeMode = reader.enumOrDefault("themeMode", ThemeMode.SYSTEM),
            ),
            audio = AudioSettings(
                defaultReciterId = audio.optStringOrNull("defaultReciterId"),
                ayahRepeat = audio.enumOrDefault("ayahRepeat", RepeatCount.ONE),
                rangeRepeat = audio.enumOrDefault("rangeRepeat", RepeatCount.ONE),
                repeatDelaySeconds = audio.optInt("repeatDelaySeconds").coerceIn(0, 30),
                streamingQuality = audio.enumOrDefault("streamingQuality", StreamingQuality.HIGH),
                autoDownload = audio.optBoolean("autoDownload"),
                cacheLimitMb = audio.optInt("cacheLimitMb", AudioSettings.DEFAULT_CACHE_LIMIT_MB).coerceIn(64, 4096),
            ),
            prayer = PrayerConfiguration(
                location = location,
                settings = PrayerSettings(
                    calculationMethod = settings.enumOrDefault("calculationMethod", PrayerCalculationMethod.MUSLIM_WORLD_LEAGUE),
                    asrMadhab = settings.enumOrDefault("asrMadhab", AsrMadhab.SHAFI),
                    offsets = PrayerOffsets(
                        offsets.optInt("fajrMinutes").coerceIn(-60, 60),
                        offsets.optInt("sunriseMinutes").coerceIn(-60, 60),
                        offsets.optInt("dhuhrMinutes").coerceIn(-60, 60),
                        offsets.optInt("asrMinutes").coerceIn(-60, 60),
                        offsets.optInt("maghribMinutes").coerceIn(-60, 60),
                        offsets.optInt("ishaMinutes").coerceIn(-60, 60),
                    ),
                    adhanSound = settings.enumOrDefault("adhanSound", AdhanSound.NOTIFICATION_ONLY),
                    silentModeBehavior = settings.enumOrDefault("silentModeBehavior", SilentModeBehavior.RESPECT_SILENT),
                    reminderMinutesBefore = settings.optInt("reminderMinutesBefore").coerceIn(0, 60),
                    notificationsEnabled = settings.optBoolean("notificationsEnabled"),
                    adhanEnabled = settings.optBoolean("adhanEnabled"),
                ),
            ),
        )
    }

    private fun readerSettingsJson(settings: ReaderSettings) = JSONObject()
        .put("fontSizeSp", settings.fontSizeSp).put("lineSpacing", settings.lineSpacing).put("themeMode", settings.themeMode.name)

    private fun audioSettingsJson(settings: AudioSettings) = JSONObject()
        .put("defaultReciterId", settings.defaultReciterId ?: JSONObject.NULL)
        .put("ayahRepeat", settings.ayahRepeat.name).put("rangeRepeat", settings.rangeRepeat.name)
        .put("repeatDelaySeconds", settings.repeatDelaySeconds).put("streamingQuality", settings.streamingQuality.name)
        .put("autoDownload", settings.autoDownload).put("cacheLimitMb", settings.cacheLimitMb)

    private fun prayerConfigurationJson(configuration: PrayerConfiguration) = JSONObject()
        .put("location", configuration.location?.let(::locationJson) ?: JSONObject.NULL)
        .put("settings", prayerSettingsJson(configuration.settings))

    private fun locationJson(location: PrayerLocation) = JSONObject()
        .put("latitude", location.latitude).put("longitude", location.longitude).put("city", location.city)
        .put("region", location.region).put("country", location.country).put("timeZoneId", location.timeZoneId)
        .put("mosqueName", location.mosqueName ?: JSONObject.NULL)

    private fun prayerSettingsJson(settings: PrayerSettings) = JSONObject()
        .put("calculationMethod", settings.calculationMethod.name).put("asrMadhab", settings.asrMadhab.name)
        .put("adhanSound", settings.adhanSound.name).put("silentModeBehavior", settings.silentModeBehavior.name)
        .put("reminderMinutesBefore", settings.reminderMinutesBefore).put("notificationsEnabled", settings.notificationsEnabled)
        .put("adhanEnabled", settings.adhanEnabled)
        .put("offsets", JSONObject().put("fajrMinutes", settings.offsets.fajrMinutes).put("sunriseMinutes", settings.offsets.sunriseMinutes)
            .put("dhuhrMinutes", settings.offsets.dhuhrMinutes).put("asrMinutes", settings.offsets.asrMinutes)
            .put("maghribMinutes", settings.offsets.maghribMinutes).put("ishaMinutes", settings.offsets.ishaMinutes))

    private fun bookmarkJson(item: BookmarkEntity) = JSONObject()
        .put("id", item.id).put("ayahId", item.ayahId).put("createdAt", item.createdAt).put("note", item.note ?: JSONObject.NULL)

    private fun readingProgressJson(item: ReadingProgressEntity) = JSONObject()
        .put("surahId", item.surahId).put("ayahId", item.ayahId).put("page", item.page).put("updatedAt", item.updatedAt)

    private fun progressJson(item: MemorizationProgressEntity) = JSONObject()
        .put("ayahId", item.ayahId).put("status", item.status).put("scheduleStage", item.scheduleStage)
        .put("dueEpochDay", item.dueEpochDay).put("lastReviewedEpochDay", item.lastReviewedEpochDay ?: JSONObject.NULL)
        .put("reviewCount", item.reviewCount).put("consecutiveHardRatings", item.consecutiveHardRatings).put("isWeak", item.isWeak)

    private fun sessionJson(item: MemorizationSessionEntity) = JSONObject()
        .put("sessionId", item.sessionId).put("surahId", item.surahId).put("startAyah", item.startAyah).put("endAyah", item.endAyah)
        .put("reciterId", item.reciterId).put("repeatCount", item.repeatCount).put("mode", item.mode).put("currentAyah", item.currentAyah)
        .put("createdAt", item.createdAt).put("lastOpenedAt", item.lastOpenedAt).put("completedAt", item.completedAt ?: JSONObject.NULL)

    private fun reviewJson(item: ReviewHistoryEntity) = JSONObject()
        .put("id", item.id).put("ayahId", item.ayahId).put("sessionId", item.sessionId ?: JSONObject.NULL)
        .put("difficulty", item.difficulty).put("reviewedAt", item.reviewedAt).put("dueEpochDay", item.dueEpochDay)

    private fun dailyGoalJson(item: DailyGoalEntity) = JSONObject()
        .put("ayahTarget", item.ayahTarget).put("completedAyahs", item.completedAyahs).put("epochDay", item.epochDay)

    private fun completionJson(item: DailyGoalCompletionEntity) = JSONObject()
        .put("epochDay", item.epochDay).put("ayahId", item.ayahId)

    private fun mosqueJson(item: Mosque) = JSONObject()
        .put("id", item.id).put("name", item.name).put("latitude", item.latitude).put("longitude", item.longitude)
        .put("address", item.address ?: JSONObject.NULL).put("phone", item.phone ?: JSONObject.NULL)
        .put("website", item.website ?: JSONObject.NULL).put("distanceMeters", item.distanceMeters)
        .put("rating", item.rating ?: JSONObject.NULL)

    private fun JSONObject.array(key: String, max: Int): List<JSONObject> {
        val array = optJSONArray(key) ?: JSONArray()
        require(array.length() <= max) { "Backup contains too many $key records" }
        return (0 until array.length()).map { index -> array.optJSONObject(index) ?: error("Invalid $key record") }
    }

    private fun JSONObject.optStringOrNull(key: String): String? =
        if (isNull(key)) null else optString(key).takeIf(String::isNotBlank)

    private fun JSONObject.optNullableLong(key: String): Long? = if (isNull(key)) null else optLong(key)
    private fun JSONObject.optNullableDouble(key: String): Double? = if (isNull(key)) null else optDouble(key)

    private inline fun <reified E : Enum<E>> JSONObject.enumOrDefault(key: String, default: E): E =
        optString(key).takeIf(String::isNotBlank)?.let { runCatching { enumValueOf<E>(it) }.getOrNull() } ?: default

    private fun Int.requireAyahId(): Int = also { require(it in 1..QuranStructure.AYAH_COUNT) }

    private data class BackupSettings(
        val reader: ReaderSettings,
        val audio: AudioSettings,
        val prayer: PrayerConfiguration,
    )

    companion object {
        const val FORMAT = "quoren-user-backup"
        const val VERSION = 1
        private const val MAX_BACKUP_CHARS = 5_000_000
        private const val MAX_BOOKMARKS = QuranStructure.AYAH_COUNT
        private const val MAX_MEMORIZATION_ITEMS = QuranStructure.AYAH_COUNT
        private const val MAX_SESSIONS = 10_000
        private const val MAX_REVIEWS = 100_000
        private const val MAX_FAVORITES = 2_000
    }
}