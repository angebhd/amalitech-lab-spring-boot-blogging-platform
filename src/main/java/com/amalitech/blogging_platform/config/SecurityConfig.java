package com.amalitech.blogging_platform.config;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Security-related bean configuration.
 */
@Configuration
public class SecurityConfig {

  /**
   * Provides an {@link Argon2} instance for password hashing.
   *
   * @return configured Argon2 instance
   */
  @Bean
  public Argon2 argon2() {
    return Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id);
  }
}
