package com.amalitech.blogging_platform.service;

import com.amalitech.blogging_platform.dto.AuthDTO;
import com.amalitech.blogging_platform.dto.UserDTO;
import com.amalitech.blogging_platform.exceptions.UnauthorizedException;
import com.amalitech.blogging_platform.model.User;
import com.amalitech.blogging_platform.model.UserRole;
import com.amalitech.blogging_platform.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class AuthService {

  private final UserRepository userRepository;
  private final UserService userService;
  private final PasswordEncoder passwordEncoder;
  private final JWTService jwtService;
  private final Logger logger = LoggerFactory.getLogger(AuthService.class);


  @Autowired
  public AuthService(UserRepository userRepository, UserService userService, PasswordEncoder passwordEncoder, JWTService jwtService) {
    this.userRepository = userRepository;
    this.userService = userService;
    this.passwordEncoder = passwordEncoder;
    this.jwtService = jwtService;
  }

  public UserDTO.Out signup(UserDTO.In in) {
    return this.userService.create(in);
  }

  public AuthDTO.LoginResponse login(AuthDTO.LoginDTO loginDTO) {
    User user = userRepository.findByUsernameIgnoreCase(loginDTO.username()).orElseThrow(() -> new UnauthorizedException("user not found"));
    boolean passwordMatch = passwordEncoder.matches(loginDTO.password(), user.getPassword());
    if(passwordMatch) {
      this.logger.info("{} logged in successfully", user.getUsername());
      String accessToken = jwtService.generateToken(user.getUsername(), List.of( user.getRole() ));
      return new AuthDTO.LoginResponse(accessToken, this.mapToUserDTO(user));
    }
    this.logger.warn("{} login failed", user.getUsername());
    throw new UnauthorizedException("username or password incorrect");
  }

  public AuthDTO.LoginResponse processOAuthPostLogin(OAuth2User oAuth2User) {

    String email = oAuth2User.getAttribute("email");
    String name = oAuth2User.getAttribute("name");
    String username = this.generateUsername(name);
    String password = UUID.randomUUID().toString();

    User user = userRepository.findByEmailIgnoreCase(email)
            .orElseGet(() -> {
              User u = new User();
              u.setEmail(email);
              u.setFirstName(name);
              u.setUsername(username);
              u.setRole(UserRole.USER);
              u.setPassword("Google_AUTH"+passwordEncoder.encode(password));
              return userRepository.save(u);
            });

    String accessToken = jwtService.generateToken(user.getEmail(), List.of(user.getRole()));

    return new AuthDTO.LoginResponse(accessToken, this.mapToUserDTO(user));
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

  private String generateUsername(String name) {
    if (name == null || name.isEmpty()) {
      throw new IllegalArgumentException("Name cannot be empty");
    }

    final String CHAR_POOL = "abcdefghijklmnopqrstuvwxyz_0123456789";
    final int MAX_LENGTH = 12;
    java.security.SecureRandom random = new java.security.SecureRandom();
    String prefix = name.length() >= 4 ? name.substring(0, 4) + "_" : name + "_";

    String username;
    do {
      int extraLength = random.nextInt(MAX_LENGTH - prefix.length() + 1);

      StringBuilder sb = new StringBuilder(prefix);
      for (int i = 0; i < extraLength; i++) {
        sb.append(CHAR_POOL.charAt(random.nextInt(CHAR_POOL.length())));
      }

      username = sb.toString();

    } while (userRepository.findByUsernameIgnoreCase(username).isPresent());

    return username;
  }

}
