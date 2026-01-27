package com.amalitech.blogging_platform.dto;

import com.amalitech.blogging_platform.model.Post;
import com.amalitech.blogging_platform.model.Review;
import com.amalitech.blogging_platform.model.Tag;
import com.amalitech.blogging_platform.model.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;


public class PostDTO {

  private PostDTO(){}

  @Getter
  @Setter
  @Schema(name = "PostDTOIn", description = "DTO required by the server to create a new post or update a existing one")
  public static class In {
    @Positive
    @Schema(description = "The author id", example = "67")
    private Long authorId;

    @NotBlank
    @Size(max = 100, min = 4)
    @Schema(description = "Title of the post", example = "What's new in spring boot 4")
    private String title;
    @Size(max = 700, min = 1)
    @Schema(description = "Post content or body ", example = "The organization in charge of spring boot, has recently release the version 4 of spring,...")
    private String body;

    @Schema(description = "Tags related to the post (New tags are automatically created)", example = "[ TECH, JAVA, SPRING BOOT ]")
    private Set<String> tags;
  }

  @Getter
  @Setter
  @Schema(name = "PostDTOut", description = "DTO returned by the server while fetching post information")
  public static class Out {
    private Long id;
    private UserDTO.Out author;
    private String title;
    private String body;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private boolean isDeleted;
  }

  @Getter
  @Setter
  @Schema(name = "PostDTODetailed", description = "DTO returned by the server while fetching post detailed information")
  public static class Detailed {
    private Long id;
    private Long authorId;
    private String authorName;
    private String title;
    private String body;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private boolean isDeleted;
    private Set<String> tags;
    private List<Review>  reviews;
    private List<CommentDTO.Out> comments;
  }

  @Getter
  @Setter
  public static class GraphQL{
    private Long id;
    private UserDTO.Out author;
    private Long authorId;
    private String title;
    private String body;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private boolean isDeleted;
    private List<Tag> tags;
    private List<Review>  reviews;
    private List<CommentDTO.Out> comments;
  }

  public static class Converter{
    private Converter(){}

    public static Out toDTO(Post post){
      PostDTO.Out dto = new PostDTO.Out();
      dto.setId(post.getId());
      dto.setTitle(post.getTitle());
      dto.setAuthor(UserDTO.Converter.toDTO(post.getAuthor()));
      dto.setBody(post.getBody());
      dto.setCreatedAt(post.getCreatedAt());
      dto.setUpdatedAt(post.getUpdatedAt());
      dto.setDeletedAt(post.getDeletedAt());
      dto.setDeleted(post.isDeleted());
      return dto;
    }

    public static PostDTO.GraphQL  toGraphQL(PostDTO.Detailed detailed){
      PostDTO.GraphQL graphQL = new PostDTO.GraphQL();
      graphQL.setId(detailed.getId());
      graphQL.setTitle(detailed.getTitle());
      graphQL.setAuthorId(detailed.getAuthorId());
      graphQL.setBody(detailed.getBody());
      graphQL.setCreatedAt(detailed.getCreatedAt());
      graphQL.setUpdatedAt(detailed.getUpdatedAt());
      graphQL.setDeletedAt(detailed.getDeletedAt());
      graphQL.setDeleted(detailed.isDeleted());
      return graphQL;
    }

    public static PostDTO.GraphQL  toGraphQL(PostDTO.Out out){
      PostDTO.GraphQL graphQL = new PostDTO.GraphQL();
      graphQL.setId(out.getId());
      graphQL.setTitle(out.getTitle());
      graphQL.setBody(out.getBody());
      graphQL.setCreatedAt(out.getCreatedAt());
      graphQL.setUpdatedAt(out.getUpdatedAt());
      graphQL.setDeletedAt(out.getDeletedAt());
      graphQL.setDeleted(out.isDeleted());
      return graphQL;
    }


      public static PaginatedData<PostDTO.GraphQL>  toGraphQL(PaginatedData<PostDTO.Out> out){
      PaginatedData<PostDTO.GraphQL> graphQL = new PaginatedData<>();
      graphQL.setPage(out.getPage());
      graphQL.setPageSize(out.getPageSize());
      graphQL.setTotal(out.getTotal());
      graphQL.setTotalPages(out.getTotalPages());
      graphQL.setItems(out.getItems().stream().map(Converter::toGraphQL).toList());

      return graphQL;
    }

    public static PaginatedData<PostDTO.GraphQL>  fromDetaildtoGraphQL(PaginatedData<PostDTO.Detailed> detailed){
      PaginatedData<PostDTO.GraphQL> graphQL = new PaginatedData<>();
      graphQL.setPage(detailed.getPage());
      graphQL.setPageSize(detailed.getPageSize());
      graphQL.setTotal(detailed.getTotal());
      graphQL.setTotalPages(detailed.getTotalPages());
      graphQL.setItems(detailed.getItems().stream().map(Converter::toGraphQL).toList());

      return graphQL;
    }



  }

}
