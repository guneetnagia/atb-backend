package com.guneet.atb.config.security;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.guneet.atb.dtos.UserInfoDTO;

import org.springframework.http.server.reactive.ServerHttpRequest;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;


public class ReactiveRequestContextHolder {
    public static final Class<ServerHttpRequest> CONTEXT_KEY = ServerHttpRequest.class;

    private ReactiveRequestContextHolder(){}

    public static Mono<Map<String, Object>> getRequestHeaders() {
        return Mono.deferContextual(contextView -> Mono.just(contextView.get(CONTEXT_KEY)))
                .map(request ->
                     request.getHeaders().entrySet().stream()
                             .filter(h -> ("source-app".equalsIgnoreCase(h.getKey()) || "user-info".equalsIgnoreCase(h.getKey())) &&
                                     h.getValue() != null && !h.getValue().isEmpty())
                             .map(e -> {
                                 if("user-info".equalsIgnoreCase(e.getKey())) {
                                     UserInfoDTO userInfoDTO;
                                     try {
                                         userInfoDTO = new ObjectMapper().readValue(e.getValue().get(0), UserInfoDTO.class);
                                     } catch (IOException ignored) {
                                         userInfoDTO = new UserInfoDTO();
                                     }
                                     return Map.entry("user-info", userInfoDTO);
                                 }
                                 return Map.entry("source-app", e.getValue().get(0));
                             })
                             .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (x, y) -> y, HashMap::new)));
    }

    public static Mono<String> getSourceRequestHeader() {
        return getRequestHeaders().map(e -> (String) e.get("source-app"));
    }

    public static Mono<UserInfoDTO> getUserInfoRequestHeader() {
        return getRequestHeaders().map(e -> {
            if(e.containsKey("user-info")) {
                return (UserInfoDTO) e.get("user-info");
            }
            return new UserInfoDTO();
        });
    }
}

