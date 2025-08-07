package com.SmartLaundry.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudinaryConfig {

    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "dot327hzh",
                "api_key", "454864482526345",
                "api_secret", "Jk7YlvqTciaf9GJ-PddoUCKe4jc"
        ));
    }
}

