package com.amalitech.blogging_platform.service;

import com.amalitech.blogging_platform.dto.AuthDTO;
import com.amalitech.blogging_platform.dto.BlacklistedTokenInfo;
import com.amalitech.blogging_platform.dto.UserDTO;
import com.amalitech.blogging_platform.exceptions.BadRequestException;
import com.amalitech.blogging_platform.security.TokenBlacklist;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class AdminService {
  private final JWTService jwtService;
  private final UserService userService;
  private final TokenBlacklist tokenBlacklist;

  public AdminService(JWTService jwtService, UserService userService, TokenBlacklist tokenBlacklist) {
    this.jwtService = jwtService;
    this.userService = userService;
    this.tokenBlacklist = tokenBlacklist;
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

  public void blacklistToken(String token){
    try{
      var expiryDate = jwtService.extractExpiration(token);
      if (expiryDate.toInstant().isBefore(Instant.now())) {
        throw new BadRequestException(" Token has already expired");
      }
      Instant expiry = Instant.from(expiryDate.toInstant());
      this.tokenBlacklist.add(token, expiry);
    }catch(BadRequestException e){
      throw e;
    }catch (Exception e){
      throw new BadRequestException(e.getMessage());
    }
  }

  public List<BlacklistedTokenInfo> getBlacklistedTokens() {
    return this.tokenBlacklist.getBlacklistedTokens();
  }
}
