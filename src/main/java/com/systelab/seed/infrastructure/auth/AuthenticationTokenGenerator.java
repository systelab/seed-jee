package com.systelab.seed.infrastructure.auth;

public interface AuthenticationTokenGenerator {
    String issueToken(String username, String role, String uri);

    String getRoleFromToken(String token);
}
