package com.quoren.app.data.local;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomDatabaseKt;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Long;
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
public final class MemorizationDao_Impl implements MemorizationDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<MemorizationProgressEntity> __insertionAdapterOfMemorizationProgressEntity;

  private final EntityInsertionAdapter<MemorizationSessionEntity> __insertionAdapterOfMemorizationSessionEntity;

  private final EntityInsertionAdapter<ReviewHistoryEntity> __insertionAdapterOfReviewHistoryEntity;

  private final EntityInsertionAdapter<ReviewHistoryEntity> __insertionAdapterOfReviewHistoryEntity_1;

  private final EntityInsertionAdapter<DailyGoalEntity> __insertionAdapterOfDailyGoalEntity;

  private final EntityInsertionAdapter<DailyGoalCompletionEntity> __insertionAdapterOfDailyGoalCompletionEntity;

  private final EntityInsertionAdapter<DailyGoalCompletionEntity> __insertionAdapterOfDailyGoalCompletionEntity_1;

  private final SharedSQLiteStatement __preparedStmtOfClearProgressForRestore;

  private final SharedSQLiteStatement __preparedStmtOfClearSessionsForRestore;

  private final SharedSQLiteStatement __preparedStmtOfClearReviewsForRestore;

  private final SharedSQLiteStatement __preparedStmtOfClearDailyGoalForRestore;

  private final SharedSQLiteStatement __preparedStmtOfClearGoalCompletionsForRestore;

  private final SharedSQLiteStatement __preparedStmtOfDeleteSession;

  public MemorizationDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfMemorizationProgressEntity = new EntityInsertionAdapter<MemorizationProgressEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `memorization_progress` (`ayahId`,`status`,`scheduleStage`,`dueEpochDay`,`lastReviewedEpochDay`,`reviewCount`,`consecutiveHardRatings`,`isWeak`) VALUES (?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final MemorizationProgressEntity entity) {
        statement.bindLong(1, entity.getAyahId());
        statement.bindString(2, entity.getStatus());
        statement.bindLong(3, entity.getScheduleStage());
        statement.bindLong(4, entity.getDueEpochDay());
        if (entity.getLastReviewedEpochDay() == null) {
          statement.bindNull(5);
        } else {
          statement.bindLong(5, entity.getLastReviewedEpochDay());
        }
        statement.bindLong(6, entity.getReviewCount());
        statement.bindLong(7, entity.getConsecutiveHardRatings());
        final int _tmp = entity.isWeak() ? 1 : 0;
        statement.bindLong(8, _tmp);
      }
    };
    this.__insertionAdapterOfMemorizationSessionEntity = new EntityInsertionAdapter<MemorizationSessionEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `memorization_session` (`sessionId`,`surahId`,`startAyah`,`endAyah`,`reciterId`,`repeatCount`,`mode`,`currentAyah`,`createdAt`,`lastOpenedAt`,`completedAt`) VALUES (?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final MemorizationSessionEntity entity) {
        statement.bindString(1, entity.getSessionId());
        statement.bindLong(2, entity.getSurahId());
        statement.bindLong(3, entity.getStartAyah());
        statement.bindLong(4, entity.getEndAyah());
        statement.bindString(5, entity.getReciterId());
        statement.bindString(6, entity.getRepeatCount());
        statement.bindString(7, entity.getMode());
        statement.bindLong(8, entity.getCurrentAyah());
        statement.bindLong(9, entity.getCreatedAt());
        statement.bindLong(10, entity.getLastOpenedAt());
        if (entity.getCompletedAt() == null) {
          statement.bindNull(11);
        } else {
          statement.bindLong(11, entity.getCompletedAt());
        }
      }
    };
    this.__insertionAdapterOfReviewHistoryEntity = new EntityInsertionAdapter<ReviewHistoryEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `review_history` (`id`,`ayahId`,`sessionId`,`difficulty`,`reviewedAt`,`dueEpochDay`) VALUES (nullif(?, 0),?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ReviewHistoryEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getAyahId());
        if (entity.getSessionId() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getSessionId());
        }
        statement.bindString(4, entity.getDifficulty());
        statement.bindLong(5, entity.getReviewedAt());
        statement.bindLong(6, entity.getDueEpochDay());
      }
    };
    this.__insertionAdapterOfReviewHistoryEntity_1 = new EntityInsertionAdapter<ReviewHistoryEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `review_history` (`id`,`ayahId`,`sessionId`,`difficulty`,`reviewedAt`,`dueEpochDay`) VALUES (nullif(?, 0),?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ReviewHistoryEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getAyahId());
        if (entity.getSessionId() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getSessionId());
        }
        statement.bindString(4, entity.getDifficulty());
        statement.bindLong(5, entity.getReviewedAt());
        statement.bindLong(6, entity.getDueEpochDay());
      }
    };
    this.__insertionAdapterOfDailyGoalEntity = new EntityInsertionAdapter<DailyGoalEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `daily_goal` (`id`,`ayahTarget`,`completedAyahs`,`epochDay`) VALUES (?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final DailyGoalEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getAyahTarget());
        statement.bindLong(3, entity.getCompletedAyahs());
        statement.bindLong(4, entity.getEpochDay());
      }
    };
    this.__insertionAdapterOfDailyGoalCompletionEntity = new EntityInsertionAdapter<DailyGoalCompletionEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR IGNORE INTO `daily_goal_completion` (`epochDay`,`ayahId`) VALUES (?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final DailyGoalCompletionEntity entity) {
        statement.bindLong(1, entity.getEpochDay());
        statement.bindLong(2, entity.getAyahId());
      }
    };
    this.__insertionAdapterOfDailyGoalCompletionEntity_1 = new EntityInsertionAdapter<DailyGoalCompletionEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `daily_goal_completion` (`epochDay`,`ayahId`) VALUES (?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final DailyGoalCompletionEntity entity) {
        statement.bindLong(1, entity.getEpochDay());
        statement.bindLong(2, entity.getAyahId());
      }
    };
    this.__preparedStmtOfClearProgressForRestore = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM memorization_progress";
        return _query;
      }
    };
    this.__preparedStmtOfClearSessionsForRestore = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM memorization_session";
        return _query;
      }
    };
    this.__preparedStmtOfClearReviewsForRestore = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM review_history";
        return _query;
      }
    };
    this.__preparedStmtOfClearDailyGoalForRestore = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM daily_goal";
        return _query;
      }
    };
    this.__preparedStmtOfClearGoalCompletionsForRestore = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM daily_goal_completion";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteSession = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM memorization_session WHERE sessionId = ?";
        return _query;
      }
    };
  }

  @Override
  public Object upsertProgress(final MemorizationProgressEntity item,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfMemorizationProgressEntity.insert(item);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object restoreProgress(final List<MemorizationProgressEntity> items,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfMemorizationProgressEntity.insert(items);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object upsertSession(final MemorizationSessionEntity item,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfMemorizationSessionEntity.insert(item);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object restoreSessions(final List<MemorizationSessionEntity> items,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfMemorizationSessionEntity.insert(items);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertReview(final ReviewHistoryEntity item,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfReviewHistoryEntity.insertAndReturnId(item);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object restoreReviews(final List<ReviewHistoryEntity> items,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfReviewHistoryEntity_1.insert(items);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object upsertDailyGoal(final DailyGoalEntity item,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfDailyGoalEntity.insert(item);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object restoreDailyGoal(final DailyGoalEntity item,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfDailyGoalEntity.insert(item);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertCompletion(final DailyGoalCompletionEntity item,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfDailyGoalCompletionEntity.insert(item);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object restoreGoalCompletions(final List<DailyGoalCompletionEntity> items,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfDailyGoalCompletionEntity_1.insert(items);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object recordCompletion(final int ayahId, final long epochDay,
      final Continuation<? super Unit> $completion) {
    return RoomDatabaseKt.withTransaction(__db, (__cont) -> MemorizationDao.DefaultImpls.recordCompletion(MemorizationDao_Impl.this, ayahId, epochDay, __cont), $completion);
  }

  @Override
  public Object clearProgressForRestore(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfClearProgressForRestore.acquire();
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
          __preparedStmtOfClearProgressForRestore.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object clearSessionsForRestore(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfClearSessionsForRestore.acquire();
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
          __preparedStmtOfClearSessionsForRestore.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object clearReviewsForRestore(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfClearReviewsForRestore.acquire();
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
          __preparedStmtOfClearReviewsForRestore.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object clearDailyGoalForRestore(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfClearDailyGoalForRestore.acquire();
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
          __preparedStmtOfClearDailyGoalForRestore.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object clearGoalCompletionsForRestore(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfClearGoalCompletionsForRestore.acquire();
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
          __preparedStmtOfClearGoalCompletionsForRestore.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteSession(final String sessionId,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteSession.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, sessionId);
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
          __preparedStmtOfDeleteSession.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object exportProgress(
      final Continuation<? super List<MemorizationProgressEntity>> $completion) {
    final String _sql = "SELECT * FROM memorization_progress ORDER BY ayahId";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<MemorizationProgressEntity>>() {
      @Override
      @NonNull
      public List<MemorizationProgressEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfAyahId = CursorUtil.getColumnIndexOrThrow(_cursor, "ayahId");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfScheduleStage = CursorUtil.getColumnIndexOrThrow(_cursor, "scheduleStage");
          final int _cursorIndexOfDueEpochDay = CursorUtil.getColumnIndexOrThrow(_cursor, "dueEpochDay");
          final int _cursorIndexOfLastReviewedEpochDay = CursorUtil.getColumnIndexOrThrow(_cursor, "lastReviewedEpochDay");
          final int _cursorIndexOfReviewCount = CursorUtil.getColumnIndexOrThrow(_cursor, "reviewCount");
          final int _cursorIndexOfConsecutiveHardRatings = CursorUtil.getColumnIndexOrThrow(_cursor, "consecutiveHardRatings");
          final int _cursorIndexOfIsWeak = CursorUtil.getColumnIndexOrThrow(_cursor, "isWeak");
          final List<MemorizationProgressEntity> _result = new ArrayList<MemorizationProgressEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final MemorizationProgressEntity _item;
            final int _tmpAyahId;
            _tmpAyahId = _cursor.getInt(_cursorIndexOfAyahId);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final int _tmpScheduleStage;
            _tmpScheduleStage = _cursor.getInt(_cursorIndexOfScheduleStage);
            final long _tmpDueEpochDay;
            _tmpDueEpochDay = _cursor.getLong(_cursorIndexOfDueEpochDay);
            final Long _tmpLastReviewedEpochDay;
            if (_cursor.isNull(_cursorIndexOfLastReviewedEpochDay)) {
              _tmpLastReviewedEpochDay = null;
            } else {
              _tmpLastReviewedEpochDay = _cursor.getLong(_cursorIndexOfLastReviewedEpochDay);
            }
            final int _tmpReviewCount;
            _tmpReviewCount = _cursor.getInt(_cursorIndexOfReviewCount);
            final int _tmpConsecutiveHardRatings;
            _tmpConsecutiveHardRatings = _cursor.getInt(_cursorIndexOfConsecutiveHardRatings);
            final boolean _tmpIsWeak;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsWeak);
            _tmpIsWeak = _tmp != 0;
            _item = new MemorizationProgressEntity(_tmpAyahId,_tmpStatus,_tmpScheduleStage,_tmpDueEpochDay,_tmpLastReviewedEpochDay,_tmpReviewCount,_tmpConsecutiveHardRatings,_tmpIsWeak);
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
  public Object exportSessions(
      final Continuation<? super List<MemorizationSessionEntity>> $completion) {
    final String _sql = "SELECT * FROM memorization_session ORDER BY lastOpenedAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<MemorizationSessionEntity>>() {
      @Override
      @NonNull
      public List<MemorizationSessionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfSessionId = CursorUtil.getColumnIndexOrThrow(_cursor, "sessionId");
          final int _cursorIndexOfSurahId = CursorUtil.getColumnIndexOrThrow(_cursor, "surahId");
          final int _cursorIndexOfStartAyah = CursorUtil.getColumnIndexOrThrow(_cursor, "startAyah");
          final int _cursorIndexOfEndAyah = CursorUtil.getColumnIndexOrThrow(_cursor, "endAyah");
          final int _cursorIndexOfReciterId = CursorUtil.getColumnIndexOrThrow(_cursor, "reciterId");
          final int _cursorIndexOfRepeatCount = CursorUtil.getColumnIndexOrThrow(_cursor, "repeatCount");
          final int _cursorIndexOfMode = CursorUtil.getColumnIndexOrThrow(_cursor, "mode");
          final int _cursorIndexOfCurrentAyah = CursorUtil.getColumnIndexOrThrow(_cursor, "currentAyah");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfLastOpenedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastOpenedAt");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completedAt");
          final List<MemorizationSessionEntity> _result = new ArrayList<MemorizationSessionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final MemorizationSessionEntity _item;
            final String _tmpSessionId;
            _tmpSessionId = _cursor.getString(_cursorIndexOfSessionId);
            final int _tmpSurahId;
            _tmpSurahId = _cursor.getInt(_cursorIndexOfSurahId);
            final int _tmpStartAyah;
            _tmpStartAyah = _cursor.getInt(_cursorIndexOfStartAyah);
            final int _tmpEndAyah;
            _tmpEndAyah = _cursor.getInt(_cursorIndexOfEndAyah);
            final String _tmpReciterId;
            _tmpReciterId = _cursor.getString(_cursorIndexOfReciterId);
            final String _tmpRepeatCount;
            _tmpRepeatCount = _cursor.getString(_cursorIndexOfRepeatCount);
            final String _tmpMode;
            _tmpMode = _cursor.getString(_cursorIndexOfMode);
            final int _tmpCurrentAyah;
            _tmpCurrentAyah = _cursor.getInt(_cursorIndexOfCurrentAyah);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpLastOpenedAt;
            _tmpLastOpenedAt = _cursor.getLong(_cursorIndexOfLastOpenedAt);
            final Long _tmpCompletedAt;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmpCompletedAt = null;
            } else {
              _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            _item = new MemorizationSessionEntity(_tmpSessionId,_tmpSurahId,_tmpStartAyah,_tmpEndAyah,_tmpReciterId,_tmpRepeatCount,_tmpMode,_tmpCurrentAyah,_tmpCreatedAt,_tmpLastOpenedAt,_tmpCompletedAt);
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
  public Object exportReviews(final Continuation<? super List<ReviewHistoryEntity>> $completion) {
    final String _sql = "SELECT * FROM review_history ORDER BY reviewedAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<ReviewHistoryEntity>>() {
      @Override
      @NonNull
      public List<ReviewHistoryEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfAyahId = CursorUtil.getColumnIndexOrThrow(_cursor, "ayahId");
          final int _cursorIndexOfSessionId = CursorUtil.getColumnIndexOrThrow(_cursor, "sessionId");
          final int _cursorIndexOfDifficulty = CursorUtil.getColumnIndexOrThrow(_cursor, "difficulty");
          final int _cursorIndexOfReviewedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "reviewedAt");
          final int _cursorIndexOfDueEpochDay = CursorUtil.getColumnIndexOrThrow(_cursor, "dueEpochDay");
          final List<ReviewHistoryEntity> _result = new ArrayList<ReviewHistoryEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ReviewHistoryEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final int _tmpAyahId;
            _tmpAyahId = _cursor.getInt(_cursorIndexOfAyahId);
            final String _tmpSessionId;
            if (_cursor.isNull(_cursorIndexOfSessionId)) {
              _tmpSessionId = null;
            } else {
              _tmpSessionId = _cursor.getString(_cursorIndexOfSessionId);
            }
            final String _tmpDifficulty;
            _tmpDifficulty = _cursor.getString(_cursorIndexOfDifficulty);
            final long _tmpReviewedAt;
            _tmpReviewedAt = _cursor.getLong(_cursorIndexOfReviewedAt);
            final long _tmpDueEpochDay;
            _tmpDueEpochDay = _cursor.getLong(_cursorIndexOfDueEpochDay);
            _item = new ReviewHistoryEntity(_tmpId,_tmpAyahId,_tmpSessionId,_tmpDifficulty,_tmpReviewedAt,_tmpDueEpochDay);
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
  public Object exportDailyGoal(final Continuation<? super DailyGoalEntity> $completion) {
    final String _sql = "SELECT * FROM daily_goal WHERE id = 0";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<DailyGoalEntity>() {
      @Override
      @Nullable
      public DailyGoalEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfAyahTarget = CursorUtil.getColumnIndexOrThrow(_cursor, "ayahTarget");
          final int _cursorIndexOfCompletedAyahs = CursorUtil.getColumnIndexOrThrow(_cursor, "completedAyahs");
          final int _cursorIndexOfEpochDay = CursorUtil.getColumnIndexOrThrow(_cursor, "epochDay");
          final DailyGoalEntity _result;
          if (_cursor.moveToFirst()) {
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final int _tmpAyahTarget;
            _tmpAyahTarget = _cursor.getInt(_cursorIndexOfAyahTarget);
            final int _tmpCompletedAyahs;
            _tmpCompletedAyahs = _cursor.getInt(_cursorIndexOfCompletedAyahs);
            final long _tmpEpochDay;
            _tmpEpochDay = _cursor.getLong(_cursorIndexOfEpochDay);
            _result = new DailyGoalEntity(_tmpId,_tmpAyahTarget,_tmpCompletedAyahs,_tmpEpochDay);
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
  public Object exportGoalCompletions(
      final Continuation<? super List<DailyGoalCompletionEntity>> $completion) {
    final String _sql = "SELECT * FROM daily_goal_completion ORDER BY epochDay, ayahId";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<DailyGoalCompletionEntity>>() {
      @Override
      @NonNull
      public List<DailyGoalCompletionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfEpochDay = CursorUtil.getColumnIndexOrThrow(_cursor, "epochDay");
          final int _cursorIndexOfAyahId = CursorUtil.getColumnIndexOrThrow(_cursor, "ayahId");
          final List<DailyGoalCompletionEntity> _result = new ArrayList<DailyGoalCompletionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DailyGoalCompletionEntity _item;
            final long _tmpEpochDay;
            _tmpEpochDay = _cursor.getLong(_cursorIndexOfEpochDay);
            final int _tmpAyahId;
            _tmpAyahId = _cursor.getInt(_cursorIndexOfAyahId);
            _item = new DailyGoalCompletionEntity(_tmpEpochDay,_tmpAyahId);
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
  public Flow<List<MemorizationProgressEntity>> observeProgress() {
    final String _sql = "SELECT * FROM memorization_progress ORDER BY ayahId";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"memorization_progress"}, new Callable<List<MemorizationProgressEntity>>() {
      @Override
      @NonNull
      public List<MemorizationProgressEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfAyahId = CursorUtil.getColumnIndexOrThrow(_cursor, "ayahId");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfScheduleStage = CursorUtil.getColumnIndexOrThrow(_cursor, "scheduleStage");
          final int _cursorIndexOfDueEpochDay = CursorUtil.getColumnIndexOrThrow(_cursor, "dueEpochDay");
          final int _cursorIndexOfLastReviewedEpochDay = CursorUtil.getColumnIndexOrThrow(_cursor, "lastReviewedEpochDay");
          final int _cursorIndexOfReviewCount = CursorUtil.getColumnIndexOrThrow(_cursor, "reviewCount");
          final int _cursorIndexOfConsecutiveHardRatings = CursorUtil.getColumnIndexOrThrow(_cursor, "consecutiveHardRatings");
          final int _cursorIndexOfIsWeak = CursorUtil.getColumnIndexOrThrow(_cursor, "isWeak");
          final List<MemorizationProgressEntity> _result = new ArrayList<MemorizationProgressEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final MemorizationProgressEntity _item;
            final int _tmpAyahId;
            _tmpAyahId = _cursor.getInt(_cursorIndexOfAyahId);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final int _tmpScheduleStage;
            _tmpScheduleStage = _cursor.getInt(_cursorIndexOfScheduleStage);
            final long _tmpDueEpochDay;
            _tmpDueEpochDay = _cursor.getLong(_cursorIndexOfDueEpochDay);
            final Long _tmpLastReviewedEpochDay;
            if (_cursor.isNull(_cursorIndexOfLastReviewedEpochDay)) {
              _tmpLastReviewedEpochDay = null;
            } else {
              _tmpLastReviewedEpochDay = _cursor.getLong(_cursorIndexOfLastReviewedEpochDay);
            }
            final int _tmpReviewCount;
            _tmpReviewCount = _cursor.getInt(_cursorIndexOfReviewCount);
            final int _tmpConsecutiveHardRatings;
            _tmpConsecutiveHardRatings = _cursor.getInt(_cursorIndexOfConsecutiveHardRatings);
            final boolean _tmpIsWeak;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsWeak);
            _tmpIsWeak = _tmp != 0;
            _item = new MemorizationProgressEntity(_tmpAyahId,_tmpStatus,_tmpScheduleStage,_tmpDueEpochDay,_tmpLastReviewedEpochDay,_tmpReviewCount,_tmpConsecutiveHardRatings,_tmpIsWeak);
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
  public Flow<MemorizationProgressEntity> observeProgress(final int ayahId) {
    final String _sql = "SELECT * FROM memorization_progress WHERE ayahId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, ayahId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"memorization_progress"}, new Callable<MemorizationProgressEntity>() {
      @Override
      @Nullable
      public MemorizationProgressEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfAyahId = CursorUtil.getColumnIndexOrThrow(_cursor, "ayahId");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfScheduleStage = CursorUtil.getColumnIndexOrThrow(_cursor, "scheduleStage");
          final int _cursorIndexOfDueEpochDay = CursorUtil.getColumnIndexOrThrow(_cursor, "dueEpochDay");
          final int _cursorIndexOfLastReviewedEpochDay = CursorUtil.getColumnIndexOrThrow(_cursor, "lastReviewedEpochDay");
          final int _cursorIndexOfReviewCount = CursorUtil.getColumnIndexOrThrow(_cursor, "reviewCount");
          final int _cursorIndexOfConsecutiveHardRatings = CursorUtil.getColumnIndexOrThrow(_cursor, "consecutiveHardRatings");
          final int _cursorIndexOfIsWeak = CursorUtil.getColumnIndexOrThrow(_cursor, "isWeak");
          final MemorizationProgressEntity _result;
          if (_cursor.moveToFirst()) {
            final int _tmpAyahId;
            _tmpAyahId = _cursor.getInt(_cursorIndexOfAyahId);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final int _tmpScheduleStage;
            _tmpScheduleStage = _cursor.getInt(_cursorIndexOfScheduleStage);
            final long _tmpDueEpochDay;
            _tmpDueEpochDay = _cursor.getLong(_cursorIndexOfDueEpochDay);
            final Long _tmpLastReviewedEpochDay;
            if (_cursor.isNull(_cursorIndexOfLastReviewedEpochDay)) {
              _tmpLastReviewedEpochDay = null;
            } else {
              _tmpLastReviewedEpochDay = _cursor.getLong(_cursorIndexOfLastReviewedEpochDay);
            }
            final int _tmpReviewCount;
            _tmpReviewCount = _cursor.getInt(_cursorIndexOfReviewCount);
            final int _tmpConsecutiveHardRatings;
            _tmpConsecutiveHardRatings = _cursor.getInt(_cursorIndexOfConsecutiveHardRatings);
            final boolean _tmpIsWeak;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsWeak);
            _tmpIsWeak = _tmp != 0;
            _result = new MemorizationProgressEntity(_tmpAyahId,_tmpStatus,_tmpScheduleStage,_tmpDueEpochDay,_tmpLastReviewedEpochDay,_tmpReviewCount,_tmpConsecutiveHardRatings,_tmpIsWeak);
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

  @Override
  public Object getProgress(final int ayahId,
      final Continuation<? super MemorizationProgressEntity> $completion) {
    final String _sql = "SELECT * FROM memorization_progress WHERE ayahId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, ayahId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<MemorizationProgressEntity>() {
      @Override
      @Nullable
      public MemorizationProgressEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfAyahId = CursorUtil.getColumnIndexOrThrow(_cursor, "ayahId");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfScheduleStage = CursorUtil.getColumnIndexOrThrow(_cursor, "scheduleStage");
          final int _cursorIndexOfDueEpochDay = CursorUtil.getColumnIndexOrThrow(_cursor, "dueEpochDay");
          final int _cursorIndexOfLastReviewedEpochDay = CursorUtil.getColumnIndexOrThrow(_cursor, "lastReviewedEpochDay");
          final int _cursorIndexOfReviewCount = CursorUtil.getColumnIndexOrThrow(_cursor, "reviewCount");
          final int _cursorIndexOfConsecutiveHardRatings = CursorUtil.getColumnIndexOrThrow(_cursor, "consecutiveHardRatings");
          final int _cursorIndexOfIsWeak = CursorUtil.getColumnIndexOrThrow(_cursor, "isWeak");
          final MemorizationProgressEntity _result;
          if (_cursor.moveToFirst()) {
            final int _tmpAyahId;
            _tmpAyahId = _cursor.getInt(_cursorIndexOfAyahId);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final int _tmpScheduleStage;
            _tmpScheduleStage = _cursor.getInt(_cursorIndexOfScheduleStage);
            final long _tmpDueEpochDay;
            _tmpDueEpochDay = _cursor.getLong(_cursorIndexOfDueEpochDay);
            final Long _tmpLastReviewedEpochDay;
            if (_cursor.isNull(_cursorIndexOfLastReviewedEpochDay)) {
              _tmpLastReviewedEpochDay = null;
            } else {
              _tmpLastReviewedEpochDay = _cursor.getLong(_cursorIndexOfLastReviewedEpochDay);
            }
            final int _tmpReviewCount;
            _tmpReviewCount = _cursor.getInt(_cursorIndexOfReviewCount);
            final int _tmpConsecutiveHardRatings;
            _tmpConsecutiveHardRatings = _cursor.getInt(_cursorIndexOfConsecutiveHardRatings);
            final boolean _tmpIsWeak;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsWeak);
            _tmpIsWeak = _tmp != 0;
            _result = new MemorizationProgressEntity(_tmpAyahId,_tmpStatus,_tmpScheduleStage,_tmpDueEpochDay,_tmpLastReviewedEpochDay,_tmpReviewCount,_tmpConsecutiveHardRatings,_tmpIsWeak);
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
  public Flow<List<MemorizationSessionEntity>> observeSessions() {
    final String _sql = "SELECT * FROM memorization_session ORDER BY lastOpenedAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"memorization_session"}, new Callable<List<MemorizationSessionEntity>>() {
      @Override
      @NonNull
      public List<MemorizationSessionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfSessionId = CursorUtil.getColumnIndexOrThrow(_cursor, "sessionId");
          final int _cursorIndexOfSurahId = CursorUtil.getColumnIndexOrThrow(_cursor, "surahId");
          final int _cursorIndexOfStartAyah = CursorUtil.getColumnIndexOrThrow(_cursor, "startAyah");
          final int _cursorIndexOfEndAyah = CursorUtil.getColumnIndexOrThrow(_cursor, "endAyah");
          final int _cursorIndexOfReciterId = CursorUtil.getColumnIndexOrThrow(_cursor, "reciterId");
          final int _cursorIndexOfRepeatCount = CursorUtil.getColumnIndexOrThrow(_cursor, "repeatCount");
          final int _cursorIndexOfMode = CursorUtil.getColumnIndexOrThrow(_cursor, "mode");
          final int _cursorIndexOfCurrentAyah = CursorUtil.getColumnIndexOrThrow(_cursor, "currentAyah");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfLastOpenedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastOpenedAt");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completedAt");
          final List<MemorizationSessionEntity> _result = new ArrayList<MemorizationSessionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final MemorizationSessionEntity _item;
            final String _tmpSessionId;
            _tmpSessionId = _cursor.getString(_cursorIndexOfSessionId);
            final int _tmpSurahId;
            _tmpSurahId = _cursor.getInt(_cursorIndexOfSurahId);
            final int _tmpStartAyah;
            _tmpStartAyah = _cursor.getInt(_cursorIndexOfStartAyah);
            final int _tmpEndAyah;
            _tmpEndAyah = _cursor.getInt(_cursorIndexOfEndAyah);
            final String _tmpReciterId;
            _tmpReciterId = _cursor.getString(_cursorIndexOfReciterId);
            final String _tmpRepeatCount;
            _tmpRepeatCount = _cursor.getString(_cursorIndexOfRepeatCount);
            final String _tmpMode;
            _tmpMode = _cursor.getString(_cursorIndexOfMode);
            final int _tmpCurrentAyah;
            _tmpCurrentAyah = _cursor.getInt(_cursorIndexOfCurrentAyah);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpLastOpenedAt;
            _tmpLastOpenedAt = _cursor.getLong(_cursorIndexOfLastOpenedAt);
            final Long _tmpCompletedAt;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmpCompletedAt = null;
            } else {
              _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            _item = new MemorizationSessionEntity(_tmpSessionId,_tmpSurahId,_tmpStartAyah,_tmpEndAyah,_tmpReciterId,_tmpRepeatCount,_tmpMode,_tmpCurrentAyah,_tmpCreatedAt,_tmpLastOpenedAt,_tmpCompletedAt);
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
  public Object getSession(final String sessionId,
      final Continuation<? super MemorizationSessionEntity> $completion) {
    final String _sql = "SELECT * FROM memorization_session WHERE sessionId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, sessionId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<MemorizationSessionEntity>() {
      @Override
      @Nullable
      public MemorizationSessionEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfSessionId = CursorUtil.getColumnIndexOrThrow(_cursor, "sessionId");
          final int _cursorIndexOfSurahId = CursorUtil.getColumnIndexOrThrow(_cursor, "surahId");
          final int _cursorIndexOfStartAyah = CursorUtil.getColumnIndexOrThrow(_cursor, "startAyah");
          final int _cursorIndexOfEndAyah = CursorUtil.getColumnIndexOrThrow(_cursor, "endAyah");
          final int _cursorIndexOfReciterId = CursorUtil.getColumnIndexOrThrow(_cursor, "reciterId");
          final int _cursorIndexOfRepeatCount = CursorUtil.getColumnIndexOrThrow(_cursor, "repeatCount");
          final int _cursorIndexOfMode = CursorUtil.getColumnIndexOrThrow(_cursor, "mode");
          final int _cursorIndexOfCurrentAyah = CursorUtil.getColumnIndexOrThrow(_cursor, "currentAyah");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfLastOpenedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastOpenedAt");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completedAt");
          final MemorizationSessionEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpSessionId;
            _tmpSessionId = _cursor.getString(_cursorIndexOfSessionId);
            final int _tmpSurahId;
            _tmpSurahId = _cursor.getInt(_cursorIndexOfSurahId);
            final int _tmpStartAyah;
            _tmpStartAyah = _cursor.getInt(_cursorIndexOfStartAyah);
            final int _tmpEndAyah;
            _tmpEndAyah = _cursor.getInt(_cursorIndexOfEndAyah);
            final String _tmpReciterId;
            _tmpReciterId = _cursor.getString(_cursorIndexOfReciterId);
            final String _tmpRepeatCount;
            _tmpRepeatCount = _cursor.getString(_cursorIndexOfRepeatCount);
            final String _tmpMode;
            _tmpMode = _cursor.getString(_cursorIndexOfMode);
            final int _tmpCurrentAyah;
            _tmpCurrentAyah = _cursor.getInt(_cursorIndexOfCurrentAyah);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpLastOpenedAt;
            _tmpLastOpenedAt = _cursor.getLong(_cursorIndexOfLastOpenedAt);
            final Long _tmpCompletedAt;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmpCompletedAt = null;
            } else {
              _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            _result = new MemorizationSessionEntity(_tmpSessionId,_tmpSurahId,_tmpStartAyah,_tmpEndAyah,_tmpReciterId,_tmpRepeatCount,_tmpMode,_tmpCurrentAyah,_tmpCreatedAt,_tmpLastOpenedAt,_tmpCompletedAt);
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
  public Flow<List<ReviewHistoryEntity>> observeReviewHistory(final int limit) {
    final String _sql = "SELECT * FROM review_history ORDER BY reviewedAt DESC LIMIT ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, limit);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"review_history"}, new Callable<List<ReviewHistoryEntity>>() {
      @Override
      @NonNull
      public List<ReviewHistoryEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfAyahId = CursorUtil.getColumnIndexOrThrow(_cursor, "ayahId");
          final int _cursorIndexOfSessionId = CursorUtil.getColumnIndexOrThrow(_cursor, "sessionId");
          final int _cursorIndexOfDifficulty = CursorUtil.getColumnIndexOrThrow(_cursor, "difficulty");
          final int _cursorIndexOfReviewedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "reviewedAt");
          final int _cursorIndexOfDueEpochDay = CursorUtil.getColumnIndexOrThrow(_cursor, "dueEpochDay");
          final List<ReviewHistoryEntity> _result = new ArrayList<ReviewHistoryEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ReviewHistoryEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final int _tmpAyahId;
            _tmpAyahId = _cursor.getInt(_cursorIndexOfAyahId);
            final String _tmpSessionId;
            if (_cursor.isNull(_cursorIndexOfSessionId)) {
              _tmpSessionId = null;
            } else {
              _tmpSessionId = _cursor.getString(_cursorIndexOfSessionId);
            }
            final String _tmpDifficulty;
            _tmpDifficulty = _cursor.getString(_cursorIndexOfDifficulty);
            final long _tmpReviewedAt;
            _tmpReviewedAt = _cursor.getLong(_cursorIndexOfReviewedAt);
            final long _tmpDueEpochDay;
            _tmpDueEpochDay = _cursor.getLong(_cursorIndexOfDueEpochDay);
            _item = new ReviewHistoryEntity(_tmpId,_tmpAyahId,_tmpSessionId,_tmpDifficulty,_tmpReviewedAt,_tmpDueEpochDay);
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
  public Flow<DailyGoalEntity> observeDailyGoal() {
    final String _sql = "SELECT * FROM daily_goal WHERE id = 0";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"daily_goal"}, new Callable<DailyGoalEntity>() {
      @Override
      @Nullable
      public DailyGoalEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfAyahTarget = CursorUtil.getColumnIndexOrThrow(_cursor, "ayahTarget");
          final int _cursorIndexOfCompletedAyahs = CursorUtil.getColumnIndexOrThrow(_cursor, "completedAyahs");
          final int _cursorIndexOfEpochDay = CursorUtil.getColumnIndexOrThrow(_cursor, "epochDay");
          final DailyGoalEntity _result;
          if (_cursor.moveToFirst()) {
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final int _tmpAyahTarget;
            _tmpAyahTarget = _cursor.getInt(_cursorIndexOfAyahTarget);
            final int _tmpCompletedAyahs;
            _tmpCompletedAyahs = _cursor.getInt(_cursorIndexOfCompletedAyahs);
            final long _tmpEpochDay;
            _tmpEpochDay = _cursor.getLong(_cursorIndexOfEpochDay);
            _result = new DailyGoalEntity(_tmpId,_tmpAyahTarget,_tmpCompletedAyahs,_tmpEpochDay);
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

  @Override
  public Object getDailyGoal(final Continuation<? super DailyGoalEntity> $completion) {
    final String _sql = "SELECT * FROM daily_goal WHERE id = 0";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<DailyGoalEntity>() {
      @Override
      @Nullable
      public DailyGoalEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfAyahTarget = CursorUtil.getColumnIndexOrThrow(_cursor, "ayahTarget");
          final int _cursorIndexOfCompletedAyahs = CursorUtil.getColumnIndexOrThrow(_cursor, "completedAyahs");
          final int _cursorIndexOfEpochDay = CursorUtil.getColumnIndexOrThrow(_cursor, "epochDay");
          final DailyGoalEntity _result;
          if (_cursor.moveToFirst()) {
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final int _tmpAyahTarget;
            _tmpAyahTarget = _cursor.getInt(_cursorIndexOfAyahTarget);
            final int _tmpCompletedAyahs;
            _tmpCompletedAyahs = _cursor.getInt(_cursorIndexOfCompletedAyahs);
            final long _tmpEpochDay;
            _tmpEpochDay = _cursor.getLong(_cursorIndexOfEpochDay);
            _result = new DailyGoalEntity(_tmpId,_tmpAyahTarget,_tmpCompletedAyahs,_tmpEpochDay);
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
  public Object countCompletions(final long epochDay,
      final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM daily_goal_completion WHERE epochDay = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, epochDay);
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
