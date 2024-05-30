package com.guneet.atb.util;


import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Criteria.CriteriaStep;

import com.guneet.atb.exception.ATBServiceException;

import org.springframework.data.relational.core.query.Query;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.data.relational.core.query.Criteria.empty;
import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;


public class SqlQueryUtil {

    private SqlQueryUtil() {}

    public static Query fromCriteriaList(Map<String, Object> criteriaList) {
        Criteria criteria = empty();
        CriteriaStep step;

        for (Map.Entry<String, Object> entry: criteriaList.entrySet()) {
            step = where(entry.getKey());
            Object value = entry.getValue();
            if(value instanceof LinkedHashMap) {
                for(Map.Entry<String, Object> operations: ((LinkedHashMap<String, Object>) value).entrySet()) {
                    criteria = criteria.and(getCriteria(step, operations.getKey(), operations.getValue()));
                }
            } else {
                criteria = criteria.and(getCriteria(step, "$eq", value));
            }
        }
        return query(criteria);
    }

    private static Criteria getCriteria(CriteriaStep step, String operator, Object value) {
        switch(operator) {
            case "$eq" :
                if(value == null) return step.isNull();
                return step.is(value);
            case "$lt" :
                return step.lessThan(value);
            case "$lte" :
                return step.lessThanOrEquals(value);
            case "$gt" :
                return step.greaterThan(value);
            case "$gte" :
                return step.greaterThanOrEquals(value);
            case "$in" :
                List<?> inList = ((ArrayList<?>) value).stream().collect(Collectors.toList());
                return step.in(inList);
            case "$nin":
                List<?> notInList = ((ArrayList<?>) value).stream().collect(Collectors.toList());
                return step.notIn(notInList);
            case "$like" :
                return step.like(value);
            case "$ne":
                if(value == null) return step.isNotNull();
                return step.not(value);
            default:
                throw new ATBServiceException(500, "INVALID_FILTER_OPERATOR", "Operator " + operator + " is not supported");
        }
    }

    public static StringBuilder sqlQueryFromCriteria(Map<String, Object> criteriaMap) {
        StringBuilder sqlBuilder = new StringBuilder();
        List<String> conditions = new ArrayList<>();
        for (Map.Entry<String, Object> entry : criteriaMap.entrySet()) {
            String columnName = entry.getKey();
            Object value = entry.getValue();
            conditions.add(generateCondition(columnName, value));
        }
        sqlBuilder.append(String.join(" AND ", conditions));

        return (sqlBuilder);
    }

    private static String generateCondition(String columnName, Object value) {
        if (value == null){
            return columnName + " IS NULL ";
        }
        if (value instanceof LinkedHashMap) {
            LinkedHashMap<String, Object> comparatorMap = (LinkedHashMap<String, Object>) value;
            Map.Entry<String, Object> comparator = comparatorMap.entrySet().iterator().next();
            Object comparatorValue = comparator.getValue();

            switch (comparator.getKey()) {
                case "$eq":
                    if(comparatorValue==null)
                        return columnName + " IS NULL ";
                    return columnName + " = " + formatValue(comparatorValue, columnName);
                case "$ne":
                    if(comparatorValue==null)
                        return columnName + " IS NOT NULL ";
                    return columnName + " <> " + formatValue(comparatorValue, columnName);
                case "$gt":
                    return columnName + " > " + formatValue(comparatorValue, columnName);
                case "$gte":
                    return columnName + " >= " + formatValue(comparatorValue, columnName);
                case "$lt":
                    return columnName + " < " + formatValue(comparatorValue, columnName);
                case "$lte":
                    return columnName + " <= " + formatValue(comparatorValue, columnName);
                case "$in":
                    return columnName + " IN (" + formatValues((List<Object>) comparatorValue, columnName) + ")";
                case "$nin":
                    return columnName + " NOT IN (" + formatValues((List<Object>) comparatorValue, columnName) + ")";
                case "$like":
                    return columnName + " LIKE " + formatValue(comparatorValue, columnName);
                default:
                    throw new ATBServiceException(500, "INVALID_FILTER_OPERATOR", "Operator " + comparator + " is not supported");
            }
        } else {
            return columnName + " = " + value;
        }
    }

    private static String formatValue(Object value, String columnName) {
        if(!"abc".contains(columnName.toLowerCase()))
            return "'"+ value.toString() + "'";
        return value.toString();
    }

    private static String formatValues(List<Object> values, String columnName) {
        List<String> formattedValues = new ArrayList<>();
        for (Object value : values) {
            formattedValues.add(formatValue(value, columnName));
        }
        return String.join(", ", formattedValues);
    }
}