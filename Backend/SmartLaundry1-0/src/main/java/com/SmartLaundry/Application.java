package com.SmartLaundry;
import com.SmartLaundry.config.CustomEnvInitializer;
import com.SmartLaundry.config.DotenvLoader;
import com.SmartLaundry.util.*;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication(scanBasePackages = "com.SmartLaundry")
@EnableCaching
@EnableScheduling
@EnableJpaRepositories(basePackages = "com.SmartLaundry.repository")
@EnableAutoConfiguration(exclude = {
		org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration.class
})
public class Application {

	public static void main(String[] args) {

		SpringApplication.run(Application.class, args);

	}

	@PostConstruct
	public void checkRedis() {
		System.out.println("🔍 Spring Redis Host: " + System.getProperty("spring.redis.host"));
		System.out.println("🔍 ENV Redis Host: " + System.getenv("SPRING_REDIS_HOST"));
	}


}