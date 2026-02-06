package com.amalitech.blogging_platform.controller.graphql;

import com.amalitech.blogging_platform.dto.AuthDTO;
import com.amalitech.blogging_platform.service.AuthService;
import jakarta.annotation.security.PermitAll;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

@Controller
public class AuthGController {
  private final AuthService authService;

  public AuthGController(AuthService authService) {
    this.authService = authService;
  }
  @MutationMapping
  @PermitAll
  public AuthDTO.LoginResponse login(@Argument String username, @Argument String password) {
    return this.authService.login(new AuthDTO.LoginDTO(username, password));
  }
}
