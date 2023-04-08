package com.georgeneokq.engine.hashing;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.CRC32;

public class HashUtil {
    public static String crc32(byte[] content) {
        CRC32 crc32 = new CRC32();
        crc32.update(content, 0, content.length);
        return Long.toHexString(crc32.getValue());
    }

    public static String md5(byte[] content) {
        String hash = "";
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("md5");
            messageDigest.update(content);
            byte[] digest = messageDigest.digest();
            hash = bytesToHex(digest);
        } catch(NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return hash;
    }

    public static String bytesToHex(byte[] bytes) {
        final char[] HEX_ARRAY = new char[] {
            '0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'
        };
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }}
