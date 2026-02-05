package com.amalitech.blogging_platform.security;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@EnableConfigurationProperties(CORSProperties.class)
@Configuration
public class CORSConfig {

  @Bean
  public CorsConfigurationSource corsConfigurationSource(CORSProperties props) {
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowedOrigins(props.getAllowedOrigins());
    config.setAllowedMethods(props.getAllowedMethods());
    config.setAllowedHeaders(props.getAllowedHeaders());
    config.setAllowCredentials(props.isAllowCredentials());

    config.setMaxAge(3600L);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);
    return source;
  }
}
