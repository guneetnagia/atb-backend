package com.guneet.atb.repository;


import com.redis.testcontainers.RedisContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;



@Testcontainers
@SpringBootTest("spring.profiles.active=test")
@AutoConfigureWebTestClient
class PingRedisRepositoryTest {

    @Autowired
    private ReactiveRedisTemplate<String, String> enlightenReactiveRedisTemplate;

    @Container
    private static final RedisContainer REDIS_CONTAINER =
            new RedisContainer(DockerImageName.parse("redis:5.0.3-alpine")).withExposedPorts(6379);

    @DynamicPropertySource
    private static void registerRedisProperties(DynamicPropertyRegistry registry) {
        registry.add("atb.redis.host", REDIS_CONTAINER::getHost);
        registry.add("atb.redis.port", () -> REDIS_CONTAINER.getMappedPort(6379).toString());
    }
    private PingRedisRepository pingRedisRepository;

    @BeforeEach
    void setUp() {
        pingRedisRepository = new PingRedisRepository(enlightenReactiveRedisTemplate);
    }

    @Test
    void create() {
        Mono<Void> createMono = pingRedisRepository.create("id");
        StepVerifier.create(createMono).verifyComplete();
        Mono<String> stringMono = pingRedisRepository.get("id");
        StepVerifier.create(stringMono)
                .expectNext("id")
                .verifyComplete();
    }

    @Test
    void delete() {
        Mono<Void> createMono = pingRedisRepository.create("id");
        StepVerifier.create(createMono).verifyComplete();

        Mono<Void> deleteMono = pingRedisRepository.delete("id");
        StepVerifier.create(deleteMono).verifyComplete();

        Mono<String> stringMono = pingRedisRepository.get("id");
        StepVerifier.create(stringMono).verifyComplete();

    }
}