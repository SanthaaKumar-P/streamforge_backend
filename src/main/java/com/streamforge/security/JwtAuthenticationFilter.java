package com.streamforge.security;

import com.streamforge.repository.SessionRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter
        extends OncePerRequestFilter {

    private final JwtService jwtService;

    private final CustomUserDetailsService userDetailsService;

    private final SessionRepository sessionRepository;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String authHeader =
                request.getHeader(
                        "Authorization"
                );

        /*
         * No bearer token.
         *
         * Public endpoints can continue.
         * Spring Security decides whether the endpoint
         * itself requires authentication.
         */
        if (
                authHeader == null ||
                !authHeader.startsWith(
                        "Bearer "
                )
        ) {

            filterChain.doFilter(
                    request,
                    response
            );

            return;
        }

        String token =
                authHeader
                        .substring(7)
                        .trim();

        if (token.isBlank()) {

            sendUnauthorized(
                    response,
                    "Missing bearer token"
            );

            return;
        }

        try {

            /*
             * ------------------------------------------------
             * ACCESS TOKEN CHECK
             * ------------------------------------------------
             */

            if (
                    !jwtService.isAccessToken(
                            token
                    )
            ) {

                sendUnauthorized(
                        response,
                        "Invalid access token"
                );

                return;
            }

            /*
             * ------------------------------------------------
             * JWT VALIDATION
             * ------------------------------------------------
             *
             * Checks signature + expiration.
             */

            if (
                    !jwtService.validateToken(
                            token
                    )
            ) {

                sendUnauthorized(
                        response,
                        "Invalid or expired access token"
                );

                return;
            }

            /*
             * ------------------------------------------------
             * USERNAME
             * ------------------------------------------------
             */

            String username =
                    jwtService.extractUsername(
                            token
                    );

            if (
                    username == null ||
                    username.isBlank()
            ) {

                sendUnauthorized(
                        response,
                        "Invalid access token"
                );

                return;
            }

            /*
             * Don't replace an existing authentication.
             */
            if (
                    SecurityContextHolder
                            .getContext()
                            .getAuthentication()
                            != null
            ) {

                filterChain.doFilter(
                        request,
                        response
                );

                return;
            }

            /*
             * ------------------------------------------------
             * LOAD USER
             * ------------------------------------------------
             */

            UserDetails userDetails =
                    userDetailsService
                            .loadUserByUsername(
                                    username
                            );

            /*
             * ------------------------------------------------
             * SERVER SESSION CHECK
             * ------------------------------------------------
             */

            boolean activeSession =
                    sessionRepository
                            .findByAccessToken(
                                    token
                            )
                            .map(session -> {

                                if (
                                        !Boolean.TRUE.equals(
                                                session.getIsActive()
                                        )
                                ) {

                                    return false;
                                }

                                if (
                                        session.getExpiryTime()
                                                == null
                                ) {

                                    return true;
                                }

                                return session
                                        .getExpiryTime()
                                        .isAfter(
                                                LocalDateTime.now()
                                        );
                            })
                            .orElse(false);

            if (!activeSession) {

                sendUnauthorized(
                        response,
                        "Access session is invalid or expired"
                );

                return;
            }

            /*
             * ------------------------------------------------
             * CREATE SPRING AUTHENTICATION
             * ------------------------------------------------
             */

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );

            SecurityContextHolder
                    .getContext()
                    .setAuthentication(
                            authentication
                    );

            /*
             * Development log.
             */
            System.out.println(
                    "========================================"
            );

            System.out.println(
                    "JWT USERNAME : "
                            + username
            );

            System.out.println(
                    "AUTHORITIES  : "
                            + userDetails
                            .getAuthorities()
            );

            System.out.println(
                    "REQUEST      : "
                            + request.getMethod()
                            + " "
                            + request.getRequestURI()
            );

            System.out.println(
                    "JWT STATUS   : VALID"
            );

            System.out.println(
                    "SESSION      : ACTIVE"
            );

            System.out.println(
                    "========================================"
            );

            filterChain.doFilter(
                    request,
                    response
            );

        } catch (Exception e) {

            SecurityContextHolder
                    .clearContext();

            System.out.println(
                    "JWT ERROR: "
                            + e.getMessage()
            );

            sendUnauthorized(
                    response,
                    "Invalid or expired access token"
            );
        }
    }

    private void sendUnauthorized(
            HttpServletResponse response,
            String message
    ) throws IOException {

        SecurityContextHolder
                .clearContext();

        response.setStatus(
                HttpServletResponse.SC_UNAUTHORIZED
        );

        response.setContentType(
                "application/json"
        );

        response.setCharacterEncoding(
                "UTF-8"
        );

        String safeMessage =
                message
                        .replace(
                                "\\",
                                "\\\\"
                        )
                        .replace(
                                "\"",
                                "\\\""
                        );

        response.getWriter().write(
                "{"
                        + "\"error\":\"Unauthorized\","
                        + "\"message\":\""
                        + safeMessage
                        + "\""
                        + "}"
        );
    }
}