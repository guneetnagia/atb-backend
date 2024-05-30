package com.guneet.atb.repository;

import reactor.core.publisher.Mono;

public interface PingRepository {
    Mono<Void> create(String serialNumber);
    Mono<Void> delete(String serialNumber);
}
