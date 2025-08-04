package com.SmartLaundry.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

//@Configuration
//public class CORSConfig {
//
//    @Bean
//    public WebMvcConfigurer corsConfigurer() {
//        return new WebMvcConfigurer() {
//            @Override
//            public void addCorsMappings(CorsRegistry registry) {
//                registry.addMapping("/**")
//                        .allowedOrigins("http://localhost:3000","http://localhost:8081","http://10.61.204.155:3000","http://10.61.204.155:8081", "http://10.61.204.155:3000",         // React web (from other device on LAN)
//                                "http://localhost:8081",             // Expo DevTools (on your machine)
//                                "http://10.61.204.155:8081",         // Expo DevTools (accessed from LAN)
//                                "http://10.61.204.155:19006",        // Expo Web in browser (optional)
//                                "exp://10.61.204.155:19000",
//                                "http://10.61.204.155:8080") // allow frontend origin
//                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
//                        .allowedHeaders("*")
//                        .allowCredentials(true); // if using cookies or credentials
//            }
//        };
//    }
//}
@Configuration
public class CORSConfig {
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins(
                                "http://localhost:3000",
                                "http://localhost:8081",
                                "http://10.61.204.155:3000",
                                "http://10.61.204.155:8081",
                                "http://10.61.204.155:19006",
                                "http://10.61.204.155:8080",
                                "http://192.168.29.110:8081"
                        )
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .exposedHeaders("Authorization") // ✅ expose if token sent back
                        .allowCredentials(true);
            }
        };
    }
}


