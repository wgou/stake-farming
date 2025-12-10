package io.renren.common.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

public class AesNewUtils {

    private static final String AES = "AES";
    private static final String AES_GCM = "AES/GCM/NoPadding";
    private static final int IV_LENGTH = 12;     // 12 字节 GCM 标准
    private static final int TAG_LENGTH = 128;   // GCM 推荐

    private static final byte[] KEY = "a9Tq3Wm7Kf2Rz8Pj6Hc1Vx5Sg4Nw0Byd".getBytes(StandardCharsets.UTF_8);
    /**
     * 加密
     */
    public static String encrypt(String plainText) {
        try {
            // 生成随机 IV
            byte[] iv = new byte[IV_LENGTH];
            SecureRandom.getInstanceStrong().nextBytes(iv);

            Cipher cipher = Cipher.getInstance(AES_GCM);
            SecretKeySpec keySpec = new SecretKeySpec(KEY, AES);
            GCMParameterSpec gcmSpec = new GCMParameterSpec(TAG_LENGTH, iv);

            cipher.init(Cipher.ENCRYPT_MODE, keySpec, gcmSpec);
            byte[] cipherText = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

            // 输出格式：Base64( IV + 密文 )
            ByteBuffer bb = ByteBuffer.allocate(iv.length + cipherText.length);
            bb.put(iv);
            bb.put(cipherText);

            return Base64.getEncoder().encodeToString(bb.array());
        } catch (Exception e) {
            throw new RuntimeException("AES 加密失败", e);
        }
    }

    /**
     * 解密
     */
    public static String decrypt(String base64Cipher) {
        try {
            byte[] decoded = Base64.getDecoder().decode(base64Cipher);

            ByteBuffer bb = ByteBuffer.wrap(decoded);

            byte[] iv = new byte[IV_LENGTH];
            bb.get(iv);

            byte[] cipherText = new byte[bb.remaining()];
            bb.get(cipherText);

            Cipher cipher = Cipher.getInstance(AES_GCM);
            SecretKeySpec keySpec = new SecretKeySpec(KEY, AES);
            GCMParameterSpec gcmSpec = new GCMParameterSpec(TAG_LENGTH, iv);

            cipher.init(Cipher.DECRYPT_MODE, keySpec, gcmSpec);
            byte[] plainText = cipher.doFinal(cipherText);

            return new String(plainText, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("AES 解密失败", e);
        }
    }
}
