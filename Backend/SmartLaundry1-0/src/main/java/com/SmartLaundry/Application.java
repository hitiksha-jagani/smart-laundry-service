package com.SmartLaundry;

import com.SmartLaundry.util.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableCaching
@EnableScheduling
@PropertySource(value = "classpath:.env", ignoreResourceNotFound = true)
public class Application {

	public static void main(String[] args) {
		EnvUtils.loadEnv("D:\\MSCIT\\summerinternship\\application-custom-properties.env");
		SpringApplication.run(Application.class, args);
	}
}
