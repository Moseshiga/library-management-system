package com.moseshiga.librarymanagement.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // For a stateless REST API, CSRF is safely disabled because we don't use browser cookies
            .csrf(AbstractHttpConfigurer::disable)
            // Prevent Spring from creating HTTP sessions
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // Define access rules
            .authorizeHttpRequests(auth -> auth
                    // 1. Public endpoints (Anyone can view the catalog and search)
                    .requestMatchers(HttpMethod.GET, "/api/books/**").permitAll()

                    // 2. Secured Actuator endpoints (Tied to requirements)
                    .requestMatchers("/actuator/health", "/actuator/info").permitAll() // Open basic health
                    .requestMatchers("/actuator/**").hasRole("ADMIN") // Metrics, Logs, etc., are strictly for Admins

                    // 3. Admin strictly endpoints (Library staff only)
                    .requestMatchers("/api/readers/**", "/api/loans/**", "/api/statistics").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.POST, "/api/books/**").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.PUT, "/api/books/**").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/api/books/**").hasRole("ADMIN")

                    // 4. Everything else requires authentication
                    .anyRequest().authenticated()
            )
            // Enable Basic Authentication for API clients like Postman
            .httpBasic(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails admin = User.builder()
                .username("admin")
                .password(passwordEncoder().encode("admin123"))
                .roles("ADMIN")
                .build();

        UserDetails user = User.builder()
                .username("user")
                .password(passwordEncoder().encode("user123"))
                .roles("USER")
                .build();

        return new InMemoryUserDetailsManager(admin, user);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
