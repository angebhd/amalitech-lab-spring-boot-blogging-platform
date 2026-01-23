package com.amalitech.blogging_platform.exceptions;

/**
 * Exception thrown when an operation cannot be completed due to a data conflict.
 * Example: trying to create a record that already exists.
 */
public class DataConflictException extends RuntimeException {
  public DataConflictException(String message) {
    super(message);
  }
}
