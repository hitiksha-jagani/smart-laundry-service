package com.SmartLaundry.config;

import com.SmartLaundry.Subscriber.RedisMessageSubscriber;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.*;

import java.time.Duration;

@Configuration
public class RedisConfig{
    @Value("${spring.redis.host}")
    private String redisHost;

    @Value("${spring.redis.port}")
    private int redisPort;

    @Value("${spring.redis.password}")
    private String redisPassword;

    @Value("${spring.redis.ssl.enabled:true}")
    private boolean redisSslEnabled;


    @Autowired
    private RedisMessageSubscriber messageSubscriber;

    public RedisConfig() {
        System.out.println(">>> RedisConfig loaded");
    }

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(redisHost);
        config.setPort(redisPort);
        config.setPassword(redisPassword);

        LettuceClientConfiguration clientConfig;

        if (redisSslEnabled) {
            clientConfig = LettuceClientConfiguration.builder()
                    .useSsl()
                    .build();
            System.out.println("🔒 Redis SSL is ENABLED");
        } else {
            clientConfig = LettuceClientConfiguration.builder()
                    .build();
            System.out.println("🔓 Redis SSL is DISABLED");
        }

        return new LettuceConnectionFactory(config, clientConfig);
    }



    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }

    // RedisTemplate configuration
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory, ObjectMapper objectMapper){
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(objectMapper);

        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(serializer );
        template.afterPropertiesSet();
        return template;
    }

    // Message Listener for Redis Pub/Sub
    @Bean
    public MessageListenerAdapter messageListener() {
        return new MessageListenerAdapter(messageSubscriber);
    }

    @Bean
    public RedisMessageListenerContainer redisContainer(RedisConnectionFactory connectionFactory) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(messageListener(), new PatternTopic("my-channel"));
        return container;
    }

    // Redis Cache Configuration
    @Bean
    public RedisCacheConfiguration cacheConfiguration() {
        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofDays(30))
                .disableCachingNullValues()
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer())
                );
    }

    // Register CacheManager to wire Spring's @Cacheable
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(cacheConfiguration())
                .build();
    }

    @PostConstruct
    public void testRedisConnection() {
        System.out.println("✅ RedisConfig PostConstruct check complete");
        System.out.println("🔍 Redis Host: " + redisHost);
        System.out.println("🔍 Redis Port: " + redisPort);
        System.out.println("🔍 Redis Password: " + (redisPassword != null ? "******" : "null"));
        System.out.println("🔍 Redis SSL Enabled: " + redisSslEnabled);
    }
}
