package com.amalitech.blogging_platform.exceptions;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.Map;

/**
 * Standard structure for API error responses.
 * Includes an error message, request path, timestamp, and optional field-level details.
 */
@Getter
@Setter // For Jackson during deserialization
@NoArgsConstructor
@Schema(description = "Structure of an error response returned by the server")
public class ErrorResponse {

  @Schema(description = "Error message describing what went wrong", example = "Invalid request")
  private String message;

  @Schema(description = "Timestamp when the error occurred")
  private final Date timestamp = new Date();

  @Schema(description = "Request path that caused the error", example = "/api/v1/comments/12")
  private String path;

  @Schema(description = "Optional field-level error descriptions (e.g., validation errors)")
  private Map<String, String> description;

  public ErrorResponse(String message, String path) {
    this.message = message;
    this.path = path;
  }

}
