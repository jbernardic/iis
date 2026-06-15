package com.example.iis.dto.auth;

/**
 * JWT pair returned by login / refresh (Part 5).
 */
public record TokenResponse(String accessToken, String refreshToken, String tokenType,
                            String role, long expiresInSeconds) {
}
