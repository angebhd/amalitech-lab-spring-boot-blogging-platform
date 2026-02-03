package com.amalitech.blogging_platform.service;

import com.amalitech.blogging_platform.dto.UserDTO;
import com.amalitech.blogging_platform.exceptions.UnauthorizedException;
import com.amalitech.blogging_platform.model.User;
import com.amalitech.blogging_platform.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JWTService jwtService;


  @Autowired
  public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JWTService jwtService) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtService = jwtService;
  }

  public AuthDTO.LoginResponse login(AuthDTO.LoginDTO loginDTO) {
    User user = userRepository.findByUsernameIgnoreCase(loginDTO.getUsername()).orElseThrow(() -> new UnauthorizedException("user not found"));
    boolean passwordMatch = passwordEncoder.matches(loginDTO.getPassword(), user.getPassword());
    if(passwordMatch) {
      String accessToken = jwtService.generateToken(user.getUsername(), List.of( user.getRole() ));
      return new AuthDTO.LoginResponse(accessToken, this.mapToUserDTO(user));
    }

    throw new UnauthorizedException("username or password incorrect");
  }

  private UserDTO.Out mapToUserDTO(User user){
    UserDTO.Out out = new UserDTO.Out();
    out.setId(user.getId());
    out.setFirstName(user.getFirstName());
    out.setLastName(user.getLastName());
    out.setUsername(user.getUsername());
    out.setEmail(user.getEmail());
    out.setCreatedAt(user.getCreatedAt());
    out.setUpdatedAt(user.getUpdatedAt());
    out.setDeletedAt(user.getDeletedAt());
    out.setDeleted(user.isDeleted());
    return out;
  }
}
