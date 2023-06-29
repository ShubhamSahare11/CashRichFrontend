package com.cashrich.coinrich.utils;


import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class Utility {

    public static boolean isValidPassword(String password) {
        boolean hasUppercase = false;
        boolean hasLowercase = false;
        boolean hasDigit = false;
        boolean hasSpecialChar = false;

        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) {
                hasUppercase = true;
            } else if (Character.isLowerCase(c)) {
                hasLowercase = true;
            } else if (Character.isDigit(c)) {
                hasDigit = true;
            } else {
                // Check for special characters based on your criteria
                // For example, you can define an array of special characters and check if the password contains any of them
                char[] specialChars = {'!', '@', '#', '$', '%', '&'};
                for (char specialChar : specialChars) {
                    if (c == specialChar) {
                        hasSpecialChar = true;
                        break;
                    }
                }
            }
        }

        return hasUppercase && hasLowercase && hasDigit && hasSpecialChar;
    }

    public static String hashPassword(String password) throws Exception {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            return bytesToHexString(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            // Handle the exception
            throw new Exception("Error Occurred");
        }
    }

    private static String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }


    public static Map<String, String> getOriginHeader() {
        Map<String, String> header = new HashMap<>();
        header.put("Origin", "CashRichFrontend");
        return header;
    }

    public static Map<String, String> getMarketApiHeaders(String sessionId) {
        Map<String, String> header = getOriginHeader();
        header.put("Session-id", sessionId);
        return header;
    }
}
