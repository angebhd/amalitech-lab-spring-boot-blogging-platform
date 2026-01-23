package com.amalitech.blogging_platform.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Generic container for paginated API responses.
 *
 * @param <T> type of the items in the page
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "PaginatedData", description = "Container for paginated responses")
public class PaginatedData<T> {

  @Schema(description = "List of items in the current page", example = "[item1, item2, item3, item4]")
  private List<T> items;

  @Schema(description = "Current page number (1-based)", example = "1")
  private int page;

  @Schema(description = "Number of items per page", example = "10")
  private int pageSize;

  @Schema(description = "Total number of pages", example = "3")
  private int totalPages;

  @Schema(description = "Total number of elements across all pages", example = "28")
  private int total;
}
