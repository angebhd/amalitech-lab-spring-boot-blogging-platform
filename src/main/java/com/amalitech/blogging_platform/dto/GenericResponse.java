package com.amalitech.blogging_platform.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GenericResponse<T> {
  private int statusCode;
  private String message;
  private T data;

  public GenericResponse(HttpStatus status, String message, T data) {
    this.statusCode = status.value();
    this.message = message;
    this.data = data;
  }

  public GenericResponse(HttpStatus status, T data) {
    this.statusCode = status.value();
    this.message = status.getReasonPhrase();
    this.data = data;
  }


}
