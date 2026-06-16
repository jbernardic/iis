package com.example.iis.web;

import com.example.iis.domain.UserAccount;
import com.example.iis.dto.auth.LoginRequest;
import com.example.iis.dto.auth.RefreshRequest;
import com.example.iis.dto.auth.TokenResponse;
import com.example.iis.repo.UserRepository;
import com.example.iis.security.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository users;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtService jwtService,
                          UserRepository users) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.users = users;
    }

    @PostMapping("/login")
    public TokenResponse login(@RequestBody LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password()));

        UserAccount user = users.findByUsername(request.username())
                .orElseThrow(() -> new BadCredentialsException("Unknown user"));
        return issueTokens(user.getUsername(), user.getRole());
    }

    @PostMapping("/refresh")
    public TokenResponse refresh(@RequestBody RefreshRequest request) {
        try {
            Jws<Claims> jws = jwtService.parse(request.refreshToken());
            Claims claims = jws.getPayload();
            if (!JwtService.TYPE_REFRESH.equals(jwtService.getType(claims))) {
                throw new BadCredentialsException("Not a refresh token");
            }
            return issueTokens(jwtService.getUsername(claims), jwtService.getRole(claims));
        } catch (BadCredentialsException e) {
            throw e;
        } catch (Exception e) {
            throw new BadCredentialsException("Invalid or expired refresh token");
        }
    }

    private TokenResponse issueTokens(String username, String role) {
        String access = jwtService.generateAccessToken(username, role);
        String refresh = jwtService.generateRefreshToken(username, role);
        return new TokenResponse(access, refresh, "Bearer", role,
                jwtService.getAccessTtlMinutes() * 60L);
    }

    @ExceptionHandler({AuthenticationException.class})
    public ResponseEntity<Map<String, String>> handleAuthFailure(AuthenticationException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Authentication failed", "detail", e.getMessage()));
    }
}
