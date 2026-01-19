package com.amalitech.blogging_platform.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PaginatedData<T>{
  private List<T> items;
  private int page;
  private int pageSize;
  private int totalPages;
  private int total;

}
