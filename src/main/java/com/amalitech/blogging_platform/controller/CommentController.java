package com.amalitech.blogging_platform.controller;

import com.amalitech.blogging_platform.dto.*;
import com.amalitech.blogging_platform.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Rest Controller for managing comments
 */
@RestController
@RequestMapping("/api/v1/comment")
@Tag(name = "Comments", description = "Manage commenents (Add, retrieve, update and delete)")
public class CommentController {

  private final CommentService commentService;

  @Autowired
  public CommentController(CommentService commentService) {
    this.commentService = commentService;
  }

  @GetMapping()
  @Operation(summary = "Get a comments in a paginated format")
  @ApiResponse(responseCode= "200", description = "Comments retrieved")
  @ApiResponse(responseCode= "409", description = "Invalid params should be integer greater than 0", content = @Content(mediaType = "application/json", schema = @Schema()))
  @ApiResponse(responseCode= "500", description = "Internal server error, please let the backend developer know if it occurred", content = @Content(mediaType = "application/json", schema = @Schema()))
  public ResponseEntity<GenericResponse<PaginatedData<CommentDTO.Out>>> getComments(@ParameterObject Pageable pageable){
    var response = new GenericResponse<>(HttpStatus.OK,  this.commentService.get(pageable));
    return ResponseEntity.ok(response);
  }


  @GetMapping("{id}")
  @Operation(summary = "Get a specific comment")
  @ApiResponse(responseCode= "200", description = "Post retrieved")
  @ApiResponse(responseCode= "404", description = "Post not found", content = @Content(mediaType = "application/json", schema = @Schema()))
  @ApiResponse(responseCode= "409", description = "Invalid data", content = @Content(mediaType = "application/json", schema = @Schema()))
  @ApiResponse(responseCode= "500", description = "Internal server error, please let the backend developer know if it occurred", content = @Content(mediaType = "application/json", schema = @Schema()))
  public ResponseEntity<GenericResponse<CommentDTO.Out>> getComment(@PathVariable Long id){
    GenericResponse<CommentDTO.Out> response = new GenericResponse<>(HttpStatus.OK, this.commentService.get(id));
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @GetMapping("post/{id}")
  @Operation(summary = "Get a comments by post")
  @ApiResponse(responseCode= "200", description = "Post retrieved")
  @ApiResponse(responseCode= "404", description = "Post not found", content = @Content(mediaType = "application/json", schema = @Schema()))
  @ApiResponse(responseCode= "409", description = "Invalid data", content = @Content(mediaType = "application/json", schema = @Schema()))
  @ApiResponse(responseCode= "500", description = "Internal server error, please let the backend developer know if it occurred", content = @Content(mediaType = "application/json", schema = @Schema()))
  public ResponseEntity<GenericResponse<PaginatedData<CommentDTO.Out>>> getCommentByPosts(@PathVariable Long id, @ParameterObject Pageable pageable){
    GenericResponse<PaginatedData<CommentDTO.Out>> response = new GenericResponse<>(HttpStatus.OK, this.commentService.getByPostId(id, pageable));
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @GetMapping("user/{id}")
  @Operation(summary = "Get a comments by users")
  @ApiResponse(responseCode= "200", description = "Post retrieved")
  @ApiResponse(responseCode= "404", description = "Post not found", content = @Content(mediaType = "application/json", schema = @Schema()))
  @ApiResponse(responseCode= "409", description = "Invalid data", content = @Content(mediaType = "application/json", schema = @Schema()))
  @ApiResponse(responseCode= "500", description = "Internal server error, please let the backend developer know if it occurred", content = @Content(mediaType = "application/json", schema = @Schema()))
  public ResponseEntity<GenericResponse<PaginatedData<CommentDTO.Out>>> getCommentByUser(@PathVariable Long id, @ParameterObject Pageable pageable){
    GenericResponse<PaginatedData<CommentDTO.Out>> response = new GenericResponse<>(HttpStatus.OK, this.commentService.getByUserId(id, pageable));
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @PostMapping()
  @Operation(summary = "Create a new comment")
  @ApiResponse(responseCode= "201", description = "Comment created")
  @ApiResponse(responseCode= "409", description = "Invalid data", content = @Content(mediaType = "application/json", schema = @Schema()))
  @ApiResponse(responseCode= "500", description = "Internal server error, please let the backend developer know if it occurred", content = @Content(mediaType = "application/json", schema = @Schema()))
  public ResponseEntity<GenericResponse<CommentDTO.Out>> create(@RequestBody @Valid CommentDTO.In in){
       return ResponseEntity.status(HttpStatus.CREATED).body(new GenericResponse<>(HttpStatus.CREATED, this.commentService.create(in)));
  }

  @PutMapping("{id}")
  @Operation(summary = "Update  a comment, you can only update the comment body, the remaining is handled by the application")
  @ApiResponse(responseCode= "200", description = "review updated")
  @ApiResponse(responseCode= "409", description = "Invalid data", content = @Content(mediaType = "application/json", schema = @Schema()))
  @ApiResponse(responseCode= "500", description = "Internal server error, please let the backend developer know if it occurred", content = @Content(mediaType = "application/json", schema = @Schema()))
  public ResponseEntity<GenericResponse<CommentDTO.Out>> update(@PathVariable Long id, @RequestBody String in){
    var resp = new GenericResponse<>(HttpStatus.OK, this.commentService.update(id, in));
    return ResponseEntity.ok(resp);
  }

  @DeleteMapping("{id}")
  @Operation(summary = "Delete  a comment")
  @ApiResponse(responseCode= "200", description = "Comment deleted")
  @ApiResponse(responseCode= "409", description = "Invalid data", content = @Content(mediaType = "application/json", schema = @Schema()))
  @ApiResponse(responseCode= "500", description = "Internal server error, please let the backend developer know if it occurred", content = @Content(mediaType = "application/json", schema = @Schema()))
  public ResponseEntity<GenericResponse<Object>> delete(@PathVariable Long id){
    this.commentService.delete(id);
    var resp = new GenericResponse<>(HttpStatus.OK, "Post deleted", null);
    return ResponseEntity.status(HttpStatusCode.valueOf(resp.getStatusCode())).body(resp);
  }
}
