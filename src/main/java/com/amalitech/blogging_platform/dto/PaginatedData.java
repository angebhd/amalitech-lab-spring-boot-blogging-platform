package com.amalitech.blogging_platform.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

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

  public PaginatedData(Page<T> page) {
    this.setPage(page.getNumber());
    this.setPageSize(page.getSize());
    this.setTotalPages(page.getTotalPages());
    this.setTotal((int)page.getTotalElements());
    this.setItems(page.getContent());
    this.setSort(page.getSort());
    this.setFirstPage(page.isFirst());
    this.setLastPage(page.isLast());
  }

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

  @Schema(description = "Sort applied")
  private Sort sort;

  private boolean isFirstPage;
  private boolean isLastPage;

}
