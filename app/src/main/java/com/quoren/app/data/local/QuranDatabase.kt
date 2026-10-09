package com.quoren.app.data.local

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Index
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "download", primaryKeys = ["reciterId", "surahId"])
data class DownloadEntity(
    val reciterId: String,
    val surahId: Int,
    val status: String,
    val downloadedAyahs: Int,
    val totalAyahs: Int,
    val totalBytes: Long,
    val errorCode: String?,
    val updatedAt: Long,
)

@Dao
interface DownloadDao {
    @Query("SELECT * FROM download ORDER BY updatedAt DESC")
    fun observeAll(): Flow<List<DownloadEntity>>

    @Query("SELECT * FROM download WHERE reciterId = :reciterId AND surahId = :surahId")
    suspend fun find(reciterId: String, surahId: Int): DownloadEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: DownloadEntity)

    /** Only touches rows that are still active, so a paused or deleted row is never revived by a late worker write. */
    @Query(
        "UPDATE download SET status = :status, downloadedAyahs = :ayahs, totalBytes = :bytes, errorCode = NULL, " +
            "updatedAt = :now WHERE reciterId = :reciterId AND surahId = :surahId AND status IN ('QUEUED','DOWNLOADING')",
    )
    suspend fun updateProgress(reciterId: String, surahId: Int, status: String, ayahs: Int, bytes: Long, now: Long)

    @Query(
        "UPDATE download SET status = :status, errorCode = :error, updatedAt = :now " +
            "WHERE reciterId = :reciterId AND surahId = :surahId AND status IN ('QUEUED','DOWNLOADING')",
    )
    suspend fun finishActive(reciterId: String, surahId: Int, status: String, error: String?, now: Long)

    @Query("UPDATE download SET status = :status, updatedAt = :now WHERE reciterId = :reciterId AND surahId = :surahId")
    suspend fun setStatus(reciterId: String, surahId: Int, status: String, now: Long)

    @Query("DELETE FROM download WHERE reciterId = :reciterId AND surahId = :surahId")
    suspend fun delete(reciterId: String, surahId: Int)
}

@Entity(
    tableName = "ayah",
    indices = [Index("page"), Index("juz"), Index(value = ["surahId", "ayahNumber"], unique = true)],
)
data class AyahEntity(
    @PrimaryKey val id: Int,
    val surahId: Int,
    val ayahNumber: Int,
    val textUthmani: String,
    val page: Int,
    val juz: Int,
    val hizb: Int,
)

/** Search-only copy. Never displayed. */
@Entity(tableName = "ayah_search")
data class AyahSearchEntity(
    @PrimaryKey val ayahId: Int,
    val normalized: String,
)

@Entity(tableName = "surah")
data class SurahEntity(
    @PrimaryKey val id: Int,
    val nameArabic: String,
    val nameTransliteration: String,
    val nameEnglish: String,
    val numberOfAyahs: Int,
    val revelationType: String,
    val firstPage: Int,
)

@Entity(tableName = "dataset_meta")
data class DatasetMetaEntity(
    @PrimaryKey val key: String,
    val value: String,
)

@Entity(tableName = "bookmark", indices = [Index(value = ["ayahId"], unique = true)])
data class BookmarkEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val ayahId: Int,
    val createdAt: Long,
    val note: String? = null,
)

@Entity(tableName = "reading_progress")
data class ReadingProgressEntity(
    @PrimaryKey val id: Int = 0,
    val surahId: Int,
    val ayahId: Int,
    val page: Int,
    val updatedAt: Long,
)

/** Read-only access to canonical data; deliberately has no write methods. */
@Dao
interface QuranDao {
    @Query("SELECT * FROM surah ORDER BY id")
    suspend fun surahs(): List<SurahEntity>

    @Query("SELECT * FROM ayah WHERE page = :page ORDER BY id")
    suspend fun ayahsByPage(page: Int): List<AyahEntity>

    @Query("SELECT * FROM ayah WHERE id = :id")
    suspend fun ayah(id: Int): AyahEntity?

    @Query("SELECT * FROM ayah WHERE id IN (SELECT MIN(id) FROM ayah GROUP BY juz) ORDER BY juz")
    suspend fun juzStarts(): List<AyahEntity>

    @Query(
        "SELECT a.* FROM ayah a INNER JOIN ayah_search s ON s.ayahId = a.id " +
            "WHERE s.normalized LIKE '%' || :query || '%' ORDER BY a.id LIMIT :limit",
    )
    suspend fun search(query: String, limit: Int): List<AyahEntity>

    @Query("SELECT COUNT(*) FROM ayah")
    suspend fun ayahCount(): Int

    @Query("SELECT value FROM dataset_meta WHERE `key` = :key")
    suspend fun meta(key: String): String?
}

/** Used only by the dataset importer. */
@Dao
interface QuranImportDao {
    @Insert suspend fun insertAyahs(items: List<AyahEntity>)
    @Insert suspend fun insertSearch(items: List<AyahSearchEntity>)
    @Insert suspend fun insertSurahs(items: List<SurahEntity>)
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun putMeta(item: DatasetMetaEntity)
    @Query("DELETE FROM ayah") suspend fun clearAyahs()
    @Query("DELETE FROM ayah_search") suspend fun clearSearch()
    @Query("DELETE FROM surah") suspend fun clearSurahs()
    @Query("DELETE FROM dataset_meta") suspend fun clearMeta()
}

@Dao
interface BookmarkDao {
    @Query("SELECT * FROM bookmark ORDER BY createdAt DESC")
    suspend fun exportAll(): List<BookmarkEntity>

    @Query("SELECT * FROM bookmark ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<BookmarkEntity>>

    @Query("SELECT ayahId FROM bookmark")
    fun observeAyahIds(): Flow<List<Int>>

    @Query("SELECT COUNT(*) FROM bookmark WHERE ayahId = :ayahId")
    suspend fun count(ayahId: Int): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: BookmarkEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun restoreAll(items: List<BookmarkEntity>)

    @Query("DELETE FROM bookmark")
    suspend fun clearForRestore()

    @Query("DELETE FROM bookmark WHERE ayahId = :ayahId")
    suspend fun deleteByAyahId(ayahId: Int)
}

@Dao
interface ReadingProgressDao {
    @Query("SELECT * FROM reading_progress WHERE id = 0")
    suspend fun exportCurrent(): ReadingProgressEntity?

    @Query("DELETE FROM reading_progress")
    suspend fun clearForRestore()

    @Query("SELECT * FROM reading_progress WHERE id = 0")
    fun observe(): Flow<ReadingProgressEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: ReadingProgressEntity)
}

@Database(
    entities = [
        AyahEntity::class,
        AyahSearchEntity::class,
        SurahEntity::class,
        DatasetMetaEntity::class,
        BookmarkEntity::class,
        ReadingProgressEntity::class,
        DownloadEntity::class,
        MemorizationProgressEntity::class,
        MemorizationSessionEntity::class,
        ReviewHistoryEntity::class,
        DailyGoalEntity::class,
        DailyGoalCompletionEntity::class,
    ],
    version = 3,
    exportSchema = false,
)
abstract class QuranDatabase : RoomDatabase() {
    abstract fun quranDao(): QuranDao
    abstract fun importDao(): QuranImportDao
    abstract fun bookmarkDao(): BookmarkDao
    abstract fun progressDao(): ReadingProgressDao
    abstract fun downloadDao(): DownloadDao
    abstract fun memorizationDao(): MemorizationDao
}

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS `download` (`reciterId` TEXT NOT NULL, `surahId` INTEGER NOT NULL, " +
                "`status` TEXT NOT NULL, `downloadedAyahs` INTEGER NOT NULL, `totalAyahs` INTEGER NOT NULL, " +
                "`totalBytes` INTEGER NOT NULL, `errorCode` TEXT, `updatedAt` INTEGER NOT NULL, " +
                "PRIMARY KEY(`reciterId`, `surahId`))",
        )
    }
}

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS `memorization_progress` (" +
                "`ayahId` INTEGER NOT NULL, `status` TEXT NOT NULL, `scheduleStage` INTEGER NOT NULL, " +
                "`dueEpochDay` INTEGER NOT NULL, `lastReviewedEpochDay` INTEGER, `reviewCount` INTEGER NOT NULL, " +
                "`consecutiveHardRatings` INTEGER NOT NULL, `isWeak` INTEGER NOT NULL, PRIMARY KEY(`ayahId`))",
        )
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS `memorization_session` (" +
                "`sessionId` TEXT NOT NULL, `surahId` INTEGER NOT NULL, `startAyah` INTEGER NOT NULL, " +
                "`endAyah` INTEGER NOT NULL, `reciterId` TEXT NOT NULL, `repeatCount` TEXT NOT NULL, `mode` TEXT NOT NULL, " +
                "`currentAyah` INTEGER NOT NULL, `createdAt` INTEGER NOT NULL, `lastOpenedAt` INTEGER NOT NULL, `completedAt` INTEGER, " +
                "PRIMARY KEY(`sessionId`))",
        )
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_memorization_session_lastOpenedAt` ON `memorization_session` (`lastOpenedAt`)")
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS `review_history` (" +
                "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `ayahId` INTEGER NOT NULL, `sessionId` TEXT, " +
                "`difficulty` TEXT NOT NULL, `reviewedAt` INTEGER NOT NULL, `dueEpochDay` INTEGER NOT NULL)",
        )
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_review_history_ayahId` ON `review_history` (`ayahId`)")
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_review_history_reviewedAt` ON `review_history` (`reviewedAt`)")
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS `daily_goal` (`id` INTEGER NOT NULL, `ayahTarget` INTEGER NOT NULL, " +
                "`completedAyahs` INTEGER NOT NULL, `epochDay` INTEGER NOT NULL, PRIMARY KEY(`id`))",
        )
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS `daily_goal_completion` (`epochDay` INTEGER NOT NULL, `ayahId` INTEGER NOT NULL, " +
                "PRIMARY KEY(`epochDay`, `ayahId`))",
        )
    }
}
