package com.amalitech.blogging_platform.dto;

import com.amalitech.blogging_platform.model.Tag;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

public class TagDTO {

  @Getter
  @Setter
  @Schema(name = "TagDTOOut")
  public static class Out{
    private Long id;
    private String name;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private boolean isDeleted;
  }

  public static class Converter {
    public static Out toDTO(Tag tag) {
      Out dto = new Out();
      dto.setName(tag.getName());
      dto.setId(tag.getId());
      dto.setCreatedAt(tag.getCreatedAt());
      dto.setUpdatedAt(tag.getUpdatedAt());
      dto.setDeletedAt(tag.getDeletedAt());
      dto.setDeleted(tag.isDeleted());
      return dto;
    }
  }
}

