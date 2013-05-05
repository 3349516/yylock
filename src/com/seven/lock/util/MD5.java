package com.seven.lock.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5����
 * @author ll
 *
 */
public class MD5 {
    private MessageDigest md;
    private static MD5 md5;

    private MD5() {
        try {
            md = MessageDigest.getInstance("md5");
        } catch (NoSuchAlgorithmException e) {
            System.out.println("MD5 ERROR");
        }
    }

    // ����һ��MD5ʵ��
    public static MD5 getInstance() {
        if (null != md5)
            return md5;
        else {
            makeInstance();
            return md5;
        }
    }

    // ��֤ͬһʱ��ֻ��һ���߳���ʹ��MD5����
    private static synchronized void makeInstance() {
        if (null == md5)
            md5 = new MD5();
    }

    public String createMD5(String pass) {
        md.update(pass.getBytes());
        byte[] b = md.digest();
        return byteToHexString(b);
    }

    private String byteToHexString(byte[] b) {
        StringBuffer sb = new StringBuffer();
        String temp = "";
        for (int i = 0; i < b.length; i++) {
            temp = Integer.toHexString(b[i] & 0Xff);
            if (temp.length() == 1) {
                temp = "0" + temp;
            }
            sb.append(temp);
        }
        return sb.toString();
    }
}