package com.example.client.model;

public record TokenResponse(String accessToken, String refreshToken, String tokenType,
                            String role, long expiresInSeconds) {
}
