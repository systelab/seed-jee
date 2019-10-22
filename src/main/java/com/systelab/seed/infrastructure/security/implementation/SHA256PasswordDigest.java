package com.systelab.seed.infrastructure.security.implementation;

import com.systelab.seed.BaseException;
import com.systelab.seed.infrastructure.SLF4JLogger;
import com.systelab.seed.infrastructure.security.PasswordDigest;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import javax.inject.Inject;
import org.slf4j.Logger;


public class SHA256PasswordDigest implements PasswordDigest {
    @Inject
    @SLF4JLogger
    private Logger logger;

    @Override
    public String digest(String plainTextPassword) throws BaseException {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(plainTextPassword.getBytes("UTF-8"));
            byte[] passwordDigest = md.digest();
            return new String(Base64.getEncoder().encode(passwordDigest));
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            logger.error(e.getMessage(), e);
            throw new BaseException("Incorrect algorithm implementation", e, BaseException.ErrorCode.DEFAULT_ERROR);
        }
    }
}
