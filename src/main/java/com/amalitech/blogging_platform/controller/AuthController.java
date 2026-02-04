package com.amalitech.blogging_platform.controller;

import com.amalitech.blogging_platform.dto.GenericResponse;
import com.amalitech.blogging_platform.service.AuthDTO;
import com.amalitech.blogging_platform.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

  @PostMapping()
  @Operation(summary = "Login")
  @ApiResponse(responseCode= "200", description = "Login successful")
  @ApiResponse(responseCode= "401", description = "Username or password incorrect", content = @Content(mediaType = "application/json", schema = @Schema()))
  @ApiResponse(responseCode= "409", description = "Invalid request", content = @Content(mediaType = "application/json", schema = @Schema()))
  @ApiResponse(responseCode= "500", description = "Internal server error, please let the backend developer know if it occurred", content = @Content(mediaType = "application/json", schema = @Schema()))
  public ResponseEntity<GenericResponse<AuthDTO.LoginResponse>> login(@RequestBody @Valid AuthDTO.LoginDTO loginDTO){
    var response = new GenericResponse<>(HttpStatus.OK,  this.authService.login(loginDTO));
    return ResponseEntity.ok(response);
  }



}
