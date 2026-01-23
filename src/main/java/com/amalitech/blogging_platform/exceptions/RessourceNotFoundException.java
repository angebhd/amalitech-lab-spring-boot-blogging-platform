package com.amalitech.blogging_platform.exceptions;

/**
 * Exception thrown when a requested resource is not found.
 */
public class RessourceNotFoundException extends RuntimeException {
  public RessourceNotFoundException(String message) {
    super(message);
  }
}
