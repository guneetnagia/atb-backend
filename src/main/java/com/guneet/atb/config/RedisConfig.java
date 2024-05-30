package com.guneet.atb.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
// import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
// import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
// import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
// import org.springframework.data.redis.core.ReactiveRedisTemplate;
// import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
// import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
// import org.springframework.data.redis.serializer.RedisSerializationContext;
// import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {
    @Value("${atb.redis.port}")
    String redisPort;
    @Value("${atb.redis.host}")
    String atbRedisHost;

    @Value("${enxt.redis.host}")
    String enxtRedisHost;

    // @Bean
    // @Primary
    // public LettuceConnectionFactory atbRedisConnectionFactory() {
    //     RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration(atbRedisHost, Integer.parseInt(redisPort));
    //     return new LettuceConnectionFactory(redisStandaloneConfiguration);
    // }

    // @Bean
    // @Primary
    // ReactiveRedisTemplate<String, String> atbReactiveRedisTemplate(ReactiveRedisConnectionFactory atbRedisConnectionFactory) {
    //     return new ReactiveRedisTemplate<>(atbRedisConnectionFactory,
    //             RedisSerializationContext.<String, String>newSerializationContext(StringRedisSerializer.UTF_8).build());
    // }

    // @Bean
    // public LettuceConnectionFactory enxtRedisConnectionFactory() {
    //     RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration(enxtRedisHost, Integer.parseInt(redisPort));
    //     return new LettuceConnectionFactory(redisStandaloneConfiguration);
    // }

    // @Bean
    // ReactiveRedisTemplate<String, EnvoyDetailsData> enxtReactiveRedisTemplate(ReactiveRedisConnectionFactory enxtRedisConnectionFactory) {
    //     JdkSerializationRedisSerializer jdkSerializationRedisSerializer = new JdkSerializationRedisSerializer();

    //     Jackson2JsonRedisSerializer<EnvoyDetailsData> envoyResponseDTOJackson2JsonRedisSerializer =
    //             new Jackson2JsonRedisSerializer<>(EnvoyDetailsData.class);

    //     return new ReactiveRedisTemplate<>(enxtRedisConnectionFactory,
    //             RedisSerializationContext.<String, EnvoyDetailsData>newSerializationContext(jdkSerializationRedisSerializer)
    //                     .key(StringRedisSerializer.UTF_8).value(envoyResponseDTOJackson2JsonRedisSerializer).build());
    // }
}

