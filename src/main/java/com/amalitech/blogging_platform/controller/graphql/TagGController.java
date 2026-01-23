package com.amalitech.blogging_platform.controller.graphql;

import com.amalitech.blogging_platform.dto.PageRequest;
import com.amalitech.blogging_platform.dto.PaginatedData;
import com.amalitech.blogging_platform.model.Tag;
import com.amalitech.blogging_platform.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
public class TagGController {
  private final TagService tagService;

  @Autowired
  public TagGController(TagService tagService) {
    this.tagService = tagService;
  }

  @QueryMapping
  public PaginatedData<Tag> tags(@Argument Integer page, @Argument Integer size){
    return this.tagService.get(new PageRequest(page, size));
  }
  @QueryMapping
  public Tag tagById(@Argument Long id){
    return this.tagService.get(id);
  }

  @MutationMapping
  public Tag createTag(@Argument String input) {
    return this.tagService.create(input);
  }

  @MutationMapping
  public Tag updateTag(@Argument Long id, @Argument String input) {
    return this.tagService.update(id, input);
  }

  @MutationMapping
  public String deleteTag(@Argument Long id) {
    this.tagService.delete(id);
    return "Tag successfully deleted";
  }

}
