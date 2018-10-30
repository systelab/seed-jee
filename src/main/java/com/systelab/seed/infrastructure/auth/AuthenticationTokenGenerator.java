package com.systelab.seed.infrastructure.auth;

import java.security.Key;

public interface AuthenticationTokenGenerator {
    String issueToken(String username, String role, String uri);

    String validateToken(String token) throws Exception;

    Key generateKey();
}
