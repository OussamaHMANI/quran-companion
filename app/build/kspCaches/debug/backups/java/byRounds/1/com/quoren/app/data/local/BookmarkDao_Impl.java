package com.quoren.app.data.local;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
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
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class BookmarkDao_Impl implements BookmarkDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<BookmarkEntity> __insertionAdapterOfBookmarkEntity;

  private final EntityInsertionAdapter<BookmarkEntity> __insertionAdapterOfBookmarkEntity_1;

  private final SharedSQLiteStatement __preparedStmtOfClearForRestore;

  private final SharedSQLiteStatement __preparedStmtOfDeleteByAyahId;

  public BookmarkDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfBookmarkEntity = new EntityInsertionAdapter<BookmarkEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR IGNORE INTO `bookmark` (`id`,`ayahId`,`createdAt`,`note`) VALUES (nullif(?, 0),?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final BookmarkEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getAyahId());
        statement.bindLong(3, entity.getCreatedAt());
        if (entity.getNote() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getNote());
        }
      }
    };
    this.__insertionAdapterOfBookmarkEntity_1 = new EntityInsertionAdapter<BookmarkEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `bookmark` (`id`,`ayahId`,`createdAt`,`note`) VALUES (nullif(?, 0),?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final BookmarkEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getAyahId());
        statement.bindLong(3, entity.getCreatedAt());
        if (entity.getNote() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getNote());
        }
      }
    };
    this.__preparedStmtOfClearForRestore = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM bookmark";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteByAyahId = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM bookmark WHERE ayahId = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final BookmarkEntity item, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfBookmarkEntity.insert(item);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object restoreAll(final List<BookmarkEntity> items,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfBookmarkEntity_1.insert(items);
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
  public Object deleteByAyahId(final int ayahId, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteByAyahId.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, ayahId);
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
          __preparedStmtOfDeleteByAyahId.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object exportAll(final Continuation<? super List<BookmarkEntity>> $completion) {
    final String _sql = "SELECT * FROM bookmark ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<BookmarkEntity>>() {
      @Override
      @NonNull
      public List<BookmarkEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfAyahId = CursorUtil.getColumnIndexOrThrow(_cursor, "ayahId");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfNote = CursorUtil.getColumnIndexOrThrow(_cursor, "note");
          final List<BookmarkEntity> _result = new ArrayList<BookmarkEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final BookmarkEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final int _tmpAyahId;
            _tmpAyahId = _cursor.getInt(_cursorIndexOfAyahId);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final String _tmpNote;
            if (_cursor.isNull(_cursorIndexOfNote)) {
              _tmpNote = null;
            } else {
              _tmpNote = _cursor.getString(_cursorIndexOfNote);
            }
            _item = new BookmarkEntity(_tmpId,_tmpAyahId,_tmpCreatedAt,_tmpNote);
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
  public Flow<List<BookmarkEntity>> observeAll() {
    final String _sql = "SELECT * FROM bookmark ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"bookmark"}, new Callable<List<BookmarkEntity>>() {
      @Override
      @NonNull
      public List<BookmarkEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfAyahId = CursorUtil.getColumnIndexOrThrow(_cursor, "ayahId");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfNote = CursorUtil.getColumnIndexOrThrow(_cursor, "note");
          final List<BookmarkEntity> _result = new ArrayList<BookmarkEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final BookmarkEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final int _tmpAyahId;
            _tmpAyahId = _cursor.getInt(_cursorIndexOfAyahId);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final String _tmpNote;
            if (_cursor.isNull(_cursorIndexOfNote)) {
              _tmpNote = null;
            } else {
              _tmpNote = _cursor.getString(_cursorIndexOfNote);
            }
            _item = new BookmarkEntity(_tmpId,_tmpAyahId,_tmpCreatedAt,_tmpNote);
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
  public Flow<List<Integer>> observeAyahIds() {
    final String _sql = "SELECT ayahId FROM bookmark";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"bookmark"}, new Callable<List<Integer>>() {
      @Override
      @NonNull
      public List<Integer> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final List<Integer> _result = new ArrayList<Integer>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Integer _item;
            _item = _cursor.getInt(0);
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
  public Object count(final int ayahId, final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM bookmark WHERE ayahId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, ayahId);
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
