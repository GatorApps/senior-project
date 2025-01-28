package org.gatorapps.garesearch.utils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class SessionUtil {
    public static String sign(String value, String secret) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secret.getBytes(), "HmacSHA256"));
        byte[] signature = mac.doFinal(value.getBytes());
        return value + "." + Base64.getUrlEncoder().withoutPadding().encodeToString(signature);
    }

    public static boolean verify(String signedValue, String[] secrets) throws Exception {
        String[] parts = signedValue.split("\\.");
        if (parts.length != 2) return false;

        String value = parts[0];
        String signature = parts[1];

        for (String secret : secrets) {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(), "HmacSHA256"));
            String computedSignature = Base64.getUrlEncoder().withoutPadding().encodeToString(mac.doFinal(value.getBytes()));
            if (computedSignature.equals(signature)) {
                return true;
            }
        }

        return false;
    }
}
