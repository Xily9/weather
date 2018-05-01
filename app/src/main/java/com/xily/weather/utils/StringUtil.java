package com.xily.weather.utils;

import android.text.TextUtils;
import android.util.Base64;

import java.security.Key;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.X509EncodedKeySpec;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by Xily on 2017/11/27.
 */

public class StringUtil {
    private static String key = "n&1P)J^A";
    private static final String RSA_PUBLIC = "";
    private static RSAPublicKey publicKey = null;

    public static String encodeWithDes(String str) throws Exception {
        AlgorithmParameterSpec ivparameterspec = new IvParameterSpec(key.getBytes());
        Key secretkey = new SecretKeySpec(key.getBytes(), "DES");
        Cipher instance = Cipher.getInstance("DES/CBC/PKCS5Padding");
        instance.init(1, secretkey, ivparameterspec);
        return Base64.encodeToString(instance.doFinal(str.getBytes()), Base64.NO_WRAP);
    }

    public static String getSeed(String str, String str2) {
        String[] strArr = new String[]{str2.substring(0, 4), str2.substring(4, 6), str2.substring(6, 8), str2.substring(8, 10), str2.substring(10, 12), str2.substring(12, 14)};
        String str3 = "";
        int i = 0;
        for (int parseInt = (Integer.parseInt(str) % 63) + 1; parseInt > 0; parseInt >>= 1) {
            if (parseInt % 2 != 0) {
                str3 = strArr[i] + str3;
            }
            i++;
        }
        return str3;
    }

    public static void loadPublicKey() {
        try {
            publicKey = (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(Base64.decode(RSA_PUBLIC, 0)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String encryptWithRSA(String plainData) {
        if (publicKey == null) {
            loadPublicKey();
        }
        try {
            LogUtil.d("paper", plainData);
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(1, publicKey);
            return Base64.encodeToString(cipher.doFinal(plainData.getBytes()), Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String decryptWithRSA(String encryedData) throws Exception {
        if (publicKey == null) {
            throw new NullPointerException("decrypt PublicKey is null !");
        }
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(2, publicKey);
        return new String(cipher.doFinal(Base64.decode(encryedData, 0)));
    }

    //格式化时间
    public static String getCurrentTime(String str) {
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(str, Locale.getDefault());
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        return simpleDateFormat.format(date);
    }

    public static String getCurrentTimeStr() {
        return getCurrentTime("yyyyMMddHHmmss");
    }

    public static long getDays(long datestart) {
        return (Calendar.getInstance().getTimeInMillis() - datestart) / (24 * 3600 * 1000);
    }

    public static boolean isInteger(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }

    public static String md5(String string) {
        if (TextUtils.isEmpty(string)) {
            return "";
        }
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
            byte[] bytes = md5.digest(string.getBytes());
            String result = "";
            for (byte b : bytes) {
                String temp = Integer.toHexString(b & 0xff);
                if (temp.length() == 1) {
                    temp = "0" + temp;
                }
                result += temp;
            }
            return result;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}
