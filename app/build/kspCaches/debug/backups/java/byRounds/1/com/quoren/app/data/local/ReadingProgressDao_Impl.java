package com.quoren.app.data.local;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
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
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class ReadingProgressDao_Impl implements ReadingProgressDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<ReadingProgressEntity> __insertionAdapterOfReadingProgressEntity;

  private final SharedSQLiteStatement __preparedStmtOfClearForRestore;

  public ReadingProgressDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfReadingProgressEntity = new EntityInsertionAdapter<ReadingProgressEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `reading_progress` (`id`,`surahId`,`ayahId`,`page`,`updatedAt`) VALUES (?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ReadingProgressEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getSurahId());
        statement.bindLong(3, entity.getAyahId());
        statement.bindLong(4, entity.getPage());
        statement.bindLong(5, entity.getUpdatedAt());
      }
    };
    this.__preparedStmtOfClearForRestore = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM reading_progress";
        return _query;
      }
    };
  }

  @Override
  public Object upsert(final ReadingProgressEntity item,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfReadingProgressEntity.insert(item);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object clearForRestore(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfClearForRestore.acquire();
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
          __preparedStmtOfClearForRestore.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object exportCurrent(final Continuation<? super ReadingProgressEntity> $completion) {
    final String _sql = "SELECT * FROM reading_progress WHERE id = 0";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<ReadingProgressEntity>() {
      @Override
      @Nullable
      public ReadingProgressEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfSurahId = CursorUtil.getColumnIndexOrThrow(_cursor, "surahId");
          final int _cursorIndexOfAyahId = CursorUtil.getColumnIndexOrThrow(_cursor, "ayahId");
          final int _cursorIndexOfPage = CursorUtil.getColumnIndexOrThrow(_cursor, "page");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final ReadingProgressEntity _result;
          if (_cursor.moveToFirst()) {
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final int _tmpSurahId;
            _tmpSurahId = _cursor.getInt(_cursorIndexOfSurahId);
            final int _tmpAyahId;
            _tmpAyahId = _cursor.getInt(_cursorIndexOfAyahId);
            final int _tmpPage;
            _tmpPage = _cursor.getInt(_cursorIndexOfPage);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _result = new ReadingProgressEntity(_tmpId,_tmpSurahId,_tmpAyahId,_tmpPage,_tmpUpdatedAt);
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
  public Flow<ReadingProgressEntity> observe() {
    final String _sql = "SELECT * FROM reading_progress WHERE id = 0";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"reading_progress"}, new Callable<ReadingProgressEntity>() {
      @Override
      @Nullable
      public ReadingProgressEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfSurahId = CursorUtil.getColumnIndexOrThrow(_cursor, "surahId");
          final int _cursorIndexOfAyahId = CursorUtil.getColumnIndexOrThrow(_cursor, "ayahId");
          final int _cursorIndexOfPage = CursorUtil.getColumnIndexOrThrow(_cursor, "page");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final ReadingProgressEntity _result;
          if (_cursor.moveToFirst()) {
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final int _tmpSurahId;
            _tmpSurahId = _cursor.getInt(_cursorIndexOfSurahId);
            final int _tmpAyahId;
            _tmpAyahId = _cursor.getInt(_cursorIndexOfAyahId);
            final int _tmpPage;
            _tmpPage = _cursor.getInt(_cursorIndexOfPage);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _result = new ReadingProgressEntity(_tmpId,_tmpSurahId,_tmpAyahId,_tmpPage,_tmpUpdatedAt);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
