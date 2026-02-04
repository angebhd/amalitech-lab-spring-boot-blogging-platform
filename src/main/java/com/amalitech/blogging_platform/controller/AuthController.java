package com.amalitech.blogging_platform.controller;

import com.amalitech.blogging_platform.dto.GenericResponse;
import com.amalitech.blogging_platform.dto.AuthDTO;
import com.amalitech.blogging_platform.dto.UserDTO;
import com.amalitech.blogging_platform.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

/**
 * Rest Controller for managing comments
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Autentication", description = "Authentication endpoint, login & signup")
public class AuthController {

  private final AuthService authService;

  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  @PostMapping("login")
  @Operation(summary = "Login")
  @ApiResponse(responseCode= "200", description = "Login successful")
  @ApiResponse(responseCode= "401", description = "Username or password incorrect", content = @Content(mediaType = "application/json", schema = @Schema()))
  @ApiResponse(responseCode= "409", description = "Invalid request", content = @Content(mediaType = "application/json", schema = @Schema()))
  @ApiResponse(responseCode= "500", description = "Internal server error, please let the backend developer know if it occurred", content = @Content(mediaType = "application/json", schema = @Schema()))
  public ResponseEntity<GenericResponse<AuthDTO.LoginResponse>> login(@RequestBody @Valid AuthDTO.LoginDTO loginDTO){
    var response = new GenericResponse<>(HttpStatus.OK,  this.authService.login(loginDTO));
    return ResponseEntity.ok(response);
  }

  @PostMapping("signup")
  @Operation(summary = "Sign up")
  @ApiResponse(responseCode= "201", description = "User created")
  @ApiResponse(responseCode= "409", description = "Invalid data", content = @Content(mediaType = "application/json", schema = @Schema()))
  @ApiResponse(responseCode= "500", description = "Internal server error, please let the backend developer know if it occurred", content = @Content(mediaType = "application/json", schema = @Schema()))
  public ResponseEntity<GenericResponse<UserDTO.Out>> create(@RequestBody @Valid UserDTO.In in){
    UserDTO.Out user = this.authService.signup(in);
    var response = new GenericResponse<>(HttpStatus.CREATED, user);
    return ResponseEntity.status(HttpStatusCode.valueOf(response.getStatusCode())).body(response);
  }

  @GetMapping("/login/oauth2/success")
  public ResponseEntity<GenericResponse<AuthDTO.LoginResponse>> loginSuccess(OAuth2AuthenticationToken authentication) {
    OAuth2User oAuth2User = authentication.getPrincipal();
    var response = new GenericResponse<>(HttpStatus.OK,  this.authService.processOAuthPostLogin(oAuth2User));
    return ResponseEntity.ok(response);
  }



}
