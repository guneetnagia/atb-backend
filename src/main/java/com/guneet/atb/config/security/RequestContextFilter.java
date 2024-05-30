package com.guneet.atb.config.security;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;


@Component
@Slf4j
public class RequestContextFilter implements WebFilter {
    private static final Pattern COMPLETE_API_PATH = Pattern.compile("\\/api\\/v1\\/tasks\\/[0-9]{1,18}\\/complete$");
    private static final Set<String> BULK_API_PATH = Set.of("/api/v1/tasks/bulk-status", "/api/v1/tasks/bulk-create");
    private static final String REPORTS = "reports";
    private static final Set<String> BULK_API_CLIENT = Set.of("fud", "dbu");

    @Value("${service-name}")
    private Set<String> services;

    private static final Set<String> excludeUrlPatterns = new HashSet<>(Arrays.asList(
            "/",
            "/swagger-ui.html",
            "/webjars/swagger-ui.html",
            "/webjars/swagger-ui/index.html",
            "/webjars/swagger-ui/swagger-ui.css",
            "/webjars/swagger-ui/index.css",
            "/webjars/swagger-ui/swagger-initializer.js",
            "/webjars/swagger-ui/swagger-ui-bundle.js",
            "/webjars/swagger-ui/swagger-ui-standalone-preset.js",
            "/v3/api-docs",
            "/v3/api-docs/swagger-config",
            "/v3/api-docs/swagger-config.json",
            "/v3/api-docs/source-info-header",
            "/swagger-docs/webjars/swagger-ui.html",
            "/swagger-docs/webjars/swagger-ui/index.html",
            "/swagger-docs/webjars/swagger-ui/index.html",
            "/swagger-docs/webjars/swagger-ui/swagger-ui.css",
            "/swagger-docs/webjars/swagger-ui/index.css",
            "/swagger-docs/webjars/swagger-ui/swagger-initializer.js",
            "/swagger-docs/webjars/swagger-ui/swagger-ui-bundle.js",
            "/swagger-docs/webjars/swagger-ui/swagger-ui-standalone-preset.js",
            "/service/tasks/webjars/swagger-ui/index.html",
            "/service/tasks/webjars/swagger-ui/swagger-ui.css",
            "/service/tasks/webjars/swagger-ui/index.css",
            "/service/tasks/webjars/swagger-ui/swagger-initializer.js",
            "/service/tasks/webjars/swagger-ui/swagger-ui-bundle.js",
            "/service/tasks/webjars/swagger-ui/swagger-ui-standalone-preset.js",
            "/service/tasks/v3/api-docs",
            "/service/tasks/v3/api-docs/swagger-config.json",
            "/service/tasks/swagger-config.json",
            "/swagger-config.json",
            "/service/tasks/v3/api-docs/swagger-config",
            "/service/tasks/v3/api-docs/source-info-header",
            "/healthcheck",
            "/actuator/health"));

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String reqPath = request.getPath().value();

        if(!excludeUrlPatterns.contains(reqPath)) {
            boolean invalidRequest = false;
            HttpHeaders headers = request.getHeaders();
            String clientName = headers.getFirst("source-app");

            if(StringUtils.hasLength(clientName) && services.contains(clientName.toLowerCase())) {
                //complete allowed for reports only
                boolean isTaskCompleteRequest = COMPLETE_API_PATH.matcher(reqPath).find();
                if(isTaskCompleteRequest && !(REPORTS.equalsIgnoreCase(clientName))) {
                    invalidRequest = true;
                }
                boolean isBulkApiRequest = (BULK_API_PATH.contains(reqPath));
                if(isBulkApiRequest &&  !(BULK_API_CLIENT.contains(clientName.toLowerCase()))) {
                    invalidRequest = true;
                }
            } else {
                invalidRequest = true;
            }
            if(invalidRequest) {
                log.warn("Invalid request, request-source: {}, path: {}", clientName, reqPath);
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Request not allowed for user = " + clientName);
            }
            logRequestDetails(request, clientName); //enable only for debug mode
        }
        var responseHeaders = exchange.getResponse().getHeaders();
        if (responseHeaders.getCacheControl() == null) {
            responseHeaders.setCacheControl(CacheControl.noCache());
        }
        responseHeaders.add("X-Frame-Options", "same-origin");
        responseHeaders.add("X-Content-Security-Policy", "script-src 'self'");
        responseHeaders.add("X-Content-Type-Options", "nosniff");

        return chain.filter(exchange).contextWrite(context -> context.put(ReactiveRequestContextHolder.CONTEXT_KEY, request));
    }

    private void logRequestDetails(ServerHttpRequest request, String clientName) {
        log.info("Request: {} {} | request-source: {}", request.getMethod(), request.getPath(), clientName);
    }
}

