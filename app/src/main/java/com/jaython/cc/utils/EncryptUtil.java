package com.jaython.cc.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class EncryptUtil {
    static {
        System.loadLibrary("jaython");
    }

    public static native String md5(String input);

    public static native String normalMd5(String input);

    public static native String base64encode(String input);

    public static native String base64decode(String input);

    public static native String pwdEncode(String input);

    public static String passwordMD5(String password) {
        String strResult;

        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");

            md5.update(password.getBytes("UTF-8"));

            byte[] byte_password_1 = md5.digest();

            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < byte_password_1.length; ++i)
                sb.append(String.format("%02x", byte_password_1[i]));
            strResult = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();

            return null;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();

            return null;
        }

        return strResult;
    }
}
