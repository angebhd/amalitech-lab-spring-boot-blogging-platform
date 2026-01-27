package com.amalitech.blogging_platform.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

public class GraphQLPageableBuilder {

  public enum SortDirection {
    ASC, DESC
  }

  @Getter
  @Setter
  public static class SortInput {
    private String field;
    private SortDirection direction;
  }

  public static Pageable get(int page, int size) {
    return PageRequest.of(page, size);
  }

  public static Pageable get(int page, int size, List<SortInput> sortBy) {
    return PageRequest.of(page, size, GraphQLPageableBuilder.getSort(sortBy));
  }

  private static Sort getSort(List<SortInput> sortInputs) {
    if (sortInputs == null || sortInputs.isEmpty()) {
      return Sort.unsorted();
    }
    return Sort.by(
            sortInputs.stream()
                    .map(s -> new Sort.Order(
                            Sort.Direction.valueOf(s.getDirection().name()),
                            s.getField()
                    ))
                    .toList()
    );
  }
}
