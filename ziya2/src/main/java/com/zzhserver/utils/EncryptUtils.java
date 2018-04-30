package com.zzhserver.utils;

import java.security.MessageDigest;
import java.text.SimpleDateFormat;

/**
 * Created by Administrator on 2017/12/10 0010.
 */

public class EncryptUtils {
    //MD5 32位
    public static String MD5(String s) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytes = md.digest(s.getBytes("utf-8"));
            return toHex(bytes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    //MD5 16位
    public static String MD5X(String s) {
        return MD5(s).substring(8, 24);
    }

    private static String toHex(byte[] bytes) {
        final char[] HEX_DIGITS = "0123456789ABCDEF".toCharArray();
        StringBuilder ret = new StringBuilder(bytes.length * 2);
        for (int i = 0; i < bytes.length; i++) {
            ret.append(HEX_DIGITS[(bytes[i] >> 4) & 0x0f]);
            ret.append(HEX_DIGITS[bytes[i] & 0x0f]);
        }
        return ret.toString();
    }

    public static String formatMM(long time) {
        SimpleDateFormat spf = new SimpleDateFormat("MM-dd hh:mm");
        return spf.format(time);
    }
}
