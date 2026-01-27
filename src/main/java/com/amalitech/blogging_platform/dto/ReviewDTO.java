package com.amalitech.blogging_platform.dto;

import com.amalitech.blogging_platform.model.EReview;
import com.amalitech.blogging_platform.model.Review;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

public class ReviewDTO {

  private ReviewDTO(){}

  @Getter
  @Setter
  @Schema(name = "ReviewDTO", description = "Request payload to create or update a review")
  public static class In{
    @PositiveOrZero
    @Schema(description = "The post id", example = "67")
    private Long postId;
    @PositiveOrZero
    @Schema(description = "The user id", example = "12")
    private Long userId;
    @NotNull
    @Schema(description = "The review rate", example = "FOUR")
    private EReview rate;
  }

  @Getter
  @Setter
  public static class Out{
    private Long id;
    private PostDTO.Out post;
    private UserDTO.Out user;
    private EReview rate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private boolean isDeleted;

  }

  public static class Converter{

   private Converter(){}

    public static Out toDTO(Review review){
     Out out = new Out();
     out.setId(review.getId());
     out.setRate(EReview.valueOf(review.getRate()));
     out.setPost(PostDTO.Converter.toDTO(review.getPost()));
     out.setUser(UserDTO.Converter.toDTO(review.getUser()));
     out.setCreatedAt(review.getCreatedAt());
     out.setUpdatedAt(review.getUpdatedAt());
     out.setDeletedAt(review.getDeletedAt());
     out.setDeleted(review.isDeleted());
     return out;
    }
  }


}

