package com.example.client.model;

/** Mirrors the backend's JWT login/refresh response. */
public record TokenResponse(String accessToken, String refreshToken, String tokenType,
                            String role, long expiresInSeconds) {
}
