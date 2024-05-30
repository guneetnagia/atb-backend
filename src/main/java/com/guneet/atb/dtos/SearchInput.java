package com.guneet.atb.dtos;

import com.enphase.cloud.enlighten.task.reactive.dtos.request.TaskTypeDynamicFilter;
import com.guneet.atb.dtos.request.ATBDynamicFilter;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Map;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SearchInput {
    Map<String, Object> criteria;
    Integer limit;
    Integer offset;
    Sort sort;
    List<String> select;

    public SearchInput(Map<String, Object> criteria){
        this.criteria = criteria;
    }

    public SearchInput(ATBDynamicFilter taskTypeDynamicFilter){
        this.criteria = taskTypeDynamicFilter.parseFilter();
        this.offset = taskTypeDynamicFilter.getOffset();
        this.limit = taskTypeDynamicFilter.getLimit();
        this.sort = taskTypeDynamicFilter.parseSortBy();
    }
}
