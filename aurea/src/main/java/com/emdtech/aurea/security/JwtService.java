package com.emdtech.aurea.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class JwtService {

    private final JwtEncoder jwtEncoder;
    private final long expirationSeconds;

    public JwtService(
            JwtEncoder jwtEncoder,
            @Value("${aurea.jwt.expiration-seconds}")
            long expirationSeconds) {

        this.jwtEncoder = jwtEncoder;
        this.expirationSeconds = expirationSeconds;
    }

    public String generarToken(
            String username,
            UserRole role) {

        Instant now = Instant.now();

        Instant expiration =
                now.plusSeconds(expirationSeconds);

        JwtClaimsSet claims =
                JwtClaimsSet.builder()
                        .subject(username)
                        .issuedAt(now)
                        .expiresAt(expiration)
                        .claim(
                                "role",
                                role.name()
                        )
                        .build();

        Jwt jwt =
                jwtEncoder.encode(
                        JwtEncoderParameters.from(claims)
                );

        return jwt.getTokenValue();
    }
}