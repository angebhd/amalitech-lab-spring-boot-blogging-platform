package com.amalitech.blogging_platform.controller.graphql;

import com.amalitech.blogging_platform.dto.*;
import com.amalitech.blogging_platform.exceptions.RessourceNotFoundException;
import com.amalitech.blogging_platform.model.Tag;
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
  private final UserService userService;
  private final ReviewService reviewService;
  private final CommentService commentService;

  @Autowired
  public PostGController(PostService postService, UserService userService,
                         ReviewService reviewService, CommentService commentService) {
    this.postService = postService;
    this.userService = userService;
    this.reviewService = reviewService;
    this.commentService = commentService;
  }

  @QueryMapping
  public PaginatedData<PostDTO.GraphQL> posts(@Argument Integer page, @Argument Integer size) {
//    return PostDTO.Converter.toGraphQL(this.postService.get(new PageRequest(page, size)));
    return null;
  }
  @QueryMapping
  public PostDTO.GraphQL postById(@Argument Long id) {
    return PostDTO.Converter.toGraphQL(this.postService.get(id));
  }

  @QueryMapping
  public PaginatedData<PostDTO.GraphQL> postByAuthorId(@Argument Integer page, @Argument Integer size, @Argument Long id) {

//    return PostDTO.Converter.toGraphQL(this.postService.getByAuthorId(id, new PageRequest(page, size)));
    return null;
  }

  @QueryMapping
  public PaginatedData<PostDTO.GraphQL> postSearch(@Argument Integer page, @Argument Integer size, @Argument String keyword, @Argument Long tagId) {
//    return PostDTO.Converter.fromDetaildtoGraphQL(this.postService.search());
    return null;
  }

  @MutationMapping
  public PostDTO.GraphQL createPost(@Argument PostDTO.In input) {
    return PostDTO.Converter.toGraphQL(this.postService.create(input));
  }

  @MutationMapping
  public PostDTO.GraphQL updatePost(@Argument Long id, @Argument PostDTO.In input) {
    return PostDTO.Converter.toGraphQL(this.postService.update(id, input));
  }

  @MutationMapping
  public String deletePost(@Argument Long id) {
    this.postService.delete(id);
    return "Post successfully deleted";
  }

  @SchemaMapping(typeName = "Post", field = "author")
  public UserDTO.Out author(PostDTO.GraphQL post) {
    try{
    return this.userService.get(post.getAuthorId());
    }catch (RessourceNotFoundException e){
      return null;
    }
  }

  @SchemaMapping(typeName = "Post", field = "reviews")
  public List<ReviewDTO.GraphQL> review(PostDTO.GraphQL post) {
    return this.reviewService.getByPostId(post.getId()).stream().map(ReviewDTO.Converter::toGraphQL).toList();
  }

  @SchemaMapping(typeName = "Post", field = "comments")
  public List<CommentDTO.GraphQL> comment(PostDTO.GraphQL post) {
    return this.commentService.getByPostId(post.getId()).stream().map(CommentDTO.Converter::toGraphQL).toList();
  }

  @SchemaMapping(typeName = "Post", field = "tags")
  public List<Tag> tags(PostDTO.GraphQL post) {
   return List.of();
  }


}
