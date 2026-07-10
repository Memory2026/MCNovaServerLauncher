package com.xingci.mcnsl.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 哈希工具类
 */
public final class HashUtils {

    private HashUtils() {
    }

    /**
     * 计算文件 SHA1
     */
    public static String sha1(Path file) throws IOException {
        return digest(file, "SHA-1");
    }

    /**
     * 计算文件 SHA256
     */
    public static String sha256(Path file) throws IOException {
        return digest(file, "SHA-256");
    }

    /**
     * 计算文件 MD5
     */
    public static String md5(Path file) throws IOException {
        return digest(file, "MD5");
    }

    /**
     * 计算字节数组 SHA1
     */
    public static String sha1(byte[] bytes) {
        return digest(bytes, "SHA-1");
    }

    /**
     * 计算字节数组 SHA256
     */
    public static String sha256(byte[] bytes) {
        return digest(bytes, "SHA-256");
    }

    /**
     * 计算字节数组 MD5
     */
    public static String md5(byte[] bytes) {
        return digest(bytes, "MD5");
    }

    /**
     * 校验文件 SHA1
     */
    public static boolean verifySha1(Path file, String expected)
            throws IOException {

        if (!Files.exists(file)) {
            return false;
        }

        return sha1(file).equalsIgnoreCase(expected);
    }

    /**
     * 校验文件 SHA256
     */
    public static boolean verifySha256(Path file, String expected)
            throws IOException {

        if (!Files.exists(file)) {
            return false;
        }

        return sha256(file).equalsIgnoreCase(expected);
    }

    /**
     * 校验文件 MD5
     */
    public static boolean verifyMd5(Path file, String expected)
            throws IOException {

        if (!Files.exists(file)) {
            return false;
        }

        return md5(file).equalsIgnoreCase(expected);
    }

    /**
     * 计算文件摘要
     */
    private static String digest(Path file, String algorithm)
            throws IOException {

        try (InputStream in = Files.newInputStream(file)) {

            MessageDigest md = MessageDigest.getInstance(algorithm);

            byte[] buffer = new byte[8192];

            int len;

            while ((len = in.read(buffer)) != -1) {
                md.update(buffer, 0, len);
            }

            return toHex(md.digest());

        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * 计算字节摘要
     */
    private static String digest(byte[] bytes, String algorithm) {

        try {

            MessageDigest md = MessageDigest.getInstance(algorithm);

            md.update(bytes);

            return toHex(md.digest());

        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * byte[] -> hex
     */
    private static String toHex(byte[] bytes) {

        StringBuilder builder = new StringBuilder(bytes.length * 2);

        for (byte b : bytes) {
            builder.append(String.format("%02x", b));
        }

        return builder.toString();
    }

}