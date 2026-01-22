package com.amalitech.blogging_platform.graphql;

import com.amalitech.blogging_platform.dto.*;
import com.amalitech.blogging_platform.service.CommentService;
import com.amalitech.blogging_platform.service.PostService;
import com.amalitech.blogging_platform.service.UserService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

@Controller
public class CommentGController {
  private final CommentService commentService;
  private final UserService userService;
  private final PostService postService;
  public CommentGController(CommentService commentService, UserService userService, PostService postService) {
    this.commentService = commentService;
    this.userService = userService;
    this.postService = postService;
  }

  @QueryMapping
  public PaginatedData<CommentDTO.GraphQL> comments(@Argument Integer page, @Argument Integer size) {
    return CommentDTO.Converter.toGraphQL(this.commentService.get(new PageRequest(page, size)));
  }

  @QueryMapping
  public CommentDTO.GraphQL commentById(@Argument Long id) {
    return CommentDTO.Converter.toGraphQL(this.commentService.get(id));
  }

  @MutationMapping
  public CommentDTO.GraphQL createComment(@Argument CommentDTO.In input) {
    return CommentDTO.Converter.toGraphQL(this.commentService.create(input));
  }

  @MutationMapping
  public CommentDTO.GraphQL updateComment(@Argument Long id, @Argument String body) {
    return CommentDTO.Converter.toGraphQL(this.commentService.update(id, body));
  }

  @MutationMapping
  public String deleteComment(@Argument Long id) {
    this.commentService.delete(id);
    return "Comment Successfully deleted";
  }

  @SchemaMapping(typeName = "Comment", field = "user")
  public UserDTO.Out user(CommentDTO.GraphQL graphQL) {
    return this.userService.get(graphQL.getUserId());
  }
  @SchemaMapping(typeName = "Comment", field = "post")
  public PostDTO.GraphQL post(CommentDTO.GraphQL graphQL) {
    return PostDTO.Converter.toGraphQL(this.postService.get(graphQL.getPostId()));
  }

  @SchemaMapping(typeName = "Comment", field = "parent")
  public CommentDTO.GraphQL comment(CommentDTO.GraphQL graphQL) {
    try{
      return CommentDTO.Converter.toGraphQL(this.commentService.get(graphQL.getParentCommentId()));
    }catch(Exception ignored){ // if no parent, just return null
      return null;
    }
  }

}
