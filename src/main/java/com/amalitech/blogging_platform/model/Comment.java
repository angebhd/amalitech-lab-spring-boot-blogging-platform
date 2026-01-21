package com.amalitech.blogging_platform.model;


import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class Comment extends BaseEntity{
  private Long id;
  private Long postId;
  private Long userId;
  private String body;
  private Long parentCommentId;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private LocalDateTime deletedAt;
  private boolean isDeleted;
}
