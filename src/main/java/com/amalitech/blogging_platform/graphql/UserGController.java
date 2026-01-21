package com.amalitech.blogging_platform.graphql;

import com.amalitech.blogging_platform.dto.PageRequest;
import com.amalitech.blogging_platform.dto.PaginatedData;
import com.amalitech.blogging_platform.dto.UserDTO;
import com.amalitech.blogging_platform.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
public class UserGController {

  private final UserService userService;

  @Autowired
  public UserGController(UserService userService) {
    this.userService = userService;
  }
  @QueryMapping
  public PaginatedData<UserDTO.Out> users(@Argument Integer page, @Argument Integer size) {
    return this.userService.get(new PageRequest(page, size));
  }
  @QueryMapping
  public UserDTO.Out userById(@Argument Long id){
    return this.userService.get(id);
  }

  @QueryMapping
  public UserDTO.Out userByUsername(@Argument String username){
    return this.userService.getByUsername(username);
  }

}
