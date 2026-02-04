package com.amalitech.blogging_platform.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public class AuthDTO {

  private AuthDTO() {}

  @Schema(name = "Login DTO", description = "Used for login with username and password, if the do not have a password, she can login with google email")
  public record LoginDTO(
          @Schema(name = "username", description = "User's username")
          String username,
          @Schema(name = "password", description = "User's password")
          String password
  ) {}

  @Schema(
          name = "Login response",
          description = "Login response, contains the access token and the authenticated user"
  )
  public record LoginResponse(
          @Schema(name = "Access token", description = "Access token that will be used to authenticate user" )
          String token,
          @Schema(name = "User", description = "Authenticated user information" )
          UserDTO.Out user
  ) {}
}

