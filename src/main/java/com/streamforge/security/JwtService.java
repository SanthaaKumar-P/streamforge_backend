package com.streamforge.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;


@Service
public class JwtService {


    private static final String SECRET_KEY =
            "streamforge-secret-key-streamforge-secret-key";


    private static final long EXPIRATION_TIME =
            1000 * 60 * 60 * 24;


    private Key getSigningKey(){

        return Keys.hmacShaKeyFor(
                SECRET_KEY.getBytes(StandardCharsets.UTF_8)
        );

    }



    public String generateToken(String username){


        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(
                        new Date(
                                System.currentTimeMillis()
                                + EXPIRATION_TIME
                        )
                )
                .signWith(getSigningKey())
                .compact();

    }



    public String extractUsername(String token){

        return Jwts.parser()
                .verifyWith((javax.crypto.SecretKey)getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();

    }



    public boolean validateToken(String token){

        try{

            Jwts.parser()
                    .verifyWith(
                            (javax.crypto.SecretKey)getSigningKey()
                    )
                    .build()
                    .parseSignedClaims(token);

            return true;

        }
        catch(Exception e){

            return false;

        }

    }

}