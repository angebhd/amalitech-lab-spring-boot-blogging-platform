package com.amalitech.blogging_platform.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User extends BaseEntity {
  private Long id;
  private String firstName;
  private String lastName;
  private String username;
  private String email;
  private String password;


}
