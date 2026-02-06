package com.amalitech.blogging_platform.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.jwt")
@Getter
@Setter
public class JWTProperties {
  private Long expireAfter;
  private String secretKey;
}
