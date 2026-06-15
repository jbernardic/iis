package com.example.client.session;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.io.Serializable;

/**
 * Per-user session state: the JWT pair obtained from the backend at login and
 * the user's role. {@link com.example.client.service.BackendClient} reads the
 * access token from here to authorize its calls.
 */
@Component
@SessionScope
public class UserSession implements Serializable {

    private String username;
    private String role;
    private String accessToken;
    private String refreshToken;

    public void set(String username, String role, String accessToken, String refreshToken) {
        this.username = username;
        this.role = role;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public String getUsername() {
        return username;
    }

    public String getRole() {
        return role;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }
}
