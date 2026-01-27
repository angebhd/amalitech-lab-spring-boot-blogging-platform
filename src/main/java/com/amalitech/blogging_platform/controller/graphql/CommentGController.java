package com.amalitech.blogging_platform.controller.graphql;

import com.amalitech.blogging_platform.dto.*;
import com.amalitech.blogging_platform.service.CommentService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

/**
 * GraphQL controller (resolver) for managing comments.
 * <p>
 * Provides queries and mutations for retrieving, creating, updating, and deleting comments.
 * Also resolves related fields like user, post, and parent comment.
 */
@Controller
public class CommentGController {
  private final CommentService commentService;
  public CommentGController(CommentService commentService) {
    this.commentService = commentService;

  }


  /**
   * Fetch paginated comments.
   *
   * @param page requested page (0 index)
   * @param size requested elements in the page
   * @param sortBy sorting arguments
   * @return paginated comments
   */
  @QueryMapping
  public PaginatedData<CommentDTO.Out> comments(@Argument int page, @Argument int size, @Argument List<GraphQLPageableBuilder.SortInput> sortBy) {

    return this.commentService.get(GraphQLPageableBuilder.get(page, size, sortBy));
  }

  /**
   * Fetch a single comment by its ID.
   *
   * @param id comment ID
   * @return GraphQL representation of the comment
   */
  @QueryMapping
  public CommentDTO.Out commentById(@Argument Long id) {
    return this.commentService.get(id);
  }

  /**
   * Create a new comment.
   *
   * @param input input DTO containing comment data
   * @return GraphQL representation of the created comment
   */
  @MutationMapping
  public CommentDTO.Out createComment(@Argument CommentDTO.In input) {
    return this.commentService.create(input);
  }

  /**
   * Update an existing comment.
   *
   * @param id   comment ID
   * @param body new comment body
   * @return GraphQL representation of the updated comment
   */
  @MutationMapping
  public CommentDTO.Out updateComment(@Argument Long id, @Argument String body) {
    return this.commentService.update(id, body);
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
   * @param comment Comment object
   * @return user who wrote the comment
   */
  @SchemaMapping(typeName = "Comment", field = "user")
  public UserDTO.Out user(CommentDTO.Out comment) {
    return comment.getUser();
  }


}
