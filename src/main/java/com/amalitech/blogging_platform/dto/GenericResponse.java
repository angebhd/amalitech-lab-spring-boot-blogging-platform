package com.amalitech.blogging_platform.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

/**
 * Generic wrapper for API responses returned by the server.
 *
 * @param <T> the type of the response payload
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Standard API response wrapper")
public class GenericResponse<T> {

  @Schema(description = "HTTP status code of the response", example = "200")
  private int statusCode;

  @Schema(description = "Human-readable response message", example = "OK")
  private String message;

  @Schema(description = "Actual response payload")
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
