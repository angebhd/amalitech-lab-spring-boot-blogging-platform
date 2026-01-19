package com.amalitech.blogging_platform.controller;

import com.amalitech.blogging_platform.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/tag")
public class TagController {
  private TagService tagService;

  @Autowired
  public TagController(TagService tagService) {
    this.tagService = tagService;
  }
}
