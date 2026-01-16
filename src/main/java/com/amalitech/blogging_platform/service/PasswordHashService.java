package com.amalitech.blogging_platform.service;

import de.mkammerer.argon2.Argon2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

  public String hash(char[] password) {
    try {
      return argon2.hash(
              TIME_COST,
              MEMORY_COST,
              PARALLELISM,
              password
      );
    } finally {
      argon2.wipeArray(password);
    }
  }

  public boolean verify(char[] password, String hash) {
    try {
      return argon2.verify(hash, password);
    } finally {
      argon2.wipeArray(password);
    }
  }
}
