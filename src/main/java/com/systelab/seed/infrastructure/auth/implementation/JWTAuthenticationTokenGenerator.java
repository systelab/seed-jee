package com.systelab.seed.infrastructure.auth.implementation;

import com.systelab.seed.infrastructure.auth.AuthenticationTokenGenerator;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.crypto.spec.SecretKeySpec;
import javax.inject.Inject;
import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class JWTAuthenticationTokenGenerator implements AuthenticationTokenGenerator {

    private String jwtKey;
    private String jwtAlgorithm;

    private static final String ROLE_CLAIM_NAME = "role";

    @Inject
    public JWTAuthenticationTokenGenerator(@ConfigProperty(name = "jwt.key", defaultValue = "simplekey") String jwtKey,
                                           @ConfigProperty(name = "jwt.algorithm", defaultValue = "DES") String jwtAlgorithm) {
        this.jwtKey = jwtKey;
        this.jwtAlgorithm = jwtAlgorithm;
    }

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
    public String getRoleFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(generateKey())
                .parseClaimsJws(token)
                .getBody()
                .get(ROLE_CLAIM_NAME)
                .toString();
    }

    public Key generateKey() {
        return new SecretKeySpec(jwtKey.getBytes(), 0, jwtKey.getBytes().length, jwtAlgorithm);
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