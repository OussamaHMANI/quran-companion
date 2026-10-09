package com.quoren.app.data.local;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class QuranDatabase_Impl extends QuranDatabase {
  private volatile QuranDao _quranDao;

  private volatile QuranImportDao _quranImportDao;

  private volatile BookmarkDao _bookmarkDao;

  private volatile ReadingProgressDao _readingProgressDao;

  private volatile DownloadDao _downloadDao;

  private volatile MemorizationDao _memorizationDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(3) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `ayah` (`id` INTEGER NOT NULL, `surahId` INTEGER NOT NULL, `ayahNumber` INTEGER NOT NULL, `textUthmani` TEXT NOT NULL, `page` INTEGER NOT NULL, `juz` INTEGER NOT NULL, `hizb` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_ayah_page` ON `ayah` (`page`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_ayah_juz` ON `ayah` (`juz`)");
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_ayah_surahId_ayahNumber` ON `ayah` (`surahId`, `ayahNumber`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `ayah_search` (`ayahId` INTEGER NOT NULL, `normalized` TEXT NOT NULL, PRIMARY KEY(`ayahId`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `surah` (`id` INTEGER NOT NULL, `nameArabic` TEXT NOT NULL, `nameTransliteration` TEXT NOT NULL, `nameEnglish` TEXT NOT NULL, `numberOfAyahs` INTEGER NOT NULL, `revelationType` TEXT NOT NULL, `firstPage` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `dataset_meta` (`key` TEXT NOT NULL, `value` TEXT NOT NULL, PRIMARY KEY(`key`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `bookmark` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `ayahId` INTEGER NOT NULL, `createdAt` INTEGER NOT NULL, `note` TEXT)");
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_bookmark_ayahId` ON `bookmark` (`ayahId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `reading_progress` (`id` INTEGER NOT NULL, `surahId` INTEGER NOT NULL, `ayahId` INTEGER NOT NULL, `page` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `download` (`reciterId` TEXT NOT NULL, `surahId` INTEGER NOT NULL, `status` TEXT NOT NULL, `downloadedAyahs` INTEGER NOT NULL, `totalAyahs` INTEGER NOT NULL, `totalBytes` INTEGER NOT NULL, `errorCode` TEXT, `updatedAt` INTEGER NOT NULL, PRIMARY KEY(`reciterId`, `surahId`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `memorization_progress` (`ayahId` INTEGER NOT NULL, `status` TEXT NOT NULL, `scheduleStage` INTEGER NOT NULL, `dueEpochDay` INTEGER NOT NULL, `lastReviewedEpochDay` INTEGER, `reviewCount` INTEGER NOT NULL, `consecutiveHardRatings` INTEGER NOT NULL, `isWeak` INTEGER NOT NULL, PRIMARY KEY(`ayahId`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `memorization_session` (`sessionId` TEXT NOT NULL, `surahId` INTEGER NOT NULL, `startAyah` INTEGER NOT NULL, `endAyah` INTEGER NOT NULL, `reciterId` TEXT NOT NULL, `repeatCount` TEXT NOT NULL, `mode` TEXT NOT NULL, `currentAyah` INTEGER NOT NULL, `createdAt` INTEGER NOT NULL, `lastOpenedAt` INTEGER NOT NULL, `completedAt` INTEGER, PRIMARY KEY(`sessionId`))");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_memorization_session_lastOpenedAt` ON `memorization_session` (`lastOpenedAt`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `review_history` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `ayahId` INTEGER NOT NULL, `sessionId` TEXT, `difficulty` TEXT NOT NULL, `reviewedAt` INTEGER NOT NULL, `dueEpochDay` INTEGER NOT NULL)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_review_history_ayahId` ON `review_history` (`ayahId`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_review_history_reviewedAt` ON `review_history` (`reviewedAt`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `daily_goal` (`id` INTEGER NOT NULL, `ayahTarget` INTEGER NOT NULL, `completedAyahs` INTEGER NOT NULL, `epochDay` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `daily_goal_completion` (`epochDay` INTEGER NOT NULL, `ayahId` INTEGER NOT NULL, PRIMARY KEY(`epochDay`, `ayahId`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, 'c6dc9ad2a71cf873b9a9b35f9ecc6803')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `ayah`");
        db.execSQL("DROP TABLE IF EXISTS `ayah_search`");
        db.execSQL("DROP TABLE IF EXISTS `surah`");
        db.execSQL("DROP TABLE IF EXISTS `dataset_meta`");
        db.execSQL("DROP TABLE IF EXISTS `bookmark`");
        db.execSQL("DROP TABLE IF EXISTS `reading_progress`");
        db.execSQL("DROP TABLE IF EXISTS `download`");
        db.execSQL("DROP TABLE IF EXISTS `memorization_progress`");
        db.execSQL("DROP TABLE IF EXISTS `memorization_session`");
        db.execSQL("DROP TABLE IF EXISTS `review_history`");
        db.execSQL("DROP TABLE IF EXISTS `daily_goal`");
        db.execSQL("DROP TABLE IF EXISTS `daily_goal_completion`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsAyah = new HashMap<String, TableInfo.Column>(7);
        _columnsAyah.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAyah.put("surahId", new TableInfo.Column("surahId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAyah.put("ayahNumber", new TableInfo.Column("ayahNumber", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAyah.put("textUthmani", new TableInfo.Column("textUthmani", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAyah.put("page", new TableInfo.Column("page", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAyah.put("juz", new TableInfo.Column("juz", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAyah.put("hizb", new TableInfo.Column("hizb", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysAyah = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesAyah = new HashSet<TableInfo.Index>(3);
        _indicesAyah.add(new TableInfo.Index("index_ayah_page", false, Arrays.asList("page"), Arrays.asList("ASC")));
        _indicesAyah.add(new TableInfo.Index("index_ayah_juz", false, Arrays.asList("juz"), Arrays.asList("ASC")));
        _indicesAyah.add(new TableInfo.Index("index_ayah_surahId_ayahNumber", true, Arrays.asList("surahId", "ayahNumber"), Arrays.asList("ASC", "ASC")));
        final TableInfo _infoAyah = new TableInfo("ayah", _columnsAyah, _foreignKeysAyah, _indicesAyah);
        final TableInfo _existingAyah = TableInfo.read(db, "ayah");
        if (!_infoAyah.equals(_existingAyah)) {
          return new RoomOpenHelper.ValidationResult(false, "ayah(com.quoren.app.data.local.AyahEntity).\n"
                  + " Expected:\n" + _infoAyah + "\n"
                  + " Found:\n" + _existingAyah);
        }
        final HashMap<String, TableInfo.Column> _columnsAyahSearch = new HashMap<String, TableInfo.Column>(2);
        _columnsAyahSearch.put("ayahId", new TableInfo.Column("ayahId", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAyahSearch.put("normalized", new TableInfo.Column("normalized", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysAyahSearch = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesAyahSearch = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoAyahSearch = new TableInfo("ayah_search", _columnsAyahSearch, _foreignKeysAyahSearch, _indicesAyahSearch);
        final TableInfo _existingAyahSearch = TableInfo.read(db, "ayah_search");
        if (!_infoAyahSearch.equals(_existingAyahSearch)) {
          return new RoomOpenHelper.ValidationResult(false, "ayah_search(com.quoren.app.data.local.AyahSearchEntity).\n"
                  + " Expected:\n" + _infoAyahSearch + "\n"
                  + " Found:\n" + _existingAyahSearch);
        }
        final HashMap<String, TableInfo.Column> _columnsSurah = new HashMap<String, TableInfo.Column>(7);
        _columnsSurah.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSurah.put("nameArabic", new TableInfo.Column("nameArabic", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSurah.put("nameTransliteration", new TableInfo.Column("nameTransliteration", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSurah.put("nameEnglish", new TableInfo.Column("nameEnglish", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSurah.put("numberOfAyahs", new TableInfo.Column("numberOfAyahs", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSurah.put("revelationType", new TableInfo.Column("revelationType", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSurah.put("firstPage", new TableInfo.Column("firstPage", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysSurah = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesSurah = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoSurah = new TableInfo("surah", _columnsSurah, _foreignKeysSurah, _indicesSurah);
        final TableInfo _existingSurah = TableInfo.read(db, "surah");
        if (!_infoSurah.equals(_existingSurah)) {
          return new RoomOpenHelper.ValidationResult(false, "surah(com.quoren.app.data.local.SurahEntity).\n"
                  + " Expected:\n" + _infoSurah + "\n"
                  + " Found:\n" + _existingSurah);
        }
        final HashMap<String, TableInfo.Column> _columnsDatasetMeta = new HashMap<String, TableInfo.Column>(2);
        _columnsDatasetMeta.put("key", new TableInfo.Column("key", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDatasetMeta.put("value", new TableInfo.Column("value", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysDatasetMeta = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesDatasetMeta = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoDatasetMeta = new TableInfo("dataset_meta", _columnsDatasetMeta, _foreignKeysDatasetMeta, _indicesDatasetMeta);
        final TableInfo _existingDatasetMeta = TableInfo.read(db, "dataset_meta");
        if (!_infoDatasetMeta.equals(_existingDatasetMeta)) {
          return new RoomOpenHelper.ValidationResult(false, "dataset_meta(com.quoren.app.data.local.DatasetMetaEntity).\n"
                  + " Expected:\n" + _infoDatasetMeta + "\n"
                  + " Found:\n" + _existingDatasetMeta);
        }
        final HashMap<String, TableInfo.Column> _columnsBookmark = new HashMap<String, TableInfo.Column>(4);
        _columnsBookmark.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBookmark.put("ayahId", new TableInfo.Column("ayahId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBookmark.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBookmark.put("note", new TableInfo.Column("note", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysBookmark = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesBookmark = new HashSet<TableInfo.Index>(1);
        _indicesBookmark.add(new TableInfo.Index("index_bookmark_ayahId", true, Arrays.asList("ayahId"), Arrays.asList("ASC")));
        final TableInfo _infoBookmark = new TableInfo("bookmark", _columnsBookmark, _foreignKeysBookmark, _indicesBookmark);
        final TableInfo _existingBookmark = TableInfo.read(db, "bookmark");
        if (!_infoBookmark.equals(_existingBookmark)) {
          return new RoomOpenHelper.ValidationResult(false, "bookmark(com.quoren.app.data.local.BookmarkEntity).\n"
                  + " Expected:\n" + _infoBookmark + "\n"
                  + " Found:\n" + _existingBookmark);
        }
        final HashMap<String, TableInfo.Column> _columnsReadingProgress = new HashMap<String, TableInfo.Column>(5);
        _columnsReadingProgress.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReadingProgress.put("surahId", new TableInfo.Column("surahId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReadingProgress.put("ayahId", new TableInfo.Column("ayahId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReadingProgress.put("page", new TableInfo.Column("page", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReadingProgress.put("updatedAt", new TableInfo.Column("updatedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysReadingProgress = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesReadingProgress = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoReadingProgress = new TableInfo("reading_progress", _columnsReadingProgress, _foreignKeysReadingProgress, _indicesReadingProgress);
        final TableInfo _existingReadingProgress = TableInfo.read(db, "reading_progress");
        if (!_infoReadingProgress.equals(_existingReadingProgress)) {
          return new RoomOpenHelper.ValidationResult(false, "reading_progress(com.quoren.app.data.local.ReadingProgressEntity).\n"
                  + " Expected:\n" + _infoReadingProgress + "\n"
                  + " Found:\n" + _existingReadingProgress);
        }
        final HashMap<String, TableInfo.Column> _columnsDownload = new HashMap<String, TableInfo.Column>(8);
        _columnsDownload.put("reciterId", new TableInfo.Column("reciterId", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDownload.put("surahId", new TableInfo.Column("surahId", "INTEGER", true, 2, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDownload.put("status", new TableInfo.Column("status", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDownload.put("downloadedAyahs", new TableInfo.Column("downloadedAyahs", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDownload.put("totalAyahs", new TableInfo.Column("totalAyahs", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDownload.put("totalBytes", new TableInfo.Column("totalBytes", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDownload.put("errorCode", new TableInfo.Column("errorCode", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDownload.put("updatedAt", new TableInfo.Column("updatedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysDownload = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesDownload = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoDownload = new TableInfo("download", _columnsDownload, _foreignKeysDownload, _indicesDownload);
        final TableInfo _existingDownload = TableInfo.read(db, "download");
        if (!_infoDownload.equals(_existingDownload)) {
          return new RoomOpenHelper.ValidationResult(false, "download(com.quoren.app.data.local.DownloadEntity).\n"
                  + " Expected:\n" + _infoDownload + "\n"
                  + " Found:\n" + _existingDownload);
        }
        final HashMap<String, TableInfo.Column> _columnsMemorizationProgress = new HashMap<String, TableInfo.Column>(8);
        _columnsMemorizationProgress.put("ayahId", new TableInfo.Column("ayahId", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMemorizationProgress.put("status", new TableInfo.Column("status", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMemorizationProgress.put("scheduleStage", new TableInfo.Column("scheduleStage", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMemorizationProgress.put("dueEpochDay", new TableInfo.Column("dueEpochDay", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMemorizationProgress.put("lastReviewedEpochDay", new TableInfo.Column("lastReviewedEpochDay", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMemorizationProgress.put("reviewCount", new TableInfo.Column("reviewCount", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMemorizationProgress.put("consecutiveHardRatings", new TableInfo.Column("consecutiveHardRatings", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMemorizationProgress.put("isWeak", new TableInfo.Column("isWeak", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysMemorizationProgress = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesMemorizationProgress = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoMemorizationProgress = new TableInfo("memorization_progress", _columnsMemorizationProgress, _foreignKeysMemorizationProgress, _indicesMemorizationProgress);
        final TableInfo _existingMemorizationProgress = TableInfo.read(db, "memorization_progress");
        if (!_infoMemorizationProgress.equals(_existingMemorizationProgress)) {
          return new RoomOpenHelper.ValidationResult(false, "memorization_progress(com.quoren.app.data.local.MemorizationProgressEntity).\n"
                  + " Expected:\n" + _infoMemorizationProgress + "\n"
                  + " Found:\n" + _existingMemorizationProgress);
        }
        final HashMap<String, TableInfo.Column> _columnsMemorizationSession = new HashMap<String, TableInfo.Column>(11);
        _columnsMemorizationSession.put("sessionId", new TableInfo.Column("sessionId", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMemorizationSession.put("surahId", new TableInfo.Column("surahId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMemorizationSession.put("startAyah", new TableInfo.Column("startAyah", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMemorizationSession.put("endAyah", new TableInfo.Column("endAyah", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMemorizationSession.put("reciterId", new TableInfo.Column("reciterId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMemorizationSession.put("repeatCount", new TableInfo.Column("repeatCount", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMemorizationSession.put("mode", new TableInfo.Column("mode", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMemorizationSession.put("currentAyah", new TableInfo.Column("currentAyah", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMemorizationSession.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMemorizationSession.put("lastOpenedAt", new TableInfo.Column("lastOpenedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMemorizationSession.put("completedAt", new TableInfo.Column("completedAt", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysMemorizationSession = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesMemorizationSession = new HashSet<TableInfo.Index>(1);
        _indicesMemorizationSession.add(new TableInfo.Index("index_memorization_session_lastOpenedAt", false, Arrays.asList("lastOpenedAt"), Arrays.asList("ASC")));
        final TableInfo _infoMemorizationSession = new TableInfo("memorization_session", _columnsMemorizationSession, _foreignKeysMemorizationSession, _indicesMemorizationSession);
        final TableInfo _existingMemorizationSession = TableInfo.read(db, "memorization_session");
        if (!_infoMemorizationSession.equals(_existingMemorizationSession)) {
          return new RoomOpenHelper.ValidationResult(false, "memorization_session(com.quoren.app.data.local.MemorizationSessionEntity).\n"
                  + " Expected:\n" + _infoMemorizationSession + "\n"
                  + " Found:\n" + _existingMemorizationSession);
        }
        final HashMap<String, TableInfo.Column> _columnsReviewHistory = new HashMap<String, TableInfo.Column>(6);
        _columnsReviewHistory.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReviewHistory.put("ayahId", new TableInfo.Column("ayahId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReviewHistory.put("sessionId", new TableInfo.Column("sessionId", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReviewHistory.put("difficulty", new TableInfo.Column("difficulty", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReviewHistory.put("reviewedAt", new TableInfo.Column("reviewedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReviewHistory.put("dueEpochDay", new TableInfo.Column("dueEpochDay", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysReviewHistory = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesReviewHistory = new HashSet<TableInfo.Index>(2);
        _indicesReviewHistory.add(new TableInfo.Index("index_review_history_ayahId", false, Arrays.asList("ayahId"), Arrays.asList("ASC")));
        _indicesReviewHistory.add(new TableInfo.Index("index_review_history_reviewedAt", false, Arrays.asList("reviewedAt"), Arrays.asList("ASC")));
        final TableInfo _infoReviewHistory = new TableInfo("review_history", _columnsReviewHistory, _foreignKeysReviewHistory, _indicesReviewHistory);
        final TableInfo _existingReviewHistory = TableInfo.read(db, "review_history");
        if (!_infoReviewHistory.equals(_existingReviewHistory)) {
          return new RoomOpenHelper.ValidationResult(false, "review_history(com.quoren.app.data.local.ReviewHistoryEntity).\n"
                  + " Expected:\n" + _infoReviewHistory + "\n"
                  + " Found:\n" + _existingReviewHistory);
        }
        final HashMap<String, TableInfo.Column> _columnsDailyGoal = new HashMap<String, TableInfo.Column>(4);
        _columnsDailyGoal.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyGoal.put("ayahTarget", new TableInfo.Column("ayahTarget", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyGoal.put("completedAyahs", new TableInfo.Column("completedAyahs", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyGoal.put("epochDay", new TableInfo.Column("epochDay", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysDailyGoal = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesDailyGoal = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoDailyGoal = new TableInfo("daily_goal", _columnsDailyGoal, _foreignKeysDailyGoal, _indicesDailyGoal);
        final TableInfo _existingDailyGoal = TableInfo.read(db, "daily_goal");
        if (!_infoDailyGoal.equals(_existingDailyGoal)) {
          return new RoomOpenHelper.ValidationResult(false, "daily_goal(com.quoren.app.data.local.DailyGoalEntity).\n"
                  + " Expected:\n" + _infoDailyGoal + "\n"
                  + " Found:\n" + _existingDailyGoal);
        }
        final HashMap<String, TableInfo.Column> _columnsDailyGoalCompletion = new HashMap<String, TableInfo.Column>(2);
        _columnsDailyGoalCompletion.put("epochDay", new TableInfo.Column("epochDay", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyGoalCompletion.put("ayahId", new TableInfo.Column("ayahId", "INTEGER", true, 2, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysDailyGoalCompletion = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesDailyGoalCompletion = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoDailyGoalCompletion = new TableInfo("daily_goal_completion", _columnsDailyGoalCompletion, _foreignKeysDailyGoalCompletion, _indicesDailyGoalCompletion);
        final TableInfo _existingDailyGoalCompletion = TableInfo.read(db, "daily_goal_completion");
        if (!_infoDailyGoalCompletion.equals(_existingDailyGoalCompletion)) {
          return new RoomOpenHelper.ValidationResult(false, "daily_goal_completion(com.quoren.app.data.local.DailyGoalCompletionEntity).\n"
                  + " Expected:\n" + _infoDailyGoalCompletion + "\n"
                  + " Found:\n" + _existingDailyGoalCompletion);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "c6dc9ad2a71cf873b9a9b35f9ecc6803", "60df540de4a4e0ecbac9f44372da5262");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "ayah","ayah_search","surah","dataset_meta","bookmark","reading_progress","download","memorization_progress","memorization_session","review_history","daily_goal","daily_goal_completion");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    try {
      super.beginTransaction();
      _db.execSQL("DELETE FROM `ayah`");
      _db.execSQL("DELETE FROM `ayah_search`");
      _db.execSQL("DELETE FROM `surah`");
      _db.execSQL("DELETE FROM `dataset_meta`");
      _db.execSQL("DELETE FROM `bookmark`");
      _db.execSQL("DELETE FROM `reading_progress`");
      _db.execSQL("DELETE FROM `download`");
      _db.execSQL("DELETE FROM `memorization_progress`");
      _db.execSQL("DELETE FROM `memorization_session`");
      _db.execSQL("DELETE FROM `review_history`");
      _db.execSQL("DELETE FROM `daily_goal`");
      _db.execSQL("DELETE FROM `daily_goal_completion`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(QuranDao.class, QuranDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(QuranImportDao.class, QuranImportDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(BookmarkDao.class, BookmarkDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(ReadingProgressDao.class, ReadingProgressDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(DownloadDao.class, DownloadDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(MemorizationDao.class, MemorizationDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public QuranDao quranDao() {
    if (_quranDao != null) {
      return _quranDao;
    } else {
      synchronized(this) {
        if(_quranDao == null) {
          _quranDao = new QuranDao_Impl(this);
        }
        return _quranDao;
      }
    }
  }

  @Override
  public QuranImportDao importDao() {
    if (_quranImportDao != null) {
      return _quranImportDao;
    } else {
      synchronized(this) {
        if(_quranImportDao == null) {
          _quranImportDao = new QuranImportDao_Impl(this);
        }
        return _quranImportDao;
      }
    }
  }

  @Override
  public BookmarkDao bookmarkDao() {
    if (_bookmarkDao != null) {
      return _bookmarkDao;
    } else {
      synchronized(this) {
        if(_bookmarkDao == null) {
          _bookmarkDao = new BookmarkDao_Impl(this);
        }
        return _bookmarkDao;
      }
    }
  }

  @Override
  public ReadingProgressDao progressDao() {
    if (_readingProgressDao != null) {
      return _readingProgressDao;
    } else {
      synchronized(this) {
        if(_readingProgressDao == null) {
          _readingProgressDao = new ReadingProgressDao_Impl(this);
        }
        return _readingProgressDao;
      }
    }
  }

  @Override
  public DownloadDao downloadDao() {
    if (_downloadDao != null) {
      return _downloadDao;
    } else {
      synchronized(this) {
        if(_downloadDao == null) {
          _downloadDao = new DownloadDao_Impl(this);
        }
        return _downloadDao;
      }
    }
  }

  @Override
  public MemorizationDao memorizationDao() {
    if (_memorizationDao != null) {
      return _memorizationDao;
    } else {
      synchronized(this) {
        if(_memorizationDao == null) {
          _memorizationDao = new MemorizationDao_Impl(this);
        }
        return _memorizationDao;
      }
    }
  }
}
