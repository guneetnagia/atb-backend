package com.guneet.atb.constants;

import java.util.List;
import java.util.Map;

public class ApplicationConstants {

    private ApplicationConstants(){}

    public static final String CREATED_AT = "created_at";
    public static final String IN = "IN";
    public static final String NIN = "NIN";
    public static final String LIKE = "LIKE";
    public static final String EQ = "EQ";
    public static final String NEQ = "NEQ";
    public static final String ID = "id";

    public static final String USER_INFO = "user-info";
    public static final String CLIENT_NAME = "source-app";

    
    public static final Map<String, Boolean> successData = Map.of("success", true);
    public static final List<String> validSortingFieldsTaskType = List.of( "name", CREATED_AT);
    public static final Map<String, String> OPERATOR_MAP = Map.of(
        EQ, "$eq",
        NEQ, "$ne",
        IN, "$in",
        NIN, "$nin",
        "GT", "$gt",
        "GTE", "$gte",
        "LT", "$lt",
        "LTE", "$lte",
        LIKE, "$like"
    );
    public static final List<String> validFiltersATBType = List.of(ID , "name");
}
