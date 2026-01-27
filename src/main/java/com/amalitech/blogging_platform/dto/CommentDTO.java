package com.amalitech.blogging_platform.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * DTOs related to comment operations.
 */
public class CommentDTO {

  private CommentDTO(){}

  @Getter
  @Setter
  @Schema(name = "CommentDTOIn", description = "DTO required by the server to create a new comment")
  public static class In{
    @Positive
    @NotNull
    private Long postId;
    @Positive
    @NotNull
    private Long userId;
    @NotBlank
    private String body;
    @Positive
    private Long parentCommentId;
  }
  @Getter
  @Setter
  public  static  class Out{
    private Long id;
    private Long postId;
    private UserDTO.Out user;
    private String body;
    private Long parentCommentId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private boolean isDeleted;
  }

  @Getter
  @Setter
  public  static  class GraphQL{
    private Long id;
    private Long postId;
    private Long userId;
    private PostDTO.GraphQL post;
    private UserDTO.Out user;
    private String body;
    private Long parentCommentId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private boolean isDeleted;
  }

  public static class Converter{
    private Converter(){}

    public static GraphQL toGraphQL(CommentDTO.Out comment){
      GraphQL graphQL = new GraphQL();
      graphQL.setId(comment.getId());
      graphQL.setPostId(comment.getPostId());
      graphQL.setUserId(comment.getUser().getId());
      graphQL.setBody(comment.getBody());
      graphQL.setParentCommentId(comment.getParentCommentId());
      graphQL.setCreatedAt(comment.getCreatedAt());
      graphQL.setUpdatedAt(comment.getUpdatedAt());
      graphQL.setDeletedAt(comment.getDeletedAt());
      graphQL.setDeleted(comment.isDeleted());
      return graphQL;
    }

    public static PaginatedData<GraphQL> toGraphQL(PaginatedData<CommentDTO.Out> comments){
      PaginatedData<GraphQL> graphQL = new PaginatedData<>();
      graphQL.setPageSize(comments.getPageSize());
      graphQL.setPage(comments.getPage());
      graphQL.setTotalPages(comments.getTotalPages());
      graphQL.setTotal(comments.getTotal());
      graphQL.setItems(comments.getItems().stream().map(Converter::toGraphQL).toList());

      return graphQL;
    }


  }

}
