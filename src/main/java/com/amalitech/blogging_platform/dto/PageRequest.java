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
  private final int page ;

  @Schema(description = "page size", defaultValue = "10")
  @Positive(message = "Page size should be a positive number, greater than 0")
  private final int size ;

  public PageRequest(){
    this.page = 1;
    this.size = 10;
  }

  public PageRequest(Integer page, Integer size) {
    if (page == null || page <= 0 ) {
      page = 1;
    }
    if (size == null || size <= 0) {
      size = 10;
    }

    this.page = page;
    this.size = size;
  }
}
