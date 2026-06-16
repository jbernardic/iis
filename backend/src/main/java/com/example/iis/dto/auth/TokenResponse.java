package com.example.iis.dto.auth;

public record TokenResponse(String accessToken, String refreshToken, String tokenType,
                            String role, long expiresInSeconds) {
}
