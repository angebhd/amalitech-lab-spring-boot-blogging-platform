package com.amalitech.blogging_platform.controller;

import com.amalitech.blogging_platform.dto.GenericResponse;
import com.amalitech.blogging_platform.dto.PaginatedData;
import com.amalitech.blogging_platform.dto.UserDTO;
import com.amalitech.blogging_platform.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.awt.print.Pageable;

@RestController
@RequestMapping("/api/v1/user")
@Tag(name = "Users", description = "Manage users (Add, delete, update and delete)")
public class UserController {

  private final UserService userService;

  @Autowired
  public UserController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping()
  public GenericResponse<PaginatedData<UserDTO.Out>> getUsers(Pageable pageable){
    return null;
  }

  @GetMapping("{id}")
  public GenericResponse<UserDTO.Out> getUser(@PathVariable Long id){
    UserDTO.Out userDTO = new UserDTO.Out();
    if(userDTO == null){
      return new GenericResponse<>(HttpStatus.NOT_FOUND, "Not found", null);
    }
    return new GenericResponse<>(HttpStatus.ACCEPTED , "Sucess", userDTO);
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


  @PutMapping()
  public GenericResponse<UserDTO.Out> update(){
    return null;
  }


  @DeleteMapping()
  public GenericResponse<UserDTO.Out> delete(){
    return null;
  }


}
