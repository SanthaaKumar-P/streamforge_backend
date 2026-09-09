package com.streamforge.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    /*
     * Keep this unchanged for the current project.
     */
    private static final String SECRET_KEY =
            "streamforge-secret-key-streamforge-secret-key";

    /*
     * 24-hour access token.
     */
    private static final long EXPIRATION_TIME =
            1000L * 60 * 60 * 24;

    private SecretKey getSigningKey() {

        return Keys.hmacShaKeyFor(
                SECRET_KEY.getBytes(
                        StandardCharsets.UTF_8
                )
        );
    }

    /**
     * Generate access JWT.
     */
    public String generateToken(
            String username
    ) {

        return Jwts.builder()
                .subject(username)

                /*
                 * Explicitly identify this as
                 * an access token.
                 */
                .claim(
                        "type",
                        "access"
                )

                .issuedAt(
                        new Date()
                )

                .expiration(
                        new Date(
                                System.currentTimeMillis()
                                        + EXPIRATION_TIME
                        )
                )

                .signWith(
                        getSigningKey()
                )

                .compact();
    }

    /**
     * Extract username.
     */
    public String extractUsername(
            String token
    ) {

        return Jwts.parser()
                .verifyWith(
                        getSigningKey()
                )
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    /**
     * Validate JWT signature and expiration.
     */
    public boolean validateToken(
            String token
    ) {

        try {

            Jwts.parser()
                    .verifyWith(
                            getSigningKey()
                    )
                    .build()
                    .parseSignedClaims(token);

            return true;

        } catch (Exception e) {

            System.out.println(
                    "JWT ERROR: "
                            + e.getMessage()
            );

            return false;
        }
    }

    /**
     * Verify that the token is an access token.
     */
    public boolean isAccessToken(
            String token
    ) {

        try {

            String type =
                    Jwts.parser()
                            .verifyWith(
                                    getSigningKey()
                            )
                            .build()
                            .parseSignedClaims(token)
                            .getPayload()
                            .get(
                                    "type",
                                    String.class
                            );

            return "access".equals(
                    type
            );

        } catch (Exception e) {

            return false;
        }
    }
}