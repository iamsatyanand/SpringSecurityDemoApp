package com.satyanand.springsecuritydemoapp.auth.security;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ForgotPasswordRateLimiter {

    private final Map<String, Integer> attempts = new ConcurrentHashMap<>();
    private final Map<String, LocalDateTime> timestamps = new ConcurrentHashMap<>();

    private static final int MAX_ATTEMPTS = 5;
    private static final int WINDOW_MINUTES = 10;

    public void validate(String email) {

        LocalDateTime now = LocalDateTime.now();

        timestamps.putIfAbsent(email, now);
        attempts.putIfAbsent(email, 0);

        if (timestamps.get(email).plusMinutes(WINDOW_MINUTES).isBefore(now)) {
            attempts.put(email, 0);
            timestamps.put(email, now);
        }

        int count = attempts.get(email);

        if (count >= MAX_ATTEMPTS) {
            throw new RuntimeException("Too many reset attempts");
        }

        attempts.put(email, count + 1);
    }
}
