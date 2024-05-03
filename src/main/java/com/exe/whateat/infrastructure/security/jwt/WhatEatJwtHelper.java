package com.exe.whateat.infrastructure.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class WhatEatJwtHelper {

    private static final String EMAIL = "email";

    @Value("${whateat.jwt.token.secret}")
    private String secretForToken;

    @Value("${whateat.jwt.refreshtoken.secret}")
    private String secretForRefreshToken;

    @Value("${spring.application.name}")
    private String issuer;

    /**
     * Currently 1 hour.
     */
    @Value("${whateat.jwt.token.lifetime}")
    private long tokenLifetime;

    /**
     * Currently 1 year.
     */
    @Value("${whateat.jwt.refreshtoken.lifetime}")
    private long refreshTokenLifetime;

    public final String generateToken() {
        final Algorithm algorithm = Algorithm.HMAC256(secretForToken);
        final Instant currentTime = Instant.now();
        return JWT.create()
                .withIssuer(issuer)
                .withSubject("dummySubject")
                .withIssuedAt(currentTime)
                .withExpiresAt(currentTime.plusSeconds(tokenLifetime))
                .withClaim(EMAIL, "dummyEmail")
                .sign(algorithm);
    }

    /**
     * Refresh token will have special claim related to "Make Love, Not War" slogan :)
     *
     * @return The refresh token.
     */
    public final String generateRefreshToken() {
        final Algorithm algorithm = Algorithm.HMAC256(secretForRefreshToken);
        final Instant currentTime = Instant.now();
        return JWT.create()
                .withIssuer(issuer)
                .withSubject("dummySubject")
                .withIssuedAt(currentTime)
                .withExpiresAt(currentTime.plusSeconds(refreshTokenLifetime))
                .withClaim(EMAIL, "dummyEmail")
                .withClaim("makeLove", "notWar")
                .sign(algorithm);
    }

    public final DecodedJWT verifyToken(String token) {
        final Algorithm algorithm = Algorithm.HMAC256(secretForToken);
        final JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer(issuer)
                .withClaimPresence(EMAIL)
                .build();
        return verifier.verify(token);
    }

    public final DecodedJWT verifyRefreshToken(String token) {
        final Algorithm algorithm = Algorithm.HMAC256(secretForRefreshToken);
        final JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer(issuer)
                .withClaimPresence(EMAIL)
                .withClaim("makeLove", "notWar")
                .build();
        return verifier.verify(token);
    }

    public final String extractEmail(DecodedJWT decodedJWT) {
        return decodedJWT.getClaim(EMAIL).asString();
    }
}
