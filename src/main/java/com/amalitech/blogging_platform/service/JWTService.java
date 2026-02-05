package com.amalitech.blogging_platform.service;

import com.amalitech.blogging_platform.model.UserRole;
import com.amalitech.blogging_platform.security.JWTProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;

@Service
public class JWTService {

  private final JWTProperties jwtProperties;
  private final SecretKey signingKey;

  public JWTService(JWTProperties jwtProperties) {
    this.jwtProperties = jwtProperties;
    this.signingKey = Keys.hmacShaKeyFor(
            Decoders.BASE64.decode(jwtProperties.getSecretKey())
    );
  }

  public String generateToken(String username, List<UserRole> userRoles) {
    List<String> roles = userRoles.stream()
            .map(r -> "ROLE_" + r.name())
            .toList();

    long now = System.currentTimeMillis();

    return Jwts.builder()
            .subject(username)
            .claim("roles", roles)
            .issuedAt(new Date(now))
            .expiration(new Date(now + jwtProperties.getExpireAfter()))
            .signWith(signingKey)
            .compact();
  }

  public Claims extractClaims(String token) {
    return Jwts.parser()
            .verifyWith(signingKey)
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

  public boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }
}

