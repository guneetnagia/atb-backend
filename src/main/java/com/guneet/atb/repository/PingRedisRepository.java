package com.guneet.atb.repository;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Repository
public class PingRedisRepository implements PingRepository{
    private static final String PING_PREFIX = "ping_";
    private final ReactiveRedisTemplate<String, String> reactiveRedisOperations;
    private final Duration expireIn = Duration.of(1, ChronoUnit.DAYS);

    public PingRedisRepository(@Qualifier("atbReactiveRedisTemplate")ReactiveRedisTemplate<String, String> reactiveRedisOperations) {
        this.reactiveRedisOperations = reactiveRedisOperations;
    }

    @Override
    public Mono<Void> create(String id) {
        return reactiveRedisOperations.opsForValue()
                .set(PING_PREFIX + id, id, expireIn)
                .then();
    }

    public Mono<String> get(String id){
        return reactiveRedisOperations.opsForValue().get(PING_PREFIX + id);
    }

    @Override
    public Mono<Void> delete(String id) {
        return reactiveRedisOperations.delete(PING_PREFIX + id).then();
    }
}
