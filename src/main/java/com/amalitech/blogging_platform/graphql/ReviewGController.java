package com.amalitech.blogging_platform.graphql;

import com.amalitech.blogging_platform.dto.*;
import com.amalitech.blogging_platform.model.EReview;
import com.amalitech.blogging_platform.service.PostService;
import com.amalitech.blogging_platform.service.ReviewService;
import com.amalitech.blogging_platform.service.UserService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

@Controller
public class ReviewGController {
  private final ReviewService reviewService;
  private final PostService postService;
  private final UserService userService;
  public ReviewGController(ReviewService reviewService, PostService postService, UserService userService) {
    this.reviewService = reviewService;
    this.postService = postService;
    this.userService = userService;
  }
  @QueryMapping
  public PaginatedData<ReviewDTO.GraphQL> reviews(@Argument Integer page, @Argument Integer size) {
    return ReviewDTO.Converter.toGraphQL(this.reviewService.get(new PageRequest(page, size)));
  }

  @QueryMapping
  public ReviewDTO.GraphQL reviewById(@Argument Long id) {
    return ReviewDTO.Converter.toGraphQL(this.reviewService.get(id));
  }

  @MutationMapping
  public ReviewDTO.GraphQL createReview(@Argument ReviewDTO.In input) {
    return ReviewDTO.Converter.toGraphQL(this.reviewService.create(input));
  }

  @MutationMapping
  public ReviewDTO.GraphQL updateReview(@Argument Long id, @Argument String rate) {
    return ReviewDTO.Converter.toGraphQL(this.reviewService.update(id, EReview.valueOf(rate)));
  }

  @MutationMapping
  public String deleteReview(@Argument Long id) {
    this.reviewService.delete(id);
    return "Review successfully deleted";
  }

  @SchemaMapping(typeName = "Review", field = "user")
  public UserDTO.Out user(ReviewDTO.GraphQL graphQL) {
      return this.userService.get(graphQL.getUserId());
  }
  @SchemaMapping(typeName = "Review", field = "post")
  public PostDTO.GraphQL post(ReviewDTO.GraphQL graphQL) {
    return PostDTO.Converter.toGraphQL(this.postService.get(graphQL.getPostId()));
  }
}
