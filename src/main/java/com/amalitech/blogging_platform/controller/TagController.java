package com.amalitech.blogging_platform.controller;

import com.amalitech.blogging_platform.dto.GenericResponse;
import com.amalitech.blogging_platform.dto.PageRequest;
import com.amalitech.blogging_platform.dto.PaginatedData;
import com.amalitech.blogging_platform.model.Tag;
import com.amalitech.blogging_platform.service.TagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/tag")
@io.swagger.v3.oas.annotations.tags.Tag(name = "Tags", description = "Manage tags (Add, retrieve, update and delete)")
public class TagController {
  private final TagService tagService;

  @Autowired
  public TagController(TagService tagService) {
    this.tagService = tagService;
  }
  @GetMapping()
  @Operation(summary = "Get a tags in a paginated format")
  @ApiResponse(responseCode= "200", description = "Tags retrieved")
  @ApiResponse(responseCode= "409", description = "Invalid params should be integer greater than 0", content = @Content(mediaType = "application/json", schema = @Schema()))
  @ApiResponse(responseCode= "500", description = "Internal server error, please let the backend developer know if it occurred", content = @Content(mediaType = "application/json", schema = @Schema()))
  public ResponseEntity<GenericResponse<PaginatedData<Tag>>> getTags(@ModelAttribute PageRequest pageRequest){
    var response = new GenericResponse<>(HttpStatus.OK,  this.tagService.get(pageRequest));
    return ResponseEntity.ok(response);
  }

  @GetMapping("{id}")
  @Operation(summary = "Get a tag by ID")
  @ApiResponse(responseCode= "200", description = "Tag retrieved")
  @ApiResponse(responseCode= "404", description = "Tag not found", content = @Content(mediaType = "application/json", schema = @Schema()))
  @ApiResponse(responseCode= "500", description = "Internal server error, please let the backend developer know if it occurred", content = @Content(mediaType = "application/json", schema = @Schema()))
  public ResponseEntity<GenericResponse<Tag>> getTag(@PathVariable Long id){
    var response = new GenericResponse<>(HttpStatus.OK,  this.tagService.get(id));
    return ResponseEntity.ok(response);
  }

  @PostMapping()
  @Operation(summary = "Create tags")
  @ApiResponse(responseCode= "201", description = "Tags created")
  @ApiResponse(responseCode= "409", description = "Invalid params should be integer greater than 0", content = @Content(mediaType = "application/json", schema = @Schema()))
  @ApiResponse(responseCode= "500", description = "Internal server error, please let the backend developer know if it occurred", content = @Content(mediaType = "application/json", schema = @Schema()))
  public ResponseEntity<GenericResponse<Tag>> create(@RequestBody String tags){
    var response = new GenericResponse<>(HttpStatus.CREATED,  this.tagService.create(tags));
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @PutMapping("{id}")
  @Operation(summary = "Update tags")
  @ApiResponse(responseCode= "200", description = "Tags updateg")
  @ApiResponse(responseCode= "409", description = "Invalid params should be integer greater than 0", content = @Content(mediaType = "application/json", schema = @Schema()))
  @ApiResponse(responseCode= "500", description = "Internal server error, please let the backend developer know if it occurred", content = @Content(mediaType = "application/json", schema = @Schema()))
  public ResponseEntity<GenericResponse<Tag>> update(@PathVariable Long id, @RequestBody String tag){
    var response = new GenericResponse<>(HttpStatus.OK,  this.tagService.update(id, tag ));
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @DeleteMapping("{id}")
  @Operation(summary = "Create tags")
  @ApiResponse(responseCode= "201", description = "Tags created")
  @ApiResponse(responseCode= "409", description = "Invalid params should be integer greater than 0", content = @Content(mediaType = "application/json", schema = @Schema()))
  @ApiResponse(responseCode= "500", description = "Internal server error, please let the backend developer know if it occurred", content = @Content(mediaType = "application/json", schema = @Schema()))
  public ResponseEntity<GenericResponse<Object>> delete(@PathVariable Long id){
    this.tagService.delete(id);
    var response = new GenericResponse<>(HttpStatus.OK, "Tag sucessfully deleted",null);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }


}
