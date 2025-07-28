package com.SmartLaundry.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CORSConfig {
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
<<<<<<< HEAD
                registry.addMapping("/**") // apply to all routes
                        .allowedOrigins("http://localhost:3000", "http://localhost:8081") // allow React dev and maybe another client
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // explicitly allow all REST methods
                        .allowedHeaders("*") // allow all headers
                        .allowCredentials(true); // allow cookies/auth headers
=======
                registry.addMapping("/**") // allow all endpoints
                        .allowedOrigins("http://localhost:3000","http://localhost:8081", "http://192.168.1.7:8080") // frontend origin
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true);
>>>>>>> 3e67097f65300117302273536779a532d37e32c1
            }
        };
    }
}

