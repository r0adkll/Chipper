package com.r0adkll.chipper.core.utils;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.RandomStringUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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
    public static String sha256(String input){
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
            return "";
        }

        md.update(input.getBytes());
        byte[] shaDig = md.digest();
        return new String(Hex.encodeHex(shaDig));
    }

}
