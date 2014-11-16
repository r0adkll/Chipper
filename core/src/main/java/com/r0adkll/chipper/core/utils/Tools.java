package com.r0adkll.chipper.core.utils;

import android.content.Context;
import android.telephony.TelephonyManager;

import java.security.MessageDigest;
import java.util.UUID;

/**
 * Created by r0adkll on 11/1/14.
 */
public class Tools {

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
     * SHA-256 hash a string
     *
     * @param base      the string to hash
     * @return          the hashed string
     */
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

}
