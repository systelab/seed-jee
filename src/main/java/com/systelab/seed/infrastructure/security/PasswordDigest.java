package com.systelab.seed.infrastructure.security;

import com.systelab.seed.BaseException;

public interface PasswordDigest {
    String digest(String plainTextPassword) throws BaseException;
}
