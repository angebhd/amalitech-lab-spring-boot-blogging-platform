package com.amalitech.blogging_platform.config;

import com.amalitech.blogging_platform.model.User;
import com.amalitech.blogging_platform.model.UserRole;
import com.amalitech.blogging_platform.repository.UserRepository;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class AdminInitializer implements CommandLineRunner {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final AdminProperties adminProperties;
  private final Logger logger = LoggerFactory.getLogger(AdminInitializer.class);

  @Override
  public void run(@Nullable String... args) {
    this.logger.info("Initializing admin | Arguments: {}", (Object) args);

    String adminEmail = adminProperties.getEmail();

    if (adminEmail == null || adminEmail.isBlank()) {
      this.logger.info("Admin email is not provided, and admin won't be won't be created");
      return;
    }

    boolean existsByEmail = userRepository.existsByEmailIgnoreCase(adminEmail);
    if (existsByEmail) {
      this.logger.info("Admin user already exists.");
      return;
    }

    boolean existsByUsername = userRepository.existsByUsernameIgnoreCase(adminEmail);
    if (existsByUsername) {
      this.logger.info("Admin user already exists.");
      return;
    }

    User admin = new User();
    admin.setEmail(adminEmail);
    admin.setUsername(this.adminProperties.getUsername());
    admin.setFirstName(this.adminProperties.getFirstName());
    admin.setLastName(this.adminProperties.getLastName());
    admin.setPassword(passwordEncoder.encode(this.adminProperties.getPassword()));
    admin.setRole(UserRole.ADMIN);

    userRepository.save(admin);

    this.logger.info("Admin user created successfully.");
  }
}