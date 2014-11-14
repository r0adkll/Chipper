package com.r0adkll.chipper.core.utils;

import android.content.Context;
import android.telephony.TelephonyManager;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.RandomStringUtils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

/**
 * Created by r0adkll on 11/1/14.
 */
public class Tools {

    /**
     * Generate a random AlphaNumeric string to use as a token
     *
     * @return      the 128 length random alpha numeric string
     */
    public static String generateToken(){
        return RandomStringUtils.randomAlphanumeric(128);
    }

    /**
     * Generate a unique device identifier that can be replicated
     * on the device
     *
     * @param ctx
     * @return
     */
    public static String generateUniqueDeviceId(Context ctx){
        final TelephonyManager tm = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);

        final String tmDevice, tmSerial, androidId;
        tmDevice = "" + tm.getDeviceId();
        tmSerial = "" + tm.getSimSerialNumber();
        androidId = "" + android.provider.Settings.Secure.getString(ctx.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

        UUID deviceUuid = new UUID(androidId.hashCode(), ((long)tmDevice.hashCode() << 32) | tmSerial.hashCode());
        return deviceUuid.toString();
    }

    /**
     * Get the time from epoch in seconds
     *
     * @return      epoch seconds
     */
    public static long time(){
        return System.currentTimeMillis()/1000;
    }

    /**
     * Hash a string to SHA1
     *
     * @param input     the string to hash
     * @return          the hashed string
     */
    public static String sha1(String input)  {
        try {
            MessageDigest mDigest = MessageDigest.getInstance("SHA1");
            byte[] result = mDigest.digest(input.getBytes());
            return Hex.encodeHexString(result);
        } catch (NoSuchAlgorithmException e){
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Hash a string to sha256
     *
     * @param input     the string to hash
     * @return          the hashed string, or an empty one
     */
    public static String sha256old(String input){
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
            return "";
        }

        try {
            md.update(input.getBytes("UTF-8"));
            byte[] shaDig = md.digest();
            return new String(Hex.encodeHexString(shaDig));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String sha256(String base) {
        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(base.getBytes("UTF-8"));
            StringBuffer sb = new StringBuffer();

            //convert the byte to hex format method 1
            for (int i = 0; i < hash.length; i++) {
                sb.append(Integer.toString((hash[i] & 0xff) + 0x100, 16).substring(1));
            }

            return sb.toString();
        } catch(Exception ex){
            throw new RuntimeException(ex);
        }
    }

    private static String bytesToHexString(byte[] bytes) {
        // http://stackoverflow.com/questions/332079
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }



}
