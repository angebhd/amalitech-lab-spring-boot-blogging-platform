package com.amalitech.blogging_platform.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;


public class PostDTO {

  @Getter
  @Setter
  @Schema(name = "PostDTOIn", description = "DTO required by the server to create a new post or update a existing one")
  public static class In {
    @Positive
    private Long authorId;
    @NotBlank
    @Size(max = 100, min = 4)
    private String title;
    @Size(max = 700, min = 1)
    private String body;
    private Set<String> tags;
  }

  @Getter
  @Setter
  @Schema(name = "PostDTOut", description = "DTO returned by the server while fetching user information")
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

}
