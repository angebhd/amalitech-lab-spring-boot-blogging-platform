package com.amalitech.blogging_platform.service;

import com.amalitech.blogging_platform.model.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;

@Service
public class JWTService {

  @Value("${jwt.secret}")
  private String secret;

  private SecretKey generateSigningKey() {
    return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
  }

  public String generateToken(String username, List<UserRole> userRoles){
    List<String> roles = userRoles.stream().map(r -> "ROLE_"+r.name()).toList();
    return Jwts.builder()
            .subject(username)
            .claim("roles", roles)
            .issuedAt(new Date(System.currentTimeMillis()))
            .expiration(new Date(System.currentTimeMillis() + (1000 * 60 * 60)))
            .signWith(generateSigningKey())
            .compact();
  }

  public Claims extractClaims(String token) {
    return Jwts.parser()
            .verifyWith(generateSigningKey())
            .build()
            .parseSignedClaims(token)
            .getPayload();
  }

  public String extractUsername(String token) {
    return extractClaims(token).getSubject();
  }

  public List<String> extractRoles(String token) {
    return extractClaims(token).get("roles", List.class);
  }

  public Date extractExpiration(String token) {
    return extractClaims(token).getExpiration();
  }

  public boolean isTokenExpired(String token){
    return new Date().before(extractExpiration(token));
  }
}
