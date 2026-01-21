package com.amalitech.blogging_platform.graphql;

import com.amalitech.blogging_platform.dto.PageRequest;
import com.amalitech.blogging_platform.dto.PaginatedData;
import com.amalitech.blogging_platform.dto.PostDTO;
import com.amalitech.blogging_platform.dto.UserDTO;
import com.amalitech.blogging_platform.service.PostService;
import com.amalitech.blogging_platform.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
public class PostGController {

  private final PostService postService;
  private final UserService userService;

  @Autowired
  public PostGController(PostService postService, UserService userService) {
    this.postService = postService;
    this.userService = userService;
  }

  @QueryMapping
  public PaginatedData<PostDTO.GraphQL> posts(@Argument Integer page, @Argument Integer size) {
    log.debug("Getting paginated data");
    return PostDTO.Converter.toGraphQL(this.postService.get(new PageRequest(page, size)));
  }
  @QueryMapping
  public PostDTO.GraphQL postById(@Argument Long id) {
    return PostDTO.Converter.toGraphQL(this.postService.get(id));
  }

  @QueryMapping
  public PaginatedData<PostDTO.GraphQL> postByAuthorId(@Argument Integer page, @Argument Integer size, @Argument Long id) {
    return PostDTO.Converter.toGraphQL(this.postService.getByAuthorId(id, new PageRequest(page, size)));
  }

  @QueryMapping
  public PaginatedData<PostDTO.GraphQL> postSearch(@Argument Integer page, @Argument Integer size, @Argument String keyword, @Argument Long tagId) {
    return PostDTO.Converter.fromDetaildtoGraphQL(this.postService.search(new PageRequest(page, size), keyword, tagId, null));
  }


  @SchemaMapping(typeName = "Post", field = "author")
  public UserDTO.Out author(PostDTO.GraphQL graphQL) {
    if (graphQL == null)
      return null;

    log.debug("Gettting author for id {}", graphQL.getAuthorId());
    return this.userService.get(graphQL.getAuthorId());
  }

}
