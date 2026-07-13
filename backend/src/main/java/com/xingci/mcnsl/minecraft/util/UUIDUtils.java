package com.xingci.mcnsl.minecraft.util;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class UUIDUtils {

    public static UUID generateOfflineUUID(String username) {
        String data = "OfflinePlayer:" + username;
        java.security.MessageDigest md5;
        try {
            md5 = java.security.MessageDigest.getInstance("MD5");
        } catch (java.security.NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        byte[] hash = md5.digest(data.getBytes(StandardCharsets.UTF_8));
        hash[6] &= 0x0f;
        hash[6] |= 0x30;
        hash[8] &= 0x3f;
        hash[8] |= 0x80;
        
        long msb = 0;
        long lsb = 0;
        for (int i = 0; i < 8; i++) {
            msb = (msb << 8) | (hash[i] & 0xff);
        }
        for (int i = 8; i < 16; i++) {
            lsb = (lsb << 8) | (hash[i] & 0xff);
        }
        
        return new UUID(msb, lsb);
    }

    public static String generateOfflineUUIDString(String username) {
        return generateOfflineUUID(username).toString();
    }

}