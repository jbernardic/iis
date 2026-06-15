package com.example.client.config;

import com.example.client.security.BackendAuthenticationProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

import java.util.List;

/**
 * Client security. A form login authenticates against the backend; the two
 * roles map to URL access:
 * <ul>
 *   <li>read-only ({@code ROLE_READ}) may view pages and call GET-style
 *       operations (orders list, SOAP search, XML validate, weather, GraphQL
 *       query);</li>
 *   <li>full access ({@code ROLE_FULL}) may additionally perform writes
 *       (create/update/delete orders, import, GraphQL mutations).</li>
 * </ul>
 * CSRF is disabled to keep the multipart upload demo simple.
 */
@Configuration
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/css/**", "/js/**", "/error").permitAll()
                        // write operations require full access
                        .requestMatchers(HttpMethod.POST, "/orders/**").hasRole("FULL")
                        .requestMatchers("/import/**").hasRole("FULL")
                        .requestMatchers("/graphql/mutate").hasRole("FULL")
                        // everything else just needs a logged-in user
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/", true)
                        .permitAll())
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout"));
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(BackendAuthenticationProvider provider) {
        return new ProviderManager(List.of(provider));
    }
}
