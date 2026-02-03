package com.amalitech.blogging_platform.config;

import com.amalitech.blogging_platform.exceptions.UnauthorizedException;
import com.amalitech.blogging_platform.model.UserDetail;
import com.amalitech.blogging_platform.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security-related bean configuration.
 */
@Configuration
@AllArgsConstructor
public class SecurityConfig {

  private final UserRepository userRepository;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) {
    return http
            .csrf(Customizer.withDefaults())
            .authorizeHttpRequests(
                    httpRequest -> httpRequest
                            .requestMatchers("/api/v1/auth/**").permitAll()
                            .requestMatchers("/swagger-ui/**").permitAll()
                            .requestMatchers("/v3/api-docs/**").permitAll()
                            .anyRequest().authenticated()
            )
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .build();

  }

  @Bean
  public UserDetailsService userDetailsService() {
    return username -> new UserDetail(userRepository.findByUsernameIgnoreCase(username)
            .orElseThrow( () -> new UnauthorizedException("invalid token, user not found")));
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8();
  }

  @Bean
  public AuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider(userDetailsService());
    daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
    return  daoAuthenticationProvider;
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) {
    return configuration.getAuthenticationManager();
  }

}
