package com.shemuel.timeline.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class AESUtil {
    private static final String ALGORITHM = "AES";
    private static final String DEFAULT_KEY = "mySecretKey12345"; // 16字节

    public static String encrypt(String value, String key) {
        try {
            if (value == null) return null;
            SecretKeySpec secretKey = new SecretKeySpec(getValidKey(key), ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encrypted = cipher.doFinal(value.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String decrypt(String encrypted, String key) {
        try {
            if (encrypted == null) return null;
            SecretKeySpec secretKey = new SecretKeySpec(getValidKey(key), ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decoded = Base64.getDecoder().decode(encrypted);
            return new String(cipher.doFinal(decoded), StandardCharsets.UTF_8);
        } catch (Exception e) {
            return encrypted;
        }
    }

    private static byte[] getValidKey(String key) {
        if (key == null || key.isEmpty()) key = DEFAULT_KEY;
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        byte[] finalKey = new byte[16]; // 固定16字节
        System.arraycopy(keyBytes, 0, finalKey, 0, Math.min(keyBytes.length, 16));
        return finalKey;
    }
}
