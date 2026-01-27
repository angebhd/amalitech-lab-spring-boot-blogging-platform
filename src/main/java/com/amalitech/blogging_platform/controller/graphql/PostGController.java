package com.amalitech.blogging_platform.controller.graphql;

import com.amalitech.blogging_platform.dto.*;
import com.amalitech.blogging_platform.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Slf4j
@Controller
public class PostGController {

  private final PostService postService;

  @Autowired
  public PostGController(PostService postService) {
    this.postService = postService;
  }

  @QueryMapping
  public PaginatedData<PostDTO.Out> posts(@Argument int page, @Argument int size, @Argument List<GraphQLPageableBuilder.SortInput> sortBy) {
    return this.postService.get(GraphQLPageableBuilder.get(page, size, sortBy));
  }
  @QueryMapping
  public PostDTO.Out postById(@Argument Long id) {
    return this.postService.get(id);
  }

  @QueryMapping
  public PaginatedData<PostDTO.Out> postByAuthorId(@Argument int page, @Argument int size, @Argument List<GraphQLPageableBuilder.SortInput> sortBy, @Argument Long id) {
    return this.postService.getByAuthorId(id, GraphQLPageableBuilder.get(page, size, sortBy));
  }

  @QueryMapping
  public PaginatedData<PostDTO.Out> postSearch(@Argument int page, @Argument int size, @Argument List<GraphQLPageableBuilder.SortInput> sortBy, @Argument String keyword) {
    return this.postService.search(keyword, GraphQLPageableBuilder.get(page, size, sortBy));
  }

  @MutationMapping
  public PostDTO.Out createPost(@Argument PostDTO.In input) {
    return this.postService.create(input);
  }

  @MutationMapping
  public PostDTO.Out updatePost(@Argument Long id, @Argument PostDTO.In input) {
    return this.postService.update(id, input);
  }

  @MutationMapping
  public String deletePost(@Argument Long id) {
    this.postService.delete(id);
    return "Post successfully deleted";
  }

  @SchemaMapping(typeName = "Post", field = "author")
  public UserDTO.Out author(PostDTO.Out post) {
    return post.getAuthor();

  }

  @SchemaMapping(typeName = "Post", field = "tags")
  public List<TagDTO.Out> tags(PostDTO.Out post) {
    return post.getTags();
  }


}
