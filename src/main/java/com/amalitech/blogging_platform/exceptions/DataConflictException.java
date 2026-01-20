package com.amalitech.blogging_platform.exceptions;

public class DataConflictException extends RuntimeException{
  public DataConflictException(String message) {
    super(message);
  }
}
