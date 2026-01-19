package com.amalitech.blogging_platform.exceptions;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.Map;

@Setter
@NoArgsConstructor
public class ErrorResponse {
  private String message;
  private final Date timestamp = new Date();
  private String path;
  private Map<String, String> description;
  public ErrorResponse(String message,String path) {
    this.message = message;
    this.path = path;
  }

  public ErrorResponse(String message,String path, Map<String, String> description) {
    this.message = message;
    this.path = path;
    this.description = description;
  }
}
