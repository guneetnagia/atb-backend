package com.guneet.atb.model.rds.repositories.writer;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

import com.guneet.atb.model.rds.LegacyATB;

import reactor.core.publisher.Mono;

public interface ATBWriteRepository extends R2dbcRepository<LegacyATB, Long> {
    @Query(value = "UPDATE atb t SET t.complete = :complete, t.status_code = :statusCode")
    Mono<LegacyATB> saveATB(Integer complete, Integer statusCode);
}