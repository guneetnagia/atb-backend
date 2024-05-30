package com.guneet.atb;


import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import com.guneet.atb.config.security.ReactiveRequestContextHolder;
import com.guneet.atb.dtos.UserInfoDTO;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Map;

import static com.guneet.atb.constants.ApplicationConstants.CLIENT_NAME;
import static com.guneet.atb.constants.ApplicationConstants.USER_INFO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ReactiveRequestContextHolderTest extends BaseTestApplicationSetup {

    @Autowired
    WebTestClient webTestClient;

    ServerHttpRequest request = Mockito.mock(ServerHttpRequest.class);


    @Test
    void getRequestHeaders() throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        ObjectMapper objectMapper = new ObjectMapper();
        headers.add(CLIENT_NAME, "admin");
        UserInfoDTO user = new UserInfoDTO(1L, "some@email.com","first", "last", "en", "US");
        headers.add(USER_INFO, objectMapper.writeValueAsString(user));
        Mockito.when(request.getHeaders()).thenReturn(headers);
        Mono<Map<String, Object>> mapMono = ReactiveRequestContextHolder.getRequestHeaders()
                .contextWrite(context -> context.put(ReactiveRequestContextHolder.CONTEXT_KEY, request));
        StepVerifier.create(mapMono)
                .assertNext(map -> {
                    assertEquals("admin", map.get(CLIENT_NAME));
                    UserInfoDTO actual = (UserInfoDTO) map.get(USER_INFO);
                    assertEquals(user.getId(), actual.getId());
                    assertEquals(user.getEmail(), actual.getEmail());
                    assertEquals(user.getFirstName(), actual.getFirstName());
                    assertEquals(user.getLastName(), actual.getLastName());
                    assertEquals(user.getCountry(), actual.getCountry());
                    assertEquals(user.getLocale(), actual.getLocale());
                })
                .verifyComplete();
    }

    @Test
    void getSourceRequestHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(CLIENT_NAME, "admin");
        Mockito.when(request.getHeaders()).thenReturn(headers);
        Mono<String> mapMono = ReactiveRequestContextHolder.getSourceRequestHeader()
                .contextWrite(context -> context.put(ReactiveRequestContextHolder.CONTEXT_KEY, request));
        StepVerifier.create(mapMono)
                .assertNext(source -> {
                    assertEquals("admin", source);
                })
                .verifyComplete();
    }

    @Test
    void getUserInfoRequestHeader() throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        ObjectMapper objectMapper = new ObjectMapper();
        UserInfoDTO user = new UserInfoDTO(1L, "some@email.com","first", "last", "en", "US");
        headers.add(USER_INFO, objectMapper.writeValueAsString(user));
        Mockito.when(request.getHeaders()).thenReturn(headers);
        Mono<UserInfoDTO> mapMono = ReactiveRequestContextHolder.getUserInfoRequestHeader()
                .contextWrite(context -> context.put(ReactiveRequestContextHolder.CONTEXT_KEY, request));
        StepVerifier.create(mapMono)
                .assertNext(actual -> {
                    assertEquals(user.getId(), actual.getId());
                    assertEquals(user.getEmail(), actual.getEmail());
                    assertEquals(user.getFirstName(), actual.getFirstName());
                    assertEquals(user.getLastName(), actual.getLastName());
                    assertEquals(user.getCountry(), actual.getCountry());
                    assertEquals(user.getLocale(), actual.getLocale());
                })
                .verifyComplete();
    }
    @Test
    void testGetEmptyUserInfo(){
        HttpHeaders headers = new HttpHeaders();
        Mockito.when(request.getHeaders()).thenReturn(headers);
        Mono<UserInfoDTO> mapMono = ReactiveRequestContextHolder.getUserInfoRequestHeader()
                .contextWrite(context -> context.put(ReactiveRequestContextHolder.CONTEXT_KEY, request));
        StepVerifier.create(mapMono)
                .assertNext(actual -> {
                    assertNull(actual.getId());
                    assertNull(actual.getEmail());
                    assertNull(actual.getFirstName());
                    assertNull(actual.getLastName());
                    assertNull(actual.getCountry());
                    assertNull(actual.getLocale());
                })
                .verifyComplete();
    }
    @Test
    void shouldGetEmptyUserInfoForWrongJson(){
        HttpHeaders headers = new HttpHeaders();
        headers.add(USER_INFO, "{wrong:json}");
        Mockito.when(request.getHeaders()).thenReturn(headers);
        Mono<Map<String, Object>> mapMono = ReactiveRequestContextHolder.getRequestHeaders()
                .contextWrite(context -> context.put(ReactiveRequestContextHolder.CONTEXT_KEY, request));
        StepVerifier.create(mapMono)
                .assertNext(map -> {
                    UserInfoDTO actual = (UserInfoDTO) map.get(USER_INFO);
                    assertNull(actual.getId());
                    assertNull(actual.getEmail());
                    assertNull(actual.getFirstName());
                    assertNull(actual.getLastName());
                    assertNull(actual.getCountry());
                    assertNull(actual.getLocale());
                })
                .verifyComplete();
    }
}
