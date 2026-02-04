package com.amalitech.blogging_platform.model;

import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class UserDetail implements UserDetails {

  private final String username;
  private final UserRole role;

  public  UserDetail(User user) {
    this.username = user.getUsername();
    this.role = user.getRole();
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of(new SimpleGrantedAuthority("ROLE_"+role.name()));
  }

  @Override
  public @Nullable String getPassword() {
    return null;
  }

  @Override
  public String getUsername() {
    return this.username;
  }
}
