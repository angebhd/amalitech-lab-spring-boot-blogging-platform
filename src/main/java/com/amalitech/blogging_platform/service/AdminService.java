package com.amalitech.blogging_platform.service;

import com.amalitech.blogging_platform.dto.AuthDTO;
import com.amalitech.blogging_platform.dto.UserDTO;
import com.amalitech.blogging_platform.exceptions.BadRequestException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AdminService {
  private final JWTService jwtService;
  private final UserService userService;

  public AdminService(JWTService jwtService, UserService userService) {
    this.jwtService = jwtService;
    this.userService = userService;
  }

  public AuthDTO.TokenPayload getTokenPayload(String jwt) {
    try{
      return new AuthDTO.TokenPayload(
              jwtService.extractUsername(jwt),
              jwtService.extractRoles(jwt),
              jwtService.extractExpiration(jwt),
              UUID.randomUUID()
      );
    }catch(Exception e){
      throw new BadRequestException(e.getMessage());
    }
  }

  public UserDTO.Out makeAdmin(long id){
    return this.userService.makeAdmin(id);
  }

  public UserDTO.Out removeAdmin(long id){
    return this.userService.removeAdmin(id);
  }
}
