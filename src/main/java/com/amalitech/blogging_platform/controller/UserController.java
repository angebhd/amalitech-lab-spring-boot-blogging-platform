package com.amalitech.blogging_platform.controller;

import com.amalitech.blogging_platform.dto.GenericResponse;
import com.amalitech.blogging_platform.dto.UserDTO;
import com.amalitech.blogging_platform.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Rest Controller for managing Users
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/user")
@Tag(name = "Users", description = "Manage users (Add, retrieve, update and delete)")
public class UserController {

  private final UserService userService;

  @Autowired
  public UserController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping()
  @Operation(summary = "Get a users in a paginated format")
  @ApiResponse(responseCode= "200", description = "Users retrieved")
  @ApiResponse(responseCode= "409", description = "Invalid params should be integer greater than 0", content = @Content(mediaType = "application/json", schema = @Schema()))
  @ApiResponse(responseCode= "500", description = "Internal server error, please let the backend developer know if it occurred", content = @Content(mediaType = "application/json", schema = @Schema()))
  public ResponseEntity<GenericResponse<Page<UserDTO.Out>>> getUsers(Pageable page){

    var response = new GenericResponse<>(HttpStatus.OK,  this.userService.get(page));
    return ResponseEntity.ok(response);
  }

  @GetMapping("{id}")
  @Operation(summary = "Get a specific user")
  @ApiResponse(responseCode= "200", description = "User retrieved")
  @ApiResponse(responseCode= "404", description = "User not found", content = @Content(mediaType = "application/json", schema = @Schema()))
  @ApiResponse(responseCode= "409", description = "Invalid data", content = @Content(mediaType = "application/json", schema = @Schema()))
  @ApiResponse(responseCode= "500", description = "Internal server error, please let the backend developer know if it occurred", content = @Content(mediaType = "application/json", schema = @Schema()))
  public ResponseEntity<GenericResponse<UserDTO.Out>> getUser(@PathVariable Long id){
    GenericResponse<UserDTO.Out> response = new GenericResponse<>(HttpStatus.OK, this.userService.get(id));
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @GetMapping("/username/{username}")
  @Operation(summary = "Get a specific user by username")
  @ApiResponse(responseCode= "200", description = "User retrieved")
  @ApiResponse(responseCode= "404", description = "User not found", content = @Content(mediaType = "application/json", schema = @Schema()))
  @ApiResponse(responseCode= "409", description = "Invalid data", content = @Content(mediaType = "application/json", schema = @Schema()))
  @ApiResponse(responseCode= "500", description = "Internal server error, please let the backend developer know if it occurred", content = @Content(mediaType = "application/json", schema = @Schema()))
  public ResponseEntity<GenericResponse<UserDTO.Out>> getUserbyUsername(@PathVariable String username){
    GenericResponse<UserDTO.Out> response = new GenericResponse<>(HttpStatus.OK, this.userService.getByUsername(username));
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @PostMapping()
  @Operation(summary = "Create a new user")
  @ApiResponse(responseCode= "201", description = "User created")
  @ApiResponse(responseCode= "409", description = "Invalid data", content = @Content(mediaType = "application/json", schema = @Schema()))
  @ApiResponse(responseCode= "500", description = "Internal server error, please let the backend developer know if it occurred", content = @Content(mediaType = "application/json", schema = @Schema()))
  public ResponseEntity<GenericResponse<UserDTO.Out>> create(@RequestBody @Valid UserDTO.In in){
    UserDTO.Out user = this.userService.create(in);
    var response = new GenericResponse<>(HttpStatus.CREATED, user);
    return ResponseEntity.status(HttpStatusCode.valueOf(response.getStatusCode())).body(response);
  }


  @PutMapping("{id}")
  @Operation(summary = "Update  a user")
  @ApiResponse(responseCode= "200", description = "User updated")
  @ApiResponse(responseCode= "409", description = "Invalid data", content = @Content(mediaType = "application/json", schema = @Schema()))
  @ApiResponse(responseCode= "500", description = "Internal server error, please let the backend developer know if it occurred", content = @Content(mediaType = "application/json", schema = @Schema()))
  public ResponseEntity<GenericResponse<UserDTO.Out>> update(@PathVariable Long id, @RequestBody @Valid UserDTO.In in){
    GenericResponse<UserDTO.Out> resp = new GenericResponse<>(HttpStatus.OK, this.userService.update(id, in));
    return ResponseEntity.ok(resp);
  }

  @DeleteMapping("{id}")
  @Operation(summary = "Delete  a user")
  @ApiResponse(responseCode= "200", description = "User deleted")
  @ApiResponse(responseCode= "409", description = "Invalid data", content = @Content(mediaType = "application/json", schema = @Schema()))
  @ApiResponse(responseCode= "500", description = "Internal server error, please let the backend developer know if it occurred", content = @Content(mediaType = "application/json", schema = @Schema()))

  public ResponseEntity<GenericResponse<UserDTO.Out>> delete(@PathVariable Long id){
    this.userService.delete(id);
    GenericResponse<UserDTO.Out> resp = new GenericResponse<>(HttpStatus.OK, "User deleted", null);
    return ResponseEntity.status(HttpStatusCode.valueOf(resp.getStatusCode())).body(resp);
  }

}
