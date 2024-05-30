package com.guneet.atb.dtos.request;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.guneet.atb.exception.ATBServiceException;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Sort;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.guneet.atb.constants.ApplicationConstants.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ATBDynamicFilter {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(example = "[\"id:IN:4,5\"]")
    private List<String> filters;

    @Schema(example = "0")
    @Min(value = 0, message = "Offset should be non negative")
    private Integer offset = 0;

    @Schema(example = "10")
    @Min(value = 1, message = "Limit should be greater than 0")
    @Max(value = 100, message = "Limit should be less than or equals to 100")
    private Integer limit = 10;

    @Schema(example = "-created_at")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String sortBy;

    public HashMap<String, Object> parseFilter(){
        //If filter object is not passed
        if(this.filters == null) return null;

        //If filter object is passed
        HashMap<String, Object> criteriaList = new HashMap<>();
        for(String filter: this.filters){
            String[] arr = (filter.split(":"));

            if(arr.length != 3 || !validFiltersATBType.contains(arr[0]))
                throw new ATBServiceException(400, "INVALID_FILTER", "Bad Request format");


            populateATBTypeCriteriaList(criteriaList, arr);
        }

        return criteriaList;
    }

    public Sort parseSortBy(){

        //If sorting param is not passed
        if(this.sortBy == null) return null;

        //Calculating the sorting order and column if sort param is passed
        char dir = this.sortBy.charAt(0);
        Sort.Direction directionOfSort = Sort.Direction.ASC;

        if(dir != '-' && dir != '+')
            throw new ATBServiceException(400, "INVALID_SORTING_FORMAT", "Invalid sorting direction");

        if(!validSortingFieldsTaskType.contains(this.sortBy.substring(1)))
            throw new ATBServiceException(400, "INVALID_SORTING_FIELD", "Invalid sorting column");

        if(dir == '-')
            directionOfSort = Sort.Direction.DESC;

        String sortingColumn = this.sortBy.substring(1);

        return Sort.by(directionOfSort, sortingColumn);
    }

}

