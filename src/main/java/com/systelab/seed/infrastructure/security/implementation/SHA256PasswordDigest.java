package com.systelab.seed.infrastructure.security.implementation;

import com.systelab.seed.BaseException;
import com.systelab.seed.infrastructure.security.PasswordDigest;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;


public class SHA256PasswordDigest implements PasswordDigest {

    @Override
    public String digest(String plainTextPassword) throws BaseException {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(plainTextPassword.getBytes(StandardCharsets.UTF_8));
            byte[] passwordDigest = md.digest();
            return new String(Base64.getEncoder().encode(passwordDigest));
        } catch (NoSuchAlgorithmException e) {
            throw new BaseException("Incorrect algorithm implementation", e, BaseException.ErrorCode.DEFAULT_ERROR);
        }
    }
}
