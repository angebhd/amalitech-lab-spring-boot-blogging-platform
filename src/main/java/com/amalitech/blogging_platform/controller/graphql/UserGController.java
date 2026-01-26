package com.amalitech.blogging_platform.controller.graphql;

import com.amalitech.blogging_platform.dto.PaginatedData;
import com.amalitech.blogging_platform.dto.UserDTO;
import com.amalitech.blogging_platform.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
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
  public PaginatedData<UserDTO.Out> users(@Argument Pageable page) {
//    return this.userService.get(page);
    return null;
  }
  @QueryMapping
  public UserDTO.Out userById(@Argument Long id){
    return this.userService.get(id);
  }

  @QueryMapping
  public UserDTO.Out userByUsername(@Argument String username){
    return this.userService.getByUsername(username);
  }

  @MutationMapping
  public UserDTO.Out createUser(@Argument UserDTO.In input) {
    return this.userService.create(input);
  }

  @MutationMapping
  public UserDTO.Out updateUser(@Argument Long id, @Argument UserDTO.In input) {
    return this.userService.update(id, input);
  }

  @MutationMapping
  public UserDTO.Out deleteUser(@Argument Long id) {
    this.userService.delete(id);
    return this.userService.get(id);
  }

}
