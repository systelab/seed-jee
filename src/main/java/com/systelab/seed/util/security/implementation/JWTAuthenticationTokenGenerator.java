package com.systelab.seed.util.security.implementation;

import com.systelab.seed.util.security.AuthenticationTokenGenerator;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class JWTAuthenticationTokenGenerator implements AuthenticationTokenGenerator {

    @Override
    public String issueToken(String username, String role, String uri) {
        Key key = generateKey();

        Claims customClaims = Jwts.claims();
        customClaims.put("role", role);

        return Jwts.builder().setClaims(customClaims).setSubject(username).setIssuer(uri).
                setIssuedAt(getIssuedAt()).setExpiration(getExpirationAt()).
                signWith(SignatureAlgorithm.HS512, key).compact();
    }

    @Override
    public String validateToken(String token) throws Exception {
        Key key = generateKey();
        Jws<Claims> claims = Jwts.parser().setSigningKey(key).parseClaimsJws(token);
        Claims c = claims.getBody();
        return c.get("role").toString();
    }

    @Override
    public Key generateKey() {
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

}