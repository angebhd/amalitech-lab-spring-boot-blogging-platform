package com.amalitech.blogging_platform.exceptions;

public class RessourceNotFoundException extends RuntimeException{
  public RessourceNotFoundException(String message) {
    super(message);
  }
}
