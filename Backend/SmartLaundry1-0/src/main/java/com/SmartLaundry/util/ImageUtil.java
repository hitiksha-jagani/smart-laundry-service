package com.SmartLaundry.util;

import java.util.Base64;

public class ImageUtil {
    public static String toBase64(byte[] bytes) {
        if (bytes == null) return null;
        return Base64.getEncoder().encodeToString(bytes);
    }
}
