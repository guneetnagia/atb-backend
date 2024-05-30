package com.guneet.atb.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.guneet.atb.BaseTestApplicationSetup;

import static org.junit.jupiter.api.Assertions.*;

class HealthCheckControllerTest extends BaseTestApplicationSetup {
    @Autowired
    private WebTestClient webClient;
    @Test
    void healthCheckDefaultValues() {
        webClient.get()
                .uri("/healthcheck")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.OPS_TICKET").isEqualTo("NA")
                .jsonPath("$.DEPLOY_TIME").isEqualTo("NA")
                .jsonPath("$.STATUS").isEqualTo("UP")
                .jsonPath("$.VERSION").isEqualTo("NA")
        ;
    }
}
