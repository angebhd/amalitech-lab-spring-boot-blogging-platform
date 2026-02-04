package com.amalitech.blogging_platform.config;

import com.amalitech.blogging_platform.service.CustomUserDetailsService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Security-related bean configuration.
 */
@Configuration
@AllArgsConstructor
public class SecurityConfig {

  private final CustomUserDetailsService customUserDetailsService;
  private final JWTAuthenticationFilter jwtAuthenticationFilter;
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) {
    return http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(
                    httpRequest -> httpRequest
                            .requestMatchers("/api/v1/auth/**").permitAll()
                            .requestMatchers("/swagger-ui/**").permitAll()
                            .requestMatchers("/graphiql").permitAll()
                            .requestMatchers("/v3/api-docs/**").permitAll()
                            .requestMatchers("/api/v1/post/feed").permitAll()
                            .anyRequest().authenticated()
            )
            .oauth2Login(oauth -> oauth
                    .defaultSuccessUrl("/api/v1/auth/login/oauth2/success", true)
                    .authorizationEndpoint( url -> url.baseUri("/api/v1/auth/oauth2"))
            )
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .authenticationProvider(authenticationProvider())
            .build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8();
  }

  @Bean
  public AuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider(customUserDetailsService);
    daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
    return  daoAuthenticationProvider;
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) {
    return configuration.getAuthenticationManager();
  }


}
