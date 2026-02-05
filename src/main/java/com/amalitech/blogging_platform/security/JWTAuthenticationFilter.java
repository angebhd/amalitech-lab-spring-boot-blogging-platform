package com.amalitech.blogging_platform.security;

import com.amalitech.blogging_platform.exceptions.UnauthorizedException;
import com.amalitech.blogging_platform.model.UserDetail;
import com.amalitech.blogging_platform.service.JWTService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
public class JWTAuthenticationFilter extends OncePerRequestFilter {

  private final JWTService jwtService;
  private final UserDetailsService userDetailsService;

  @Autowired
  public JWTAuthenticationFilter(JWTService jwtService, UserDetailsService userDetailsService) {
    this.jwtService = jwtService;
    this.userDetailsService = userDetailsService;
  }


  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
          throws ServletException, IOException {

    String authHeader = request.getHeader("Authorization");
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      filterChain.doFilter(request, response);
      return;
    }

    String jwtToken = authHeader.substring(7);

    try {
      String username = jwtService.extractUsername(jwtToken);

      if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

        UserDetail user = (UserDetail) userDetailsService.loadUserByUsername(username);
        if (jwtService.isTokenExpired(jwtToken) ) {
          throw new UnauthorizedException("Token has expired");
        } else {
          var authToken = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
          authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
          SecurityContextHolder.getContext().setAuthentication(authToken);
        }
      }
    } catch (UnauthorizedException e) {
      SecurityContextHolder.clearContext();
      throw e;
    } catch (Exception e) {
      SecurityContextHolder.clearContext();
      throw new UnauthorizedException("Invalid authentication token");
    }

    filterChain.doFilter(request, response);
  }
}
