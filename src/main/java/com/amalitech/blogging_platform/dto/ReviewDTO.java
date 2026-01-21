package com.amalitech.blogging_platform.dto;

import com.amalitech.blogging_platform.model.EReview;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

public class ReviewDTO {

  private ReviewDTO(){}

  @Getter
  @Setter
  @Schema(name = "ReviewDTO")
  public static class In{
    @PositiveOrZero
    @Schema(description = "The post id", example = "67")
    private Long postId;
    @PositiveOrZero
    @Schema(description = "The user id", example = "67")
    private Long userId;
    @NotNull
    @Schema(description = "The review rate", example = "FOUR")
    private EReview rate;
  }
}

