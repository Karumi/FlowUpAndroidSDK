/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package io.flowup.storage;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import io.flowup.logger.Logger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class SQLDelightStorage {

  private static final ReentrantReadWriteLock DB_LOCK = new ReentrantReadWriteLock();
  private static final Lock READ_LOCK = DB_LOCK.readLock();
  private static final Lock WRITE_LOCK = DB_LOCK.writeLock();

  private final SQLiteOpenHelper openHelper;

  public SQLDelightStorage(SQLiteOpenHelper openHelper) {
    this.openHelper = openHelper;
  }

  protected void executeTransaction(Transaction transaction) {
    WRITE_LOCK.lock();
    Logger.d("Start writing a DB transaction");
    SQLiteDatabase database = null;
    try {
      database = openHelper.getWritableDatabase();
      database.beginTransaction();
      transaction.execute(database);
      database.setTransactionSuccessful();
    } finally {
      if (database != null) {
        database.endTransaction();
        database.close();
      }
      Logger.d("Write DB transaction finished");
      WRITE_LOCK.unlock();
    }
  }

  protected <T> T read(Read<T> read) {
    READ_LOCK.lock();
    Logger.d("Start reading from DB");
    T result = null;
    SQLiteDatabase database = null;
    try {
      database = openHelper.getReadableDatabase();
      result = read.read(database);
    } finally {
      Logger.d("End reading from DB");
      if (database != null) {
        database.close();
      }
      READ_LOCK.unlock();
    }
    return result;
  }

  public interface Read<T> {
    T read(SQLiteDatabase database);
  }

  public interface Transaction {
    void execute(SQLiteDatabase database);
  }
}
