package com.amalitech.blogging_platform.exceptions;

public class UnauthorizedException extends RuntimeException {
  public UnauthorizedException(String message) {
        super(message);
    }
}
