package com.amalitech.blogging_platform.controller;

import com.amalitech.blogging_platform.dto.*;
import com.amalitech.blogging_platform.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
 * Rest Controller for managing Posts
 */
@RestController
@RequestMapping("/api/v1/post")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Posts", description = "Manage posts (Add, retrieve, update and delete)")
public class PostController {

  private final PostService postService;

  @Autowired
  public PostController(PostService postService) {
    this.postService = postService;
  }

  @GetMapping()
  @Operation(summary = "Get a posts in a paginated format")
  @ApiResponse(responseCode= "200", description = "Posts retrieved")
  @ApiResponse(responseCode= "409", description = "Invalid params should be integer greater than 0", content = @Content(mediaType = "application/json", schema = @Schema()))
  @ApiResponse(responseCode= "500", description = "Internal server error, please let the backend developer know if it occurred", content = @Content(mediaType = "application/json", schema = @Schema()))
  public ResponseEntity<GenericResponse<PaginatedData<PostDTO.Out>>> getPosts(@ParameterObject Pageable page){
    var response = new GenericResponse<>(HttpStatus.OK,  this.postService.get(page));
    return ResponseEntity.ok(response);
  }

  @GetMapping("feed")
  @Operation(summary = "Get a posts for feed (with stats) in a paginated format")
  @ApiResponse(responseCode= "200", description = "Posts retrieved")
  @ApiResponse(responseCode= "409", description = "Invalid params should be integer greater than 0", content = @Content(mediaType = "application/json", schema = @Schema()))
  @ApiResponse(responseCode= "500", description = "Internal server error, please let the backend developer know if it occurred", content = @Content(mediaType = "application/json", schema = @Schema()))
  public ResponseEntity<GenericResponse<PaginatedData<PostDTO.OutWithStats>>> getPostFeed(@ParameterObject Pageable page){
    var response = new GenericResponse<>(HttpStatus.OK,  this.postService.getFeed(page));
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


  @GetMapping("/author/{id}")
  @Operation(summary = "Get a posts of an author in a paginated format")
  @ApiResponse(responseCode= "200", description = "Posts retrieved")
  @ApiResponse(responseCode= "409", description = "Invalid params should be integer greater than 0", content = @Content(mediaType = "application/json", schema = @Schema()))
  @ApiResponse(responseCode= "500", description = "Internal server error, please let the backend developer know if it occurred", content = @Content(mediaType = "application/json", schema = @Schema()))
  public ResponseEntity<GenericResponse<PaginatedData<PostDTO.Out>>> getByAuthor(@PathVariable Long id, @ParameterObject Pageable pageable){
    var response = new GenericResponse<>(HttpStatus.OK,  this.postService.getByAuthorId(id, pageable));
    return ResponseEntity.ok(response);
  }

  @GetMapping("search")
  @Operation(summary = "Search comments by title, author content and filter by tags")
  @ApiResponse(responseCode= "200", description = "Post retrieved")
  @ApiResponse(responseCode= "404", description = "Post not found", content = @Content(mediaType = "application/json", schema = @Schema()))
  @ApiResponse(responseCode= "409", description = "Invalid data", content = @Content(mediaType = "application/json", schema = @Schema()))
  @ApiResponse(responseCode= "500", description = "Internal server error, please let the backend developer know if it occurred", content = @Content(mediaType = "application/json", schema = @Schema()))
  public ResponseEntity<GenericResponse<PaginatedData<PostDTO.OutWithStats>>> search(@ParameterObject Pageable page, @RequestParam(required = false) String keyword ){
    GenericResponse<PaginatedData<PostDTO.OutWithStats>> response = new GenericResponse<>(HttpStatus.OK, this.postService.search(keyword, page));

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
  @Operation(summary = "Update  a post")
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
