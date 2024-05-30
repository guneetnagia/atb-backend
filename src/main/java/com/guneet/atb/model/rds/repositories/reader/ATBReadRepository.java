package com.guneet.atb.model.rds.repositories.reader;

import org.springframework.data.r2dbc.repository.R2dbcRepository;

import com.guneet.atb.model.rds.LegacyATB;

public interface ATBReadRepository extends R2dbcRepository<LegacyATB, Long> {
    
}
