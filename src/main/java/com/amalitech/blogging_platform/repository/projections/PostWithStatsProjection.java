package com.amalitech.blogging_platform.repository.projections;

import java.time.LocalDateTime;

public interface PostWithStatsProjection {
  Long getId();

  Long getAuthorId();
  String getAuthorUsername();
  String getAuthorEmail();
  String getAuthorFirstName();
  String getAuthorLastName();

  String getTitle();
  String getBody();

  LocalDateTime getCreatedAt();
  LocalDateTime getUpdatedAt();
  LocalDateTime getDeletedAt();
  Boolean getIsDeleted();

  Long getReviews();
  Double getReviewAverage();
  Long getComments();

  String[] getTags();
}
