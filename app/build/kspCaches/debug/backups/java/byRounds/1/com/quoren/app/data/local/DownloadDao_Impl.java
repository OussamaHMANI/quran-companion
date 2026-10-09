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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class DownloadDao_Impl implements DownloadDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<DownloadEntity> __insertionAdapterOfDownloadEntity;

  private final SharedSQLiteStatement __preparedStmtOfUpdateProgress;

  private final SharedSQLiteStatement __preparedStmtOfFinishActive;

  private final SharedSQLiteStatement __preparedStmtOfSetStatus;

  private final SharedSQLiteStatement __preparedStmtOfDelete;

  public DownloadDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfDownloadEntity = new EntityInsertionAdapter<DownloadEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `download` (`reciterId`,`surahId`,`status`,`downloadedAyahs`,`totalAyahs`,`totalBytes`,`errorCode`,`updatedAt`) VALUES (?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final DownloadEntity entity) {
        statement.bindString(1, entity.getReciterId());
        statement.bindLong(2, entity.getSurahId());
        statement.bindString(3, entity.getStatus());
        statement.bindLong(4, entity.getDownloadedAyahs());
        statement.bindLong(5, entity.getTotalAyahs());
        statement.bindLong(6, entity.getTotalBytes());
        if (entity.getErrorCode() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getErrorCode());
        }
        statement.bindLong(8, entity.getUpdatedAt());
      }
    };
    this.__preparedStmtOfUpdateProgress = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE download SET status = ?, downloadedAyahs = ?, totalBytes = ?, errorCode = NULL, updatedAt = ? WHERE reciterId = ? AND surahId = ? AND status IN ('QUEUED','DOWNLOADING')";
        return _query;
      }
    };
    this.__preparedStmtOfFinishActive = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE download SET status = ?, errorCode = ?, updatedAt = ? WHERE reciterId = ? AND surahId = ? AND status IN ('QUEUED','DOWNLOADING')";
        return _query;
      }
    };
    this.__preparedStmtOfSetStatus = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE download SET status = ?, updatedAt = ? WHERE reciterId = ? AND surahId = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDelete = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM download WHERE reciterId = ? AND surahId = ?";
        return _query;
      }
    };
  }

  @Override
  public Object upsert(final DownloadEntity item, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfDownloadEntity.insert(item);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateProgress(final String reciterId, final int surahId, final String status,
      final int ayahs, final long bytes, final long now,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateProgress.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, status);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, ayahs);
        _argIndex = 3;
        _stmt.bindLong(_argIndex, bytes);
        _argIndex = 4;
        _stmt.bindLong(_argIndex, now);
        _argIndex = 5;
        _stmt.bindString(_argIndex, reciterId);
        _argIndex = 6;
        _stmt.bindLong(_argIndex, surahId);
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
          __preparedStmtOfUpdateProgress.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object finishActive(final String reciterId, final int surahId, final String status,
      final String error, final long now, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfFinishActive.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, status);
        _argIndex = 2;
        if (error == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, error);
        }
        _argIndex = 3;
        _stmt.bindLong(_argIndex, now);
        _argIndex = 4;
        _stmt.bindString(_argIndex, reciterId);
        _argIndex = 5;
        _stmt.bindLong(_argIndex, surahId);
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
          __preparedStmtOfFinishActive.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object setStatus(final String reciterId, final int surahId, final String status,
      final long now, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfSetStatus.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, status);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, now);
        _argIndex = 3;
        _stmt.bindString(_argIndex, reciterId);
        _argIndex = 4;
        _stmt.bindLong(_argIndex, surahId);
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
          __preparedStmtOfSetStatus.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final String reciterId, final int surahId,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDelete.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, reciterId);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, surahId);
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
          __preparedStmtOfDelete.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<DownloadEntity>> observeAll() {
    final String _sql = "SELECT * FROM download ORDER BY updatedAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"download"}, new Callable<List<DownloadEntity>>() {
      @Override
      @NonNull
      public List<DownloadEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfReciterId = CursorUtil.getColumnIndexOrThrow(_cursor, "reciterId");
          final int _cursorIndexOfSurahId = CursorUtil.getColumnIndexOrThrow(_cursor, "surahId");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfDownloadedAyahs = CursorUtil.getColumnIndexOrThrow(_cursor, "downloadedAyahs");
          final int _cursorIndexOfTotalAyahs = CursorUtil.getColumnIndexOrThrow(_cursor, "totalAyahs");
          final int _cursorIndexOfTotalBytes = CursorUtil.getColumnIndexOrThrow(_cursor, "totalBytes");
          final int _cursorIndexOfErrorCode = CursorUtil.getColumnIndexOrThrow(_cursor, "errorCode");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<DownloadEntity> _result = new ArrayList<DownloadEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DownloadEntity _item;
            final String _tmpReciterId;
            _tmpReciterId = _cursor.getString(_cursorIndexOfReciterId);
            final int _tmpSurahId;
            _tmpSurahId = _cursor.getInt(_cursorIndexOfSurahId);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final int _tmpDownloadedAyahs;
            _tmpDownloadedAyahs = _cursor.getInt(_cursorIndexOfDownloadedAyahs);
            final int _tmpTotalAyahs;
            _tmpTotalAyahs = _cursor.getInt(_cursorIndexOfTotalAyahs);
            final long _tmpTotalBytes;
            _tmpTotalBytes = _cursor.getLong(_cursorIndexOfTotalBytes);
            final String _tmpErrorCode;
            if (_cursor.isNull(_cursorIndexOfErrorCode)) {
              _tmpErrorCode = null;
            } else {
              _tmpErrorCode = _cursor.getString(_cursorIndexOfErrorCode);
            }
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new DownloadEntity(_tmpReciterId,_tmpSurahId,_tmpStatus,_tmpDownloadedAyahs,_tmpTotalAyahs,_tmpTotalBytes,_tmpErrorCode,_tmpUpdatedAt);
            _result.add(_item);
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

  @Override
  public Object find(final String reciterId, final int surahId,
      final Continuation<? super DownloadEntity> $completion) {
    final String _sql = "SELECT * FROM download WHERE reciterId = ? AND surahId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, reciterId);
    _argIndex = 2;
    _statement.bindLong(_argIndex, surahId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<DownloadEntity>() {
      @Override
      @Nullable
      public DownloadEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfReciterId = CursorUtil.getColumnIndexOrThrow(_cursor, "reciterId");
          final int _cursorIndexOfSurahId = CursorUtil.getColumnIndexOrThrow(_cursor, "surahId");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfDownloadedAyahs = CursorUtil.getColumnIndexOrThrow(_cursor, "downloadedAyahs");
          final int _cursorIndexOfTotalAyahs = CursorUtil.getColumnIndexOrThrow(_cursor, "totalAyahs");
          final int _cursorIndexOfTotalBytes = CursorUtil.getColumnIndexOrThrow(_cursor, "totalBytes");
          final int _cursorIndexOfErrorCode = CursorUtil.getColumnIndexOrThrow(_cursor, "errorCode");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final DownloadEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpReciterId;
            _tmpReciterId = _cursor.getString(_cursorIndexOfReciterId);
            final int _tmpSurahId;
            _tmpSurahId = _cursor.getInt(_cursorIndexOfSurahId);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final int _tmpDownloadedAyahs;
            _tmpDownloadedAyahs = _cursor.getInt(_cursorIndexOfDownloadedAyahs);
            final int _tmpTotalAyahs;
            _tmpTotalAyahs = _cursor.getInt(_cursorIndexOfTotalAyahs);
            final long _tmpTotalBytes;
            _tmpTotalBytes = _cursor.getLong(_cursorIndexOfTotalBytes);
            final String _tmpErrorCode;
            if (_cursor.isNull(_cursorIndexOfErrorCode)) {
              _tmpErrorCode = null;
            } else {
              _tmpErrorCode = _cursor.getString(_cursorIndexOfErrorCode);
            }
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _result = new DownloadEntity(_tmpReciterId,_tmpSurahId,_tmpStatus,_tmpDownloadedAyahs,_tmpTotalAyahs,_tmpTotalBytes,_tmpErrorCode,_tmpUpdatedAt);
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
