package com.amalitech.blogging_platform.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PageRequest {
  @Schema(description = "page", defaultValue = "1")
  @Positive(message = "Page should be a positive number, greater than 0")
  private int page = 1;

  @Schema(description = "page size", defaultValue = "10")
  @Positive(message = "Page size should be a positive number, greater than 0")
  private int size = 10;
}
