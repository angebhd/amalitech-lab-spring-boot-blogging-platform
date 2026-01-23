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
  public static class GraphQL{
    private Long id;
    private Long postId;
    private Long userId;
    private PostDTO.GraphQL post;
    private UserDTO.Out user;
    private EReview rate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private boolean isDeleted;

  }

  public static class Converter{
   private Converter(){}

    public static GraphQL toGraphQL(Review review){
      GraphQL graphQL = new GraphQL();
      graphQL.setId(review.getId());
      graphQL.setPostId(review.getPostId());
      graphQL.setUserId(review.getUserId());
      graphQL.setRate(EReview.valueOf(review.getRate()));
      graphQL.setCreatedAt(review.getCreatedAt());
      graphQL.setUpdatedAt(review.getUpdatedAt());
      graphQL.setDeletedAt(review.getDeletedAt());
      graphQL.setDeleted(review.isDeleted());
      return graphQL;
    }

    public static PaginatedData<GraphQL> toGraphQL(PaginatedData<Review> review){
      PaginatedData<GraphQL> graphQL = new PaginatedData<>();
      graphQL.setPageSize(review.getPageSize());
      graphQL.setPage(review.getPage());
      graphQL.setTotalPages(review.getTotalPages());
      graphQL.setTotal(review.getTotal());
      graphQL.setItems(review.getItems().stream().map(Converter::toGraphQL).toList());

      return graphQL;
    }


  }


}

