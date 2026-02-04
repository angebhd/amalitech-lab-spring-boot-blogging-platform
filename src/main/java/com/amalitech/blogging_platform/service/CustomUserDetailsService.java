package com.amalitech.blogging_platform.service;

import com.amalitech.blogging_platform.exceptions.UnauthorizedException;
import com.amalitech.blogging_platform.model.UserDetail;
import com.amalitech.blogging_platform.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

  private final UserRepository userRepository;

  @Override
  public UserDetail loadUserByUsername(String username) {
    return new UserDetail(
            userRepository.findByUsernameIgnoreCase(username)
                    .orElseThrow(() ->
                            new UnauthorizedException("invalid token, user not found"))
    );
  }
}