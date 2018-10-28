package com.systelab.seed.infrastructure.security.implementation;

import com.systelab.seed.BaseException;
import com.systelab.seed.infrastructure.security.PasswordDigest;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SHA256PasswordDigest implements PasswordDigest
{
    private Logger logger;

    @Override
    public String digest(String plainTextPassword) throws BaseException {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
            md.update(plainTextPassword.getBytes("UTF-8"));
            byte[] passwordDigest = md.digest();
            return new String(Base64.getEncoder().encode(passwordDigest));
        } catch (NoSuchAlgorithmException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            throw new BaseException("Incorrect algorithm implementation", e, BaseException.ErrorCode.DEFAULT_ERROR);
        } catch (UnsupportedEncodingException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            throw new BaseException("Incorrect algorithm implementation", e, BaseException.ErrorCode.DEFAULT_ERROR);
        }
    }
}
