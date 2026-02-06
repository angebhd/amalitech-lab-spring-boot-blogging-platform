package com.amalitech.blogging_platform.controller.graphql;

import com.amalitech.blogging_platform.dto.GraphQLPageableBuilder;
import com.amalitech.blogging_platform.dto.PaginatedData;
import com.amalitech.blogging_platform.dto.TagDTO;
import com.amalitech.blogging_platform.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class TagGController {
  private final TagService tagService;

  @Autowired
  public TagGController(TagService tagService) {
    this.tagService = tagService;
  }

  @QueryMapping
  public PaginatedData<TagDTO.Out> tags(@Argument Integer page, @Argument Integer size, @Argument List<GraphQLPageableBuilder.SortInput> sortBy){
    return this.tagService.get(GraphQLPageableBuilder.get(page, size, sortBy));

  }
  @QueryMapping
  public TagDTO.Out tagById(@Argument Long id){
    return this.tagService.get(id);
  }

  @MutationMapping
  public TagDTO.Out createTag(@Argument String input) {
    return this.tagService.create(input);
  }

  @MutationMapping
  public TagDTO.Out updateTag(@Argument Long id, @Argument String input) {
    return this.tagService.update(id, input);
  }

  @MutationMapping
  public String deleteTag(@Argument Long id) {
    this.tagService.delete(id);
    return "Tag successfully deleted";
  }

}
