package com.systelab.seed.infrastructure.auth.implementation;

import com.systelab.seed.infrastructure.auth.AuthenticationTokenGenerator;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class JWTAuthenticationTokenGenerator implements AuthenticationTokenGenerator {

    private static final String ROLE_CLAIM_NAME = "role";

    @Override
    public String issueToken(String username, String role, String uri) {
        return Jwts.builder()
                .setSubject(username)
                .setClaims(getClaimsWithRole(role))
                .setIssuer(uri)
                .setIssuedAt(getIssuedAt())
                .setExpiration(getExpirationAt())
                .signWith(SignatureAlgorithm.HS512, generateKey()).compact();
    }

    @Override
    public String validateToken(String token) {
        return Jwts.parser()
                .setSigningKey(generateKey())
                .parseClaimsJws(token)
                .getBody()
                .get(ROLE_CLAIM_NAME)
                .toString();
    }

    @Override
    public Key generateKey() {
        // TODO: Move to external configuration
        String keyString = "simplekey";
        return new SecretKeySpec(keyString.getBytes(), 0, keyString.getBytes().length, "DES");
    }

    private Date getIssuedAt() {
        return new Date();
    }

    private Date getExpirationAt() {
        LocalDateTime localDateTime = LocalDateTime.now().plusMinutes(15L);
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    private Claims getClaimsWithRole(String role) {
        Claims claims = Jwts.claims();
        claims.put(ROLE_CLAIM_NAME, role);
        return claims;
    }
}