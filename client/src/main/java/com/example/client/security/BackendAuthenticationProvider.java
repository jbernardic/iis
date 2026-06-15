package com.example.client.security;

import com.example.client.model.TokenResponse;
import com.example.client.service.BackendClient;
import com.example.client.session.UserSession;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Authenticates the web client's users against the backend's JWT login. On
 * success it stores the token pair in the {@link UserSession} and grants the
 * matching role authority ({@code ROLE_FULL} / {@code ROLE_READ}).
 */
@Component
public class BackendAuthenticationProvider implements AuthenticationProvider {

    private final BackendClient backendClient;
    private final UserSession userSession;

    public BackendAuthenticationProvider(BackendClient backendClient, UserSession userSession) {
        this.backendClient = backendClient;
        this.userSession = userSession;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = String.valueOf(authentication.getCredentials());

        TokenResponse token;
        try {
            token = backendClient.login(username, password);
        } catch (Exception e) {
            throw new BadCredentialsException("Backend rejected the credentials");
        }
        if (token == null || token.accessToken() == null) {
            throw new BadCredentialsException("No token returned by backend");
        }

        userSession.set(username, token.role(), token.accessToken(), token.refreshToken());
        var authority = new SimpleGrantedAuthority("ROLE_" + token.role());
        return new UsernamePasswordAuthenticationToken(username, null, List.of(authority));
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
