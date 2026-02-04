package com.amalitech.blogging_platform.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

public class AuthDTO {

  private AuthDTO() {}

  @Schema(name = "Login DTO", description = "Username and password")
  @Getter
  public static class  LoginDTO {
    private String username;
    private String password;
  }

  @Schema(name = "Login response", description = "Login response, contains the access token and the authenticated user")
  @Getter
  public static class  LoginResponse {
    @Schema(name = "Access token", description = "Access that will be used to authenticate user")
    private String token;
    private UserDTO.Out user;

    public  LoginResponse(String token, UserDTO.Out user) {
      this.token = token;
      this.user = user;
    }
  }
}

