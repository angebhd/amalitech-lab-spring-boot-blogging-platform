package com.amalitech.blogging_platform.config;

import com.amalitech.blogging_platform.model.UserDetail;
import com.amalitech.blogging_platform.service.JWTService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

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
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    SecurityContext context = SecurityContextHolder.getContext();
    String token = request.getHeader("Authorization");
    if(token == null || context.getAuthentication() == null) {
      filterChain.doFilter(request, response);
      return;
    }
    if (token.startsWith("Bearer ")) {
      String jwtToken = token.substring(7);
      String username = jwtService.extractUsername(jwtToken);
      try{
        UserDetail user = (UserDetail) userDetailsService.loadUserByUsername(username);
        var authToken = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        authToken.setDetails(new WebAuthenticationDetails(request));
        context.setAuthentication(authToken);
      }catch(Exception ignored){}
    }
    filterChain.doFilter(request, response);

  }
}
