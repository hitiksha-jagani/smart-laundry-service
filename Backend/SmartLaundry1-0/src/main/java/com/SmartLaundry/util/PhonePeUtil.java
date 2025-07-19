package com.SmartLaundry.util;
import org.apache.commons.codec.digest.DigestUtils;

public class PhonePeUtil {
    public static String generateXVerify(String base64Payload, String path, String saltKey, String saltIndex) {
        String data = base64Payload + path + saltKey;
        String sha256 = DigestUtils.sha256Hex(data);
        return sha256 + "###" + saltIndex;
    }
}
