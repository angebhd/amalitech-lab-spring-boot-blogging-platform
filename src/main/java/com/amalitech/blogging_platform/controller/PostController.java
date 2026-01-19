package com.amalitech.blogging_platform.controller;

import com.amalitech.blogging_platform.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/post")
public class PostController {

  private PostService postService;

  @Autowired
  public PostController(PostService postService) {
    this.postService = postService;
  }

  @GetMapping()
  public ResponseEntity<String> getByUserId(Long userId){
    return new ResponseEntity<>("Hello ! Spring", HttpStatus.ACCEPTED);
  }
}
