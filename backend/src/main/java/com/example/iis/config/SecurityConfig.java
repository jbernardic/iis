package com.example.iis.config;

import com.example.iis.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Stateless security for the backend.
 * <ul>
 *   <li>{@code /api/auth/**}, SOAP ({@code /ws/**}), the H2 console and the
 *       GraphiQL UI are open.</li>
 *   <li>{@code GET /api/orders/**} and {@code /api/xml/**} need any authenticated
 *       user (read-only or full).</li>
 *   <li>writes ({@code POST/PUT/DELETE}) and {@code /api/import/**} need ROLE_FULL.</li>
 * </ul>
 * GraphQL mutations are additionally guarded with {@code @PreAuthorize} (method
 * security), so the read-only role can query but not mutate.
 */
@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .headers(h -> h.frameOptions(frame -> frame.disable())) // H2 console
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/ws/**").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers("/graphiql/**", "/vendor/**").permitAll()
                        .requestMatchers("/actuator/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/orders/**").hasAnyRole("READ", "FULL")
                        .requestMatchers(HttpMethod.POST, "/api/orders/**").hasRole("FULL")
                        .requestMatchers(HttpMethod.PUT, "/api/orders/**").hasRole("FULL")
                        .requestMatchers(HttpMethod.DELETE, "/api/orders/**").hasRole("FULL")
                        .requestMatchers("/api/import/**").hasRole("FULL")
                        .requestMatchers("/api/xml/**").hasAnyRole("READ", "FULL")
                        .requestMatchers("/api/source").hasAnyRole("READ", "FULL")
                        .requestMatchers("/graphql").authenticated()
                        .anyRequest().authenticated())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
