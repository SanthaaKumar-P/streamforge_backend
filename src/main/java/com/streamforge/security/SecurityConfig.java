package com.streamforge.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;

import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomUserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http

                .csrf(csrf -> csrf.disable())

                .cors(cors ->
                        cors.configurationSource(
                                corsConfigurationSource()
                        )
                )

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                .authorizeHttpRequests(auth -> auth

                        // ==============================
                        // PUBLIC
                        // ==============================

                        .requestMatchers(
                                "/api/auth/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**"
                        )
                        .permitAll()

                        // ==============================
                        // ADMIN
                        // ==============================

                        .requestMatchers("/api/admin/**")
                        .hasRole("ADMIN")

                        // ==============================
                        // CREATOR
                        // ==============================

                        .requestMatchers("/api/creator/**")
                        .hasRole("CREATOR")

                        // ==============================
                        // PRODUCER
                        // ==============================

                        .requestMatchers("/api/producer/**")
                        .hasRole("PRODUCER")

                        // ==============================
                        // CONTENT MANAGER
                        // ==============================

                        .requestMatchers("/api/content/**")
                        .hasRole("CONTENT_MANAGER")

                        // ==============================
                        // PRODUCTION
                        // ==============================

                        .requestMatchers(
                                org.springframework.http.HttpMethod.POST,
                                "/api/productions/**"
                        )
                        .hasAnyRole("ADMIN", "PRODUCER")

                        .requestMatchers(
                                org.springframework.http.HttpMethod.PUT,
                                "/api/productions/**"
                        )
                        .hasAnyRole("ADMIN", "PRODUCER")

                        .requestMatchers(
                                org.springframework.http.HttpMethod.DELETE,
                                "/api/productions/**"
                        )
                        .hasRole("ADMIN")

                        .requestMatchers(
                                org.springframework.http.HttpMethod.GET,
                                "/api/productions/**"
                        )
                        .authenticated()

                        // ==============================
                        // PRODUCTION TEAM
                        // ==============================

                        .requestMatchers(
                                org.springframework.http.HttpMethod.POST,
                                "/api/production-team/**"
                        )
                        .hasAnyRole("ADMIN", "PRODUCER")

                        .requestMatchers(
                                org.springframework.http.HttpMethod.DELETE,
                                "/api/production-team/**"
                        )
                        .hasAnyRole("ADMIN", "PRODUCER")

                        .requestMatchers(
                                org.springframework.http.HttpMethod.GET,
                                "/api/production-team/**"
                        )
                        .authenticated()

                        // ==============================
                        // EVERYTHING ELSE
                        // ==============================

                        .anyRequest()
                        .authenticated()
                )

                .authenticationProvider(
                        authenticationProvider()
                )

                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration =
                new CorsConfiguration();

        configuration.setAllowedOrigins(
                List.of(
                        "http://localhost:5173",
                        "http://localhost:8081"
                )
        );

        configuration.setAllowedMethods(
                List.of(
                        "GET",
                        "POST",
                        "PUT",
                        "DELETE",
                        "PATCH",
                        "OPTIONS"
                )
        );

        configuration.setAllowedHeaders(
                List.of("*")
        );

        configuration.setExposedHeaders(
                List.of("Authorization")
        );

        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration(
                "/**",
                configuration
        );

        return source;
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {

        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider();

        provider.setUserDetailsService(
                userDetailsService
        );

        provider.setPasswordEncoder(
                passwordEncoder()
        );

        return provider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration
    ) throws Exception {

        return configuration.getAuthenticationManager();
    }
}