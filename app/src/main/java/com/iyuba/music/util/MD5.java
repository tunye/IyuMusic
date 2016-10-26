package com.iyuba.music.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5校验
 *
 * @author 陈彤
 */
public class MD5 {
    private static final char[] DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    public static String getMD5ofStr(String text) {
        MessageDigest msgDigest = null;
        try {
            msgDigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(
                    "System doesn't support MD5 algorithm.");
        }
        try {
            msgDigest.update(text.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(
                    "System doesn't support your  EncodingException.");
        }
        byte[] bytes = msgDigest.digest();
        return new String(encodeHex(bytes));
    }

    public static String md5_16(String text) {
        return getMD5ofStr(text).substring(8, 24);
    }

    public static char[] encodeHex(byte[] data) {
        int l = data.length;
        char[] out = new char[l << 1];
        int i = 0;
        for (int j = 0; i < l; ++i) {
            out[(j++)] = DIGITS[((0xF0 & data[i]) >>> 4)];
            out[(j++)] = DIGITS[(0xF & data[i])];
        }
        return out;
    }
}