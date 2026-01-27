package com.amalitech.blogging_platform.controller.graphql;

import com.amalitech.blogging_platform.dto.*;
import com.amalitech.blogging_platform.model.EReview;
import com.amalitech.blogging_platform.service.ReviewService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class ReviewGController {
  private final ReviewService reviewService;

  public ReviewGController(ReviewService reviewService) {
    this.reviewService = reviewService;

  }
  @QueryMapping
  public PaginatedData<ReviewDTO.Out> reviews(@Argument int page, @Argument int size, @Argument List<GraphQLPageableBuilder.SortInput> sortBy) {
    return this.reviewService.get(GraphQLPageableBuilder.get(page, size, sortBy));
  }

  @QueryMapping
  public ReviewDTO.Out reviewById(@Argument Long id) {
    return this.reviewService.get(id);
  }

  @MutationMapping
  public ReviewDTO.Out createReview(@Argument ReviewDTO.In input) {
    return this.reviewService.create(input);
  }

  @MutationMapping
  public ReviewDTO.Out updateReview(@Argument Long id, @Argument String rate) {
    return this.reviewService.update(id, EReview.valueOf(rate));
  }

  @MutationMapping
  public String deleteReview(@Argument Long id) {
    this.reviewService.delete(id);
    return "Review successfully deleted";
  }

  @SchemaMapping(typeName = "Review", field = "user")
  public UserDTO.Out user(ReviewDTO.Out review) {
      return review.getUser();
  }
  @SchemaMapping(typeName = "Review", field = "post")
  public PostDTO.Out post(ReviewDTO.Out review) {
    return review.getPost();
  }
}
