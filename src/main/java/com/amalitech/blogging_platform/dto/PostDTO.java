package com.amalitech.blogging_platform.dto;


import com.amalitech.blogging_platform.model.Review;
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
    private Long authorId;
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

}
