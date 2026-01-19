package com.amalitech.blogging_platform.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

public class UserDTO {
  private UserDTO (){}

  @Getter
  @Setter
  @Schema(name = "UserDTOIn", description = "DTO required by the server to create a new user or update a existing one")
  public static class In{
    @Schema(description = "User's firstname", example = "Ange")
    private String firstName;

    @Schema(description = "User's lastname", example = "Buhendwa")
    private String lastName;

    @Schema(description = "User's username", example = "angebhd")
    @NotBlank(message = "Username should not be empty")
    @Size(min = 4, max = 12, message = "username should be between 4 and 12 characters")
    private String username;

    @Schema(description = "User's email", example = "angebhd@gmail.com")
    @Email
    private String email;

    @Schema(description = "User's password", example = "MyD!fficultP@ssw0rd")
    @NotBlank(message = "Password should not be empty")
    @Size(min = 4, max = 12, message = "Password should be between 4 and 12 characters")
    private String password;
  }

  @Getter
  @Setter
  @Schema(name = "UserDTOOut", description = "DTO returned by the server while fetching user information")
 public static class Out{
    private Long id;
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private boolean isDeleted;
  }
}
