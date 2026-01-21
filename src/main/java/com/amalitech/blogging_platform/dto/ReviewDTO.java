package com.amalitech.blogging_platform.dto;

import com.amalitech.blogging_platform.model.EReview;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

public class ReviewDTO {

  @Getter
  @Setter
  @Schema(name = "ReviewDTO")
  public static class In{
    @PositiveOrZero
    private Long postId;
    @PositiveOrZero
    private Long userId;
    @NotNull
    private EReview rate;
  }
}

