package com.amalitech.blogging_platform.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Tag extends BaseEntity{
  private Long id;
  private String name;
}
