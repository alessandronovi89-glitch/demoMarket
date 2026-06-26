package com.demo.service;

import com.demo.configuration.SecurityConfiguration;
import com.demo.dto.AuthTokens;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.MediaType;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import jakarta.inject.Singleton;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletionStage;

@Slf4j
@Singleton
@AllArgsConstructor
public class AuthService {

    private final SecurityConfiguration securityConfig;

    @Client()
    private final HttpClient httpClient;

    public CompletionStage<AuthTokens> exchangeCodeForToken(String code) {
        String body = "grant_type=" + securityConfig.getGrantType()
                + "&code=" + code
                + "&client_id=" + securityConfig.getClientId()
                + "&client_secret=" + securityConfig.getClientSecret()
                + "&redirect_uri=" + securityConfig.getRedirectUri();

        log.info("Exchanging code for token at {}", securityConfig.getTokenUrl());

        return httpClient.toAsync().exchange(
                HttpRequest.POST(securityConfig.getTokenUrl(), body)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED_TYPE), AuthTokens.class)
                .thenApply(response -> {
                    System.out.println(response);
                    return response.getBody().get();
                });

    }
}
