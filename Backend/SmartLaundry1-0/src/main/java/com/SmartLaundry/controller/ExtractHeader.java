package com.SmartLaundry.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class ExtractHeader {
//    public String extractTokenFromHeader(HttpServletRequest request) {
//        String authHeader = request.getHeader("Authorization");
//        if (authHeader != null && authHeader.startsWith("Bearer ")) {
//            return authHeader.substring(7);
//        }
//        throw new RuntimeException("Missing or invalid Authorization header");
//    }
        public String extractTokenFromHeader(HttpServletRequest request) {
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                throw new IllegalArgumentException("Missing or invalid Authorization header");
            }
            return authHeader.substring(7); // after "Bearer "
        }

}
