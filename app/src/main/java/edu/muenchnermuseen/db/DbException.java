package edu.muenchnermuseen.db;

/**
 * Created by sfrey on 05.05.2017.
 */

public class DbException extends Exception {
  public DbException() {
  }

  public DbException(String message) {
    super(message);
  }

  public DbException(String message, Throwable cause) {
    super(message, cause);
  }

  public DbException(Throwable cause) {
    super(cause);
  }
}
