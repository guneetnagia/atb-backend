package com.guneet.atb.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import lombok.extern.slf4j.Slf4j;
import static com.guneet.atb.constants.ApplicationConstants.*;

@Slf4j
public class CriteriaGeneratorUtil {

    public static void populateATBTypeCriteriaList(HashMap<String, Object> criteriaList, String[] arr){
        arr[1] = arr[1].toUpperCase();

        if(Objects.equals(arr[1], IN) || Objects.equals(arr[1], NIN)){
            //The third element will be a comma separated string which is an array
            List<String> arrList = new ArrayList<>(Arrays.asList(arr[2].split(",")));
            criteriaList.put(arr[0], new LinkedHashMap<>(Map.of(OPERATOR_MAP.get(arr[1]), arrList)));
        } else if (Objects.equals(arr[1], LIKE)) {
            criteriaList.put(arr[0], new LinkedHashMap<>(Map.of(OPERATOR_MAP.get(LIKE), "%" + arr[2] + "%")));
        } else {
            //The third element will always be a single element, and never an array
            criteriaList.put(arr[0], new LinkedHashMap<>(Map.of(OPERATOR_MAP.get(arr[1]), arr[2])));
        }
    }
    
}
