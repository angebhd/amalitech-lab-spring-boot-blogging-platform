package com.amalitech.blogging_platform.service;

import de.mkammerer.argon2.Argon2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service for hashing and verifying passwords using the Argon2id algorithm.
 * <p>
 * This service uses secure hashing parameters (time cost, memory cost, parallelism)
 * and ensures that plaintext passwords are wiped from memory after use.
 */
@Service
class PasswordHashService {
  private static final int TIME_COST = 3;
  private static final int MEMORY_COST = 131072; // 128 MB (in KB)
  private static final int PARALLELISM = 2;

  private final Argon2 argon2;

  @Autowired
  public PasswordHashService( Argon2 argon2) {
    this.argon2 = argon2;
  }

  /**
   * Hashes a plaintext password using Argon2id.
   * <p>
   * After hashing, the original password array is wiped from memory for security.
   *
   * @param password plaintext password as a char array
   * @return the hashed password string
   */
  public String hash(char[] password) {
    try {
      return argon2.hash(TIME_COST, MEMORY_COST, PARALLELISM, password);
    } finally {
      argon2.wipeArray(password);
    }
  }

  /**
   * Verifies a plaintext password against a stored Argon2 hash.
   * <p>
   * After verification, the original password array is wiped from memory for security.
   *
   * @param password plaintext password as a char array
   * @param hash     previously stored Argon2 hash
   * @return true if the password matches the hash, false otherwise
   */
  public boolean verify(char[] password, String hash) {
    try {
      return argon2.verify(hash, password);
    } finally {
      argon2.wipeArray(password);
    }
  }
}
