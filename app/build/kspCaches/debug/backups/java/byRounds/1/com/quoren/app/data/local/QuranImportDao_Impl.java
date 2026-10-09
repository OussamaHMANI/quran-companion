package com.quoren.app.data.local;

import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.SharedSQLiteStatement;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class QuranImportDao_Impl implements QuranImportDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<AyahEntity> __insertionAdapterOfAyahEntity;

  private final EntityInsertionAdapter<AyahSearchEntity> __insertionAdapterOfAyahSearchEntity;

  private final EntityInsertionAdapter<SurahEntity> __insertionAdapterOfSurahEntity;

  private final EntityInsertionAdapter<DatasetMetaEntity> __insertionAdapterOfDatasetMetaEntity;

  private final SharedSQLiteStatement __preparedStmtOfClearAyahs;

  private final SharedSQLiteStatement __preparedStmtOfClearSearch;

  private final SharedSQLiteStatement __preparedStmtOfClearSurahs;

  private final SharedSQLiteStatement __preparedStmtOfClearMeta;

  public QuranImportDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfAyahEntity = new EntityInsertionAdapter<AyahEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `ayah` (`id`,`surahId`,`ayahNumber`,`textUthmani`,`page`,`juz`,`hizb`) VALUES (?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final AyahEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getSurahId());
        statement.bindLong(3, entity.getAyahNumber());
        statement.bindString(4, entity.getTextUthmani());
        statement.bindLong(5, entity.getPage());
        statement.bindLong(6, entity.getJuz());
        statement.bindLong(7, entity.getHizb());
      }
    };
    this.__insertionAdapterOfAyahSearchEntity = new EntityInsertionAdapter<AyahSearchEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `ayah_search` (`ayahId`,`normalized`) VALUES (?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final AyahSearchEntity entity) {
        statement.bindLong(1, entity.getAyahId());
        statement.bindString(2, entity.getNormalized());
      }
    };
    this.__insertionAdapterOfSurahEntity = new EntityInsertionAdapter<SurahEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `surah` (`id`,`nameArabic`,`nameTransliteration`,`nameEnglish`,`numberOfAyahs`,`revelationType`,`firstPage`) VALUES (?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final SurahEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getNameArabic());
        statement.bindString(3, entity.getNameTransliteration());
        statement.bindString(4, entity.getNameEnglish());
        statement.bindLong(5, entity.getNumberOfAyahs());
        statement.bindString(6, entity.getRevelationType());
        statement.bindLong(7, entity.getFirstPage());
      }
    };
    this.__insertionAdapterOfDatasetMetaEntity = new EntityInsertionAdapter<DatasetMetaEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `dataset_meta` (`key`,`value`) VALUES (?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final DatasetMetaEntity entity) {
        statement.bindString(1, entity.getKey());
        statement.bindString(2, entity.getValue());
      }
    };
    this.__preparedStmtOfClearAyahs = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM ayah";
        return _query;
      }
    };
    this.__preparedStmtOfClearSearch = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM ayah_search";
        return _query;
      }
    };
    this.__preparedStmtOfClearSurahs = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM surah";
        return _query;
      }
    };
    this.__preparedStmtOfClearMeta = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM dataset_meta";
        return _query;
      }
    };
  }

  @Override
  public Object insertAyahs(final List<AyahEntity> items,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfAyahEntity.insert(items);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertSearch(final List<AyahSearchEntity> items,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfAyahSearchEntity.insert(items);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertSurahs(final List<SurahEntity> items,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfSurahEntity.insert(items);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object putMeta(final DatasetMetaEntity item,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfDatasetMetaEntity.insert(item);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object clearAyahs(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfClearAyahs.acquire();
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfClearAyahs.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object clearSearch(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfClearSearch.acquire();
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfClearSearch.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object clearSurahs(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfClearSurahs.acquire();
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfClearSurahs.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object clearMeta(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfClearMeta.acquire();
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfClearMeta.release(_stmt);
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
