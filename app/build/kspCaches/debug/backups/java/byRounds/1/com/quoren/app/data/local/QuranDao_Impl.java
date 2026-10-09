package com.quoren.app.data.local;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.coroutines.Continuation;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class QuranDao_Impl implements QuranDao {
  private final RoomDatabase __db;

  public QuranDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
  }

  @Override
  public Object surahs(final Continuation<? super List<SurahEntity>> $completion) {
    final String _sql = "SELECT * FROM surah ORDER BY id";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<SurahEntity>>() {
      @Override
      @NonNull
      public List<SurahEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfNameArabic = CursorUtil.getColumnIndexOrThrow(_cursor, "nameArabic");
          final int _cursorIndexOfNameTransliteration = CursorUtil.getColumnIndexOrThrow(_cursor, "nameTransliteration");
          final int _cursorIndexOfNameEnglish = CursorUtil.getColumnIndexOrThrow(_cursor, "nameEnglish");
          final int _cursorIndexOfNumberOfAyahs = CursorUtil.getColumnIndexOrThrow(_cursor, "numberOfAyahs");
          final int _cursorIndexOfRevelationType = CursorUtil.getColumnIndexOrThrow(_cursor, "revelationType");
          final int _cursorIndexOfFirstPage = CursorUtil.getColumnIndexOrThrow(_cursor, "firstPage");
          final List<SurahEntity> _result = new ArrayList<SurahEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final SurahEntity _item;
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final String _tmpNameArabic;
            _tmpNameArabic = _cursor.getString(_cursorIndexOfNameArabic);
            final String _tmpNameTransliteration;
            _tmpNameTransliteration = _cursor.getString(_cursorIndexOfNameTransliteration);
            final String _tmpNameEnglish;
            _tmpNameEnglish = _cursor.getString(_cursorIndexOfNameEnglish);
            final int _tmpNumberOfAyahs;
            _tmpNumberOfAyahs = _cursor.getInt(_cursorIndexOfNumberOfAyahs);
            final String _tmpRevelationType;
            _tmpRevelationType = _cursor.getString(_cursorIndexOfRevelationType);
            final int _tmpFirstPage;
            _tmpFirstPage = _cursor.getInt(_cursorIndexOfFirstPage);
            _item = new SurahEntity(_tmpId,_tmpNameArabic,_tmpNameTransliteration,_tmpNameEnglish,_tmpNumberOfAyahs,_tmpRevelationType,_tmpFirstPage);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object ayahsByPage(final int page,
      final Continuation<? super List<AyahEntity>> $completion) {
    final String _sql = "SELECT * FROM ayah WHERE page = ? ORDER BY id";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, page);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<AyahEntity>>() {
      @Override
      @NonNull
      public List<AyahEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfSurahId = CursorUtil.getColumnIndexOrThrow(_cursor, "surahId");
          final int _cursorIndexOfAyahNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "ayahNumber");
          final int _cursorIndexOfTextUthmani = CursorUtil.getColumnIndexOrThrow(_cursor, "textUthmani");
          final int _cursorIndexOfPage = CursorUtil.getColumnIndexOrThrow(_cursor, "page");
          final int _cursorIndexOfJuz = CursorUtil.getColumnIndexOrThrow(_cursor, "juz");
          final int _cursorIndexOfHizb = CursorUtil.getColumnIndexOrThrow(_cursor, "hizb");
          final List<AyahEntity> _result = new ArrayList<AyahEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final AyahEntity _item;
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final int _tmpSurahId;
            _tmpSurahId = _cursor.getInt(_cursorIndexOfSurahId);
            final int _tmpAyahNumber;
            _tmpAyahNumber = _cursor.getInt(_cursorIndexOfAyahNumber);
            final String _tmpTextUthmani;
            _tmpTextUthmani = _cursor.getString(_cursorIndexOfTextUthmani);
            final int _tmpPage;
            _tmpPage = _cursor.getInt(_cursorIndexOfPage);
            final int _tmpJuz;
            _tmpJuz = _cursor.getInt(_cursorIndexOfJuz);
            final int _tmpHizb;
            _tmpHizb = _cursor.getInt(_cursorIndexOfHizb);
            _item = new AyahEntity(_tmpId,_tmpSurahId,_tmpAyahNumber,_tmpTextUthmani,_tmpPage,_tmpJuz,_tmpHizb);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object ayah(final int id, final Continuation<? super AyahEntity> $completion) {
    final String _sql = "SELECT * FROM ayah WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<AyahEntity>() {
      @Override
      @Nullable
      public AyahEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfSurahId = CursorUtil.getColumnIndexOrThrow(_cursor, "surahId");
          final int _cursorIndexOfAyahNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "ayahNumber");
          final int _cursorIndexOfTextUthmani = CursorUtil.getColumnIndexOrThrow(_cursor, "textUthmani");
          final int _cursorIndexOfPage = CursorUtil.getColumnIndexOrThrow(_cursor, "page");
          final int _cursorIndexOfJuz = CursorUtil.getColumnIndexOrThrow(_cursor, "juz");
          final int _cursorIndexOfHizb = CursorUtil.getColumnIndexOrThrow(_cursor, "hizb");
          final AyahEntity _result;
          if (_cursor.moveToFirst()) {
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final int _tmpSurahId;
            _tmpSurahId = _cursor.getInt(_cursorIndexOfSurahId);
            final int _tmpAyahNumber;
            _tmpAyahNumber = _cursor.getInt(_cursorIndexOfAyahNumber);
            final String _tmpTextUthmani;
            _tmpTextUthmani = _cursor.getString(_cursorIndexOfTextUthmani);
            final int _tmpPage;
            _tmpPage = _cursor.getInt(_cursorIndexOfPage);
            final int _tmpJuz;
            _tmpJuz = _cursor.getInt(_cursorIndexOfJuz);
            final int _tmpHizb;
            _tmpHizb = _cursor.getInt(_cursorIndexOfHizb);
            _result = new AyahEntity(_tmpId,_tmpSurahId,_tmpAyahNumber,_tmpTextUthmani,_tmpPage,_tmpJuz,_tmpHizb);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object juzStarts(final Continuation<? super List<AyahEntity>> $completion) {
    final String _sql = "SELECT * FROM ayah WHERE id IN (SELECT MIN(id) FROM ayah GROUP BY juz) ORDER BY juz";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<AyahEntity>>() {
      @Override
      @NonNull
      public List<AyahEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfSurahId = CursorUtil.getColumnIndexOrThrow(_cursor, "surahId");
          final int _cursorIndexOfAyahNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "ayahNumber");
          final int _cursorIndexOfTextUthmani = CursorUtil.getColumnIndexOrThrow(_cursor, "textUthmani");
          final int _cursorIndexOfPage = CursorUtil.getColumnIndexOrThrow(_cursor, "page");
          final int _cursorIndexOfJuz = CursorUtil.getColumnIndexOrThrow(_cursor, "juz");
          final int _cursorIndexOfHizb = CursorUtil.getColumnIndexOrThrow(_cursor, "hizb");
          final List<AyahEntity> _result = new ArrayList<AyahEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final AyahEntity _item;
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final int _tmpSurahId;
            _tmpSurahId = _cursor.getInt(_cursorIndexOfSurahId);
            final int _tmpAyahNumber;
            _tmpAyahNumber = _cursor.getInt(_cursorIndexOfAyahNumber);
            final String _tmpTextUthmani;
            _tmpTextUthmani = _cursor.getString(_cursorIndexOfTextUthmani);
            final int _tmpPage;
            _tmpPage = _cursor.getInt(_cursorIndexOfPage);
            final int _tmpJuz;
            _tmpJuz = _cursor.getInt(_cursorIndexOfJuz);
            final int _tmpHizb;
            _tmpHizb = _cursor.getInt(_cursorIndexOfHizb);
            _item = new AyahEntity(_tmpId,_tmpSurahId,_tmpAyahNumber,_tmpTextUthmani,_tmpPage,_tmpJuz,_tmpHizb);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object search(final String query, final int limit,
      final Continuation<? super List<AyahEntity>> $completion) {
    final String _sql = "SELECT a.* FROM ayah a INNER JOIN ayah_search s ON s.ayahId = a.id WHERE s.normalized LIKE '%' || ? || '%' ORDER BY a.id LIMIT ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, query);
    _argIndex = 2;
    _statement.bindLong(_argIndex, limit);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<AyahEntity>>() {
      @Override
      @NonNull
      public List<AyahEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfSurahId = CursorUtil.getColumnIndexOrThrow(_cursor, "surahId");
          final int _cursorIndexOfAyahNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "ayahNumber");
          final int _cursorIndexOfTextUthmani = CursorUtil.getColumnIndexOrThrow(_cursor, "textUthmani");
          final int _cursorIndexOfPage = CursorUtil.getColumnIndexOrThrow(_cursor, "page");
          final int _cursorIndexOfJuz = CursorUtil.getColumnIndexOrThrow(_cursor, "juz");
          final int _cursorIndexOfHizb = CursorUtil.getColumnIndexOrThrow(_cursor, "hizb");
          final List<AyahEntity> _result = new ArrayList<AyahEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final AyahEntity _item;
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final int _tmpSurahId;
            _tmpSurahId = _cursor.getInt(_cursorIndexOfSurahId);
            final int _tmpAyahNumber;
            _tmpAyahNumber = _cursor.getInt(_cursorIndexOfAyahNumber);
            final String _tmpTextUthmani;
            _tmpTextUthmani = _cursor.getString(_cursorIndexOfTextUthmani);
            final int _tmpPage;
            _tmpPage = _cursor.getInt(_cursorIndexOfPage);
            final int _tmpJuz;
            _tmpJuz = _cursor.getInt(_cursorIndexOfJuz);
            final int _tmpHizb;
            _tmpHizb = _cursor.getInt(_cursorIndexOfHizb);
            _item = new AyahEntity(_tmpId,_tmpSurahId,_tmpAyahNumber,_tmpTextUthmani,_tmpPage,_tmpJuz,_tmpHizb);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object ayahCount(final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM ayah";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp;
          } else {
            _result = 0;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object meta(final String key, final Continuation<? super String> $completion) {
    final String _sql = "SELECT value FROM dataset_meta WHERE `key` = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, key);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<String>() {
      @Override
      @Nullable
      public String call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final String _result;
          if (_cursor.moveToFirst()) {
            if (_cursor.isNull(0)) {
              _result = null;
            } else {
              _result = _cursor.getString(0);
            }
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
