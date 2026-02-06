package com.amalitech.blogging_platform.dto;

import java.time.Instant;

public record BlacklistedTokenInfo(
        String token,
        Instant expiry,
        String timeRemaining) {}
