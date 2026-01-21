package com.amalitech.blogging_platform.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "PaginatedData", description = "Container for paginated responses")
public class PaginatedData<T>{
  @Schema(description = "Items", example = "[item1, item2, item3, item4]")
  private List<T> items;

  @Schema(description = "Page retrieved", example = "1")
  private int page;

  @Schema(description = "Page size", example = "10")
  private int pageSize;

  @Schema(description = "Total pages ", example = "3")
  private int totalPages;

  @Schema(description = "Total elements", example = "28")
  private int total;
}
