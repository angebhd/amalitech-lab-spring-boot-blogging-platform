package com.amalitech.blogging_platform.controller;

import com.amalitech.blogging_platform.dto.*;
import com.amalitech.blogging_platform.model.EReview;
import com.amalitech.blogging_platform.model.Review;
import com.amalitech.blogging_platform.service.PostService;
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

@RestController
@RequestMapping("/api/v1/post")
@Tag(name = "Posts", description = "Manage posts (Add, delete, update and delete)")
public class PostController {

  private PostService postService;

  @Autowired
  public PostController(PostService postService) {
    this.postService = postService;
  }

  @GetMapping()
  @Operation(summary = "Get a posts in a paginated format")
  @ApiResponse(responseCode= "200", description = "Posts retrieved")
  @ApiResponse(responseCode= "409", description = "Invalid params should be integer greater than 0", content = @Content(mediaType = "application/json", schema = @Schema()))
  @ApiResponse(responseCode= "500", description = "Internal server error, please let the backend developer know if it occurred", content = @Content(mediaType = "application/json", schema = @Schema()))
  public ResponseEntity<GenericResponse<PaginatedData<PostDTO.Out>>> getPosts(@ModelAttribute PageRequest pageRequest){
    var response = new GenericResponse<>(HttpStatus.OK,  this.postService.get(pageRequest));
    return ResponseEntity.ok(response);
  }

  @GetMapping("{id}")
  @Operation(summary = "Get a specific post")
  @ApiResponse(responseCode= "200", description = "Post retrieved")
  @ApiResponse(responseCode= "404", description = "Post not found", content = @Content(mediaType = "application/json", schema = @Schema()))
  @ApiResponse(responseCode= "409", description = "Invalid data", content = @Content(mediaType = "application/json", schema = @Schema()))
  @ApiResponse(responseCode= "500", description = "Internal server error, please let the backend developer know if it occurred", content = @Content(mediaType = "application/json", schema = @Schema()))
  public ResponseEntity<GenericResponse<PostDTO.Out>> getPost(@PathVariable Long id){
    GenericResponse<PostDTO.Out> response = new GenericResponse<>(HttpStatus.OK, this.postService.get(id));
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @PostMapping()
  @Operation(summary = "Create a new post")
  @ApiResponse(responseCode= "201", description = "Comment created")
  @ApiResponse(responseCode= "409", description = "Invalid data", content = @Content(mediaType = "application/json", schema = @Schema()))
  @ApiResponse(responseCode= "500", description = "Internal server error, please let the backend developer know if it occurred", content = @Content(mediaType = "application/json", schema = @Schema()))
  public ResponseEntity<GenericResponse<PostDTO.Out>> create(@RequestBody @Valid PostDTO.In in){
    PostDTO.Out post = this.postService.create(in);
    return ResponseEntity.status(HttpStatus.CREATED).body(new GenericResponse<>(HttpStatus.CREATED, post));
  }

  @PutMapping("{id}")
  @Operation(summary = "Update  a post, the tags are ignored, use the /api/post/tag to update tags")
  @ApiResponse(responseCode= "200", description = "review updated")
  @ApiResponse(responseCode= "409", description = "Invalid data", content = @Content(mediaType = "application/json", schema = @Schema()))
  @ApiResponse(responseCode= "500", description = "Internal server error, please let the backend developer know if it occurred", content = @Content(mediaType = "application/json", schema = @Schema()))
  public ResponseEntity<GenericResponse<PostDTO.Out>> update(@PathVariable Long id, @RequestBody PostDTO.In in){
    var resp = new GenericResponse<>(HttpStatus.OK, this.postService.update(id, in));
    return ResponseEntity.ok(resp);
  }

  @DeleteMapping("{id}")
  @Operation(summary = "Delete  a Post")
  @ApiResponse(responseCode= "200", description = "Post deleted")
  @ApiResponse(responseCode= "409", description = "Invalid data", content = @Content(mediaType = "application/json", schema = @Schema()))
  @ApiResponse(responseCode= "500", description = "Internal server error, please let the backend developer know if it occurred", content = @Content(mediaType = "application/json", schema = @Schema()))
  public ResponseEntity<GenericResponse<PostDTO.Out>> delete(@PathVariable Long id){
    this.postService.delete(id);
    var resp = new GenericResponse<PostDTO.Out>(HttpStatus.OK, "Post deleted", null);
    return ResponseEntity.status(HttpStatusCode.valueOf(resp.getStatusCode())).body(resp);
  }
}
