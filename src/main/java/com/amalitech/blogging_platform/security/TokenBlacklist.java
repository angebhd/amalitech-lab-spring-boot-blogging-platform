package com.amalitech.blogging_platform.security;

import com.amalitech.blogging_platform.dto.BlacklistedTokenInfo;
import com.amalitech.blogging_platform.exceptions.RessourceNotFoundException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class TokenBlacklist {

  private final ConcurrentMap<String, Instant> blacklist = new ConcurrentHashMap<>();

  public void add(String token, Instant expiry) {
    blacklist.put(token, expiry);
  }

  public List<BlacklistedTokenInfo> getBlacklistedTokens() {
    Instant now = Instant.now();
    if (blacklist.isEmpty()) {
      throw new RessourceNotFoundException("There's not blacklisted tokens yet");
    }
    return blacklist.entrySet().stream()
            .filter(e -> !now.isAfter(e.getValue()))     // only non-expired
            .map(e -> new BlacklistedTokenInfo(
                    e.getKey(),
                    e.getValue(),
                    Duration.between(now, e.getValue()).toMinutes() + " min left"))
            .toList();
  }

  public boolean isBlacklisted(String token) {
    Instant expiry = blacklist.get(token);
    if (expiry == null) {
      return false;
    }
    if (Instant.now().isAfter(expiry)) {
      blacklist.remove(token);
      return false;
    }
    return true;
  }

  @Scheduled(fixedRate = 600_000)
  public void cleanup() {
    blacklist.entrySet().removeIf(e -> Instant.now().isAfter(e.getValue()));
  }

}
