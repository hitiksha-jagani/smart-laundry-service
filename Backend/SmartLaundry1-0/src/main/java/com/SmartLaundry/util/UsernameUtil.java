package com.SmartLaundry.util;

import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

//@author Hitiksha Jagani
@Component
public class UsernameUtil {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
    );

    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "^[0-9]{10}$"
    );

    public static boolean isEmail(String input) {
        return EMAIL_PATTERN.matcher(input).matches();
    }

    public static boolean isPhone(String input) {
        return PHONE_PATTERN.matcher(input).matches();
    }

    public static String getType(String input) {
        if (isEmail(input)) return "email";
        if (isPhone(input)) return "phone";
        throw new IllegalArgumentException("Invalid username: must be email or phone");
    }
}

