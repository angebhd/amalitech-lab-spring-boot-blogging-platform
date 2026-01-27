package com.amalitech.blogging_platform.controller.graphql;

import com.amalitech.blogging_platform.dto.*;
import com.amalitech.blogging_platform.service.CommentService;
import com.amalitech.blogging_platform.service.PostService;
import com.amalitech.blogging_platform.service.UserService;
import org.springframework.data.domain.Pageable;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

/**
 * GraphQL controller (resolver) for managing comments.
 * <p>
 * Provides queries and mutations for retrieving, creating, updating, and deleting comments.
 * Also resolves related fields like user, post, and parent comment.
 */
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


  /**
   * Fetch paginated comments.
   *
   * @param page page number (1-based)
   * @param size number of comments per page
   * @return paginated GraphQL representation of comments
   */
  @QueryMapping
  public PaginatedData<CommentDTO.GraphQL> comments(@Argument Integer page, @Argument Integer size) {
    return CommentDTO.Converter.toGraphQL(this.commentService.get(Pageable.unpaged()));
  }

  /**
   * Fetch a single comment by its ID.
   *
   * @param id comment ID
   * @return GraphQL representation of the comment
   */
  @QueryMapping
  public CommentDTO.GraphQL commentById(@Argument Long id) {
    return CommentDTO.Converter.toGraphQL(this.commentService.get(id));
  }

  /**
   * Create a new comment.
   *
   * @param input input DTO containing comment data
   * @return GraphQL representation of the created comment
   */
  @MutationMapping
  public CommentDTO.GraphQL createComment(@Argument CommentDTO.In input) {
    return CommentDTO.Converter.toGraphQL(this.commentService.create(input));
  }

  /**
   * Update an existing comment.
   *
   * @param id   comment ID
   * @param body new comment body
   * @return GraphQL representation of the updated comment
   */
  @MutationMapping
  public CommentDTO.GraphQL updateComment(@Argument Long id, @Argument String body) {
    return CommentDTO.Converter.toGraphQL(this.commentService.update(id, body));
  }

  /**
   * Delete a comment by its ID.
   *
   * @param id comment ID
   * @return success message
   */
  @MutationMapping
  public String deleteComment(@Argument Long id) {
    this.commentService.delete(id);
    return "Comment Successfully deleted";
  }

  /**
   * Resolves the user of a comment for GraphQL queries.
   *
   * @param graphQL GraphQL comment object
   * @return user who wrote the comment
   */
  @SchemaMapping(typeName = "Comment", field = "user")
  public UserDTO.Out user(CommentDTO.GraphQL graphQL) {
    return this.userService.get(graphQL.getUserId());
  }

  /**
   * Resolves the post of a comment for GraphQL queries.
   *
   * @param graphQL GraphQL comment object
   * @return post associated with the comment
   */
  @SchemaMapping(typeName = "Comment", field = "post")
  public PostDTO.GraphQL post(CommentDTO.GraphQL graphQL) {
    return PostDTO.Converter.toGraphQL(this.postService.get(graphQL.getPostId()));
  }


  /**
   * Resolves the parent comment of a comment, if it exists.
   *
   * @param graphQL GraphQL comment object
   * @return parent comment, or null if none exists
   */
  @SchemaMapping(typeName = "Comment", field = "parent")
  public CommentDTO.GraphQL comment(CommentDTO.GraphQL graphQL) {
    try{
      return CommentDTO.Converter.toGraphQL(this.commentService.get(graphQL.getParentCommentId()));
    }catch(Exception ignored){ // if no parent, just return null (It is possible for a comment to not have a parent)
      return null;
    }
  }

}
