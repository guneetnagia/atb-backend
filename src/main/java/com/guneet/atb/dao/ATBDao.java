package com.guneet.atb.dao;

import com.guneet.atb.model.rds.LegacyATB;
import com.guneet.atb.model.rds.repositories.reader.ATBReadRepository;
import com.guneet.atb.model.rds.repositories.writer.ATBWriteRepository;
import com.guneet.atb.service.CrudService;
import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;
import org.springframework.data.relational.core.sql.SqlIdentifier;
import org.springframework.data.relational.core.query.Query;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import java.util.List;

public class ATBDao extends CrudService<LegacyATB>{

    final ATBWriteRepository atbWriteRepository;
    final ATBReadRepository atbReadRepository;
    @Value("${spring.profiles.active}")
    String env;

    public ATBDao(@Qualifier("readerR2dbcEntityTemplate") R2dbcEntityTemplate readerR2dbcEntityTemplate,
                    @Qualifier("writerR2dbcEntityTemplate") R2dbcEntityTemplate writerR2dbcEntityTemplate,
                    ATBWriteRepository atbWriteRepository,
                    ATBReadRepository atbReadRepository) {
        super(LegacyATB.class, readerR2dbcEntityTemplate, writerR2dbcEntityTemplate, "atb");
        this.atbWriteRepository = atbWriteRepository;
        this.atbReadRepository = atbReadRepository;
    }

    public Mono<LegacyATB> findById(Long taskId) {
        return atbReadRepository.findById(taskId);
    }

    public Flux<LegacyATB> findAllById(List<Long> taskIdList){
        return atbReadRepository.findAllById(taskIdList);
    }

    public Mono<LegacyATB> findByIdFromMaster(Long taskId) {
        return atbWriteRepository.findById(taskId);
    }

    public Mono<Long> cancelATB(Long taskId, Map<SqlIdentifier, Object> updates) {
        Query query = query(where("id").is(taskId).and("completed").isNull());
        return updateByQuery(query, updates);
    }

}
