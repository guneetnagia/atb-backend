package com.guneet.atb.service;


import com.guneet.atb.dtos.SearchInput;
import com.guneet.atb.util.SqlQueryUtil;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Query;
import org.springframework.data.relational.core.query.Update;
import org.springframework.data.relational.core.sql.SqlIdentifier;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.empty;
import static org.springframework.data.relational.core.query.Query.query;

@AllArgsConstructor
@Slf4j
public class CrudService<T extends Serializable> {

    protected Class<T> klass;
    protected R2dbcEntityTemplate readerR2dbcEntityTemplate;
    protected R2dbcEntityTemplate writerR2dbcEntityTemplate;
    protected String tableName;

    public Mono<T> create(T object)  {
        return writerR2dbcEntityTemplate.insert(klass)
                .into(tableName)
                .using(object)
                .onErrorResume(Exception.class, ex -> Mono.error(new RuntimeException(ex.getMessage())));
    }

    public Mono<T> findById(Long id) {
        return readerR2dbcEntityTemplate.select(klass)
                .from(tableName)
                .matching(query(where("id").is(id)))
                .first();
    }

    public Mono<Long> findCountByDeviceIds(List<Long> deviceIds) {
        return readerR2dbcEntityTemplate.select(klass)
                .from(tableName)
                .matching(query(where("device_id").in(deviceIds)))
                .count();
    }

    public Mono<Long> updateById(Long id, Map<SqlIdentifier, Object> update) {
        return writerR2dbcEntityTemplate.update(klass)
                .inTable(tableName)
                .matching(query(where("id").is(id)))
                .apply(Update.from(update));
    }

    public Mono<Long> updateByQuery(Query query, Map<SqlIdentifier, Object> update) {
        return writerR2dbcEntityTemplate.update(klass)
                .inTable(tableName)
                .matching(query)
                .apply(Update.from(update));
    }

    public Flux<T> findBy(SearchInput input) {
        return findBy(input, true);
    }
    public Flux<T> findBy(SearchInput input, boolean readFromSlave) {
        Query query = empty();

        if(input.getCriteria()!=null) query = SqlQueryUtil.fromCriteriaList(input.getCriteria());
        if(input.getLimit()!=null) query = query.limit(input.getLimit());
        if(input.getOffset()!=null) query = query.offset(input.getOffset());

        if(input.getSort() !=null && input.getSort().isSorted()){
            query = query.sort(input.getSort());
        }

        if(input.getSelect() != null) query = query.columns(input.getSelect());
        R2dbcEntityTemplate dbEntityTemplate = readFromSlave ? readerR2dbcEntityTemplate : writerR2dbcEntityTemplate;
        return dbEntityTemplate
                .select(klass)
                .from(tableName)
                .matching(query)
                .all();
    }

    public Mono<Long> findCountBy(SearchInput input) {
        Query query = empty();

        if(input.getCriteria()!=null) query = SqlQueryUtil.fromCriteriaList(input.getCriteria());
        return readerR2dbcEntityTemplate
                .select(klass)
                .from(tableName)
                .matching(query)
                .count();
    }

    public Mono<Boolean> exists(SearchInput input) {
        Query query = empty();

        if(input.getCriteria()!=null) query = SqlQueryUtil.fromCriteriaList(input.getCriteria());
        return readerR2dbcEntityTemplate
                .select(klass)
                .from(tableName)
                .matching(query)
                .exists();
    }

    public Mono<Long> countBy(SearchInput input) throws Exception {
        Query query = SqlQueryUtil.fromCriteriaList(input.getCriteria());
        return readerR2dbcEntityTemplate
                .select(klass)
                .from(tableName)
                .matching(query)
                .count();
    }

    public Mono<T> findByName(String name){
        return readerR2dbcEntityTemplate.select(klass)
            .from(tableName)
            .matching(query(where("name").is(name)))
            .first();
    }

    public Mono<Long> countAll(){
        return readerR2dbcEntityTemplate
            .select(klass)
            .from(tableName)
            .count();
    }

    public Flux<T> findAll(){
        return readerR2dbcEntityTemplate
            .select(klass)
            .from(tableName)
            .all();
    }

    // for update add @transactional
}

