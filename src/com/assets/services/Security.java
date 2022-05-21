package com.assets.services;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

public class Security {
    private static final byte[] salt = new byte[]{1, 32, 45, 75, 63, 20, 12, 4, 6, 0, 75, 97, 36, 12, 47, 68};

    public static String getHash(String password) {
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 50000, 128);
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");

            byte[] hash = factory.generateSecret(spec).getEncoded();
            StringBuilder str = new StringBuilder();
            for (byte b : hash)
                str.append(String.format("%02X", b));
            return str.toString();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
            System.out.println("Algorithm key PBKDF2WithHmacSHA1 not created. Message: " + e.getMessage());
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Algorithm PBKDF2WithHmacSHA1 not found. Message: " + e.getMessage());
        }
        return null;
    }
}
