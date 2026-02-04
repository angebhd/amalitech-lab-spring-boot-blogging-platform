package com.amalitech.blogging_platform.controller;

import com.amalitech.blogging_platform.dto.*;
import com.amalitech.blogging_platform.model.EReview;
import com.amalitech.blogging_platform.model.Review;
import com.amalitech.blogging_platform.service.ReviewService;
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
 * Rest Controller for managing Reviews
 */
@RestController
@RequestMapping("/api/v1/review")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Reviews", description = "Manage Reviews (Add, retrieve, update and delete)")
public class ReviewController {

  private final ReviewService reviewService;

  @Autowired
  public ReviewController(ReviewService reviewService) {
    this.reviewService = reviewService;
  }

  @GetMapping()
  @Operation(summary = "Get a reviews in a paginated format")
  @ApiResponse(responseCode= "200", description = "Reviews retrieved")
  @ApiResponse(responseCode= "409", description = "Invalid params should be integer greater than 0", content = @Content(mediaType = "application/json", schema = @Schema()))
  @ApiResponse(responseCode= "500", description = "Internal server error, please let the backend developer know if it occurred", content = @Content(mediaType = "application/json", schema = @Schema()))
  public ResponseEntity<GenericResponse<PaginatedData<ReviewDTO.Out>>> getReview(@ParameterObject Pageable pageable){
    var response = new GenericResponse<>(HttpStatus.OK,  this.reviewService.get(pageable));
    return ResponseEntity.ok(response);
  }

  @GetMapping("post/{id}")
  @Operation(summary = "Get a reviews in a paginated format")
  @ApiResponse(responseCode= "200", description = "Reviews retrieved")
  @ApiResponse(responseCode= "409", description = "Invalid params should be integer greater than 0", content = @Content(mediaType = "application/json", schema = @Schema()))
  @ApiResponse(responseCode= "500", description = "Internal server error, please let the backend developer know if it occurred", content = @Content(mediaType = "application/json", schema = @Schema()))
  public ResponseEntity<GenericResponse<PaginatedData<ReviewDTO.Out>>> getReviewByPost(@PathVariable Long id, @ParameterObject Pageable pageable){
    var response = new GenericResponse<>(HttpStatus.OK,  this.reviewService.getByPostId(id, pageable));
    return ResponseEntity.ok(response);
  }

  @GetMapping("user/{id}")
  @Operation(summary = "Get a reviews in a paginated format")
  @ApiResponse(responseCode= "200", description = "Reviews retrieved")
  @ApiResponse(responseCode= "409", description = "Invalid params should be integer greater than 0", content = @Content(mediaType = "application/json", schema = @Schema()))
  @ApiResponse(responseCode= "500", description = "Internal server error, please let the backend developer know if it occurred", content = @Content(mediaType = "application/json", schema = @Schema()))
  public ResponseEntity<GenericResponse<PaginatedData<ReviewDTO.Out>>> getReviewByUser(@PathVariable Long id, @ParameterObject Pageable pageable){
    var response = new GenericResponse<>(HttpStatus.OK,  this.reviewService.getByUserId(id, pageable));
    return ResponseEntity.ok(response);
  }

  @GetMapping("{id}")
  @Operation(summary = "Get a specific review")
  @ApiResponse(responseCode= "200", description = "review retrieved")
  @ApiResponse(responseCode= "404", description = "review not found", content = @Content(mediaType = "application/json", schema = @Schema()))
  @ApiResponse(responseCode= "409", description = "Invalid path variable", content = @Content(mediaType = "application/json", schema = @Schema()))
  @ApiResponse(responseCode= "500", description = "Internal server error, please let the backend developer know if it occurred", content = @Content(mediaType = "application/json", schema = @Schema()))
  public ResponseEntity<GenericResponse<ReviewDTO.Out>> getUser(@PathVariable Long id){
    var response = new GenericResponse<>(HttpStatus.OK, this.reviewService.get(id));
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @PostMapping()
  @Operation(summary = "Create a new review")
  @ApiResponse(responseCode= "201", description = "review created")
  @ApiResponse(responseCode= "409", description = "Invalid data", content = @Content(mediaType = "application/json", schema = @Schema()))
  @ApiResponse(responseCode= "500", description = "Internal server error, please let the backend developer know if it occurred", content = @Content(mediaType = "application/json", schema = @Schema()))
  public ResponseEntity<GenericResponse<ReviewDTO.Out>> create(@RequestBody @Valid ReviewDTO.In in){
    ReviewDTO.Out review = this.reviewService.create(in);
    var response = new GenericResponse<>(HttpStatus.CREATED, review);
    return ResponseEntity.status(HttpStatusCode.valueOf(response.getStatusCode())).body(response);
  }

  @PutMapping("{id}")
  @Operation(summary = "Update  a review")
  @ApiResponse(responseCode= "200", description = "review updated")
  @ApiResponse(responseCode= "409", description = "Invalid data", content = @Content(mediaType = "application/json", schema = @Schema()))
  @ApiResponse(responseCode= "500", description = "Internal server error, please let the backend developer know if it occurred", content = @Content(mediaType = "application/json", schema = @Schema()))
  public ResponseEntity<GenericResponse<ReviewDTO.Out>> update(@PathVariable Long id, @RequestBody EReview in){
    var resp = new GenericResponse<>(HttpStatus.OK, this.reviewService.update(id, in));
    return ResponseEntity.ok(resp);
  }

  @DeleteMapping("{id}")
  @Operation(summary = "Delete  a review")
  @ApiResponse(responseCode= "200", description = "review deleted")
  @ApiResponse(responseCode= "409", description = "Invalid data", content = @Content(mediaType = "application/json", schema = @Schema()))
  @ApiResponse(responseCode= "500", description = "Internal server error, please let the backend developer know if it occurred", content = @Content(mediaType = "application/json", schema = @Schema()))
  public ResponseEntity<GenericResponse<Review>> delete(@PathVariable Long id){
    this.reviewService.delete(id);
    var resp = new GenericResponse<Review>(HttpStatus.OK, "Review deleted", null);
    return ResponseEntity.status(HttpStatusCode.valueOf(resp.getStatusCode())).body(resp);
  }
}
