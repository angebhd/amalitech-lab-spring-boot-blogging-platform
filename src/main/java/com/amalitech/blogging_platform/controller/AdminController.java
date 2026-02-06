package com.amalitech.blogging_platform.controller;

import com.amalitech.blogging_platform.dto.AuthDTO;
import com.amalitech.blogging_platform.dto.GenericResponse;
import com.amalitech.blogging_platform.dto.UserDTO;
import com.amalitech.blogging_platform.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasRole('ADMIN')")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Admin endpoints", description = "Admin endpoints")
public class AdminController {
  private  final AdminService adminService;
  public AdminController(AdminService adminService) {
    this.adminService = adminService;
  }

  @PostMapping("view-token-payload")
  @Operation(summary = "Get token claims")
  @ApiResponse(responseCode= "200", description = "Login successful")
  @ApiResponse(responseCode= "401", description = "Authentication failed, please login and send a correct token", content = @Content(mediaType = "application/json", schema = @Schema()))
  @ApiResponse(responseCode= "403", description = "You don't have the right to do these operation", content = @Content(mediaType = "application/json", schema = @Schema()))
  @ApiResponse(responseCode= "409", description = "Invalid request", content = @Content(mediaType = "application/json", schema = @Schema()))
  @ApiResponse(responseCode= "500", description = "Internal server error, please let the backend developer know if it occurred", content = @Content(mediaType = "application/json", schema = @Schema()))
  public ResponseEntity<GenericResponse<AuthDTO.TokenPayload>> login(@RequestBody String token){
    var response = new GenericResponse<>(HttpStatus.OK,  this.adminService.getTokenPayload(token));
    return ResponseEntity.ok(response);
  }

  @PostMapping("make-admin")
  @Operation(summary = "Get token claims")
  @ApiResponse(responseCode= "200", description = "Login successful")
  @ApiResponse(responseCode= "401", description = "Authentication failed, please login and send a correct token", content = @Content(mediaType = "application/json", schema = @Schema()))
  @ApiResponse(responseCode= "403", description = "You don't have the right to do these operation", content = @Content(mediaType = "application/json", schema = @Schema()))
  @ApiResponse(responseCode= "409", description = "Invalid request", content = @Content(mediaType = "application/json", schema = @Schema()))
  @ApiResponse(responseCode= "500", description = "Internal server error, please let the backend developer know if it occurred", content = @Content(mediaType = "application/json", schema = @Schema()))
  public ResponseEntity<GenericResponse<UserDTO.Out>> makeAdmin(@RequestBody Long id){
    var response = new GenericResponse<>(HttpStatus.OK,  this.adminService.makeAdmin(id));
    return ResponseEntity.ok(response);
  }

  @PostMapping("remove-admin")
  @Operation(summary = "Get token claims")
  @ApiResponse(responseCode= "200", description = "Login successful")
  @ApiResponse(responseCode= "401", description = "Authentication failed, please login and send a correct token", content = @Content(mediaType = "application/json", schema = @Schema()))
  @ApiResponse(responseCode= "403", description = "You don't have the right to do these operation", content = @Content(mediaType = "application/json", schema = @Schema()))
  @ApiResponse(responseCode= "409", description = "Invalid request", content = @Content(mediaType = "application/json", schema = @Schema()))
  @ApiResponse(responseCode= "500", description = "Internal server error, please let the backend developer know if it occurred", content = @Content(mediaType = "application/json", schema = @Schema()))
  public ResponseEntity<GenericResponse<UserDTO.Out>> removeAdmin(@RequestBody Long id){
    var response = new GenericResponse<>(HttpStatus.OK,  this.adminService.removeAdmin(id));
    return ResponseEntity.ok(response);
  }

}
