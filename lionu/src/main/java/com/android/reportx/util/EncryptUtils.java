package com.android.reportx.util;

import android.os.Build;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import android.util.Base64;

public class EncryptUtils {
    //md5开关
    public static boolean md5Switch = true;
    //AES算法相关
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/ECB/PKCS5Padding";

    public static String md5(String input) {
        try {
            // 获取MD5实例
            MessageDigest md = MessageDigest.getInstance("MD5");
            // 计算输入字符串的哈希值
            byte[] messageDigest = md.digest(input.getBytes());
            // 将字节数组转换为十六进制字符串
            StringBuilder hexString = new StringBuilder();
            for (byte b : messageDigest) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            //e.printStackTrace();
            return null;
        }
    }

    /**
     * 生成一个随机的AES密钥
     *
     * @return 生成的密钥
     * @throws Exception 如果生成密钥失败
     */
    public static SecretKey generateKey() throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);
        keyGenerator.init(256); // 可以选择128, 192, 256位密钥长度
        return keyGenerator.generateKey();
    }

    /**
     * 将密钥转换为Base64编码的字符串
     *
     * @param secretKey 密钥
     * @return Base64编码的密钥字符串
     */
    public static String secretKeyToBase64(SecretKey secretKey) {
        return Base64.encodeToString(secretKey.getEncoded(), Base64.DEFAULT);
    }

    /**
     * 将Base64编码的字符串转换为密钥
     *
     * @param encodedKey Base64编码的密钥字符串
     * @return 密钥
     */
    public static SecretKey base64ToSecretKey(String encodedKey) {
        byte[] decodedKey = Base64.decode(encodedKey, Base64.DEFAULT);
        return new SecretKeySpec(decodedKey, 0, decodedKey.length, ALGORITHM);
    }

    /**
     * 使用AES加密字符串
     *
     * @param data      要加密的数据
     * @param secretKey 密钥
     * @return 加密后的Base64编码字符串
     * @throws Exception 如果加密失败
     */
    public static String encrypt(String data, SecretKey secretKey) throws Exception {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedBytes = cipher.doFinal(data.getBytes());
        return Base64.encodeToString(encryptedBytes, Base64.DEFAULT);
    }

    /**
     * 使用AES解密字符串
     *
     * @param encryptedData 加密后的Base64编码字符串
     * @param secretKey     密钥
     * @return 解密后的字符串
     * @throws Exception 如果解密失败
     */
    public static String decrypt(String encryptedData, SecretKey secretKey) throws Exception {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decodedBytes = Base64.decode(encryptedData, Base64.DEFAULT);
        byte[] decryptedBytes = cipher.doFinal(decodedBytes);
        return new String(decryptedBytes);
    }
}
