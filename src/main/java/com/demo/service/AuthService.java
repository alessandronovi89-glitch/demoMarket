package com.demo.service;

import com.demo.configuration.SecurityConfiguration;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import jakarta.inject.Singleton;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Singleton
@AllArgsConstructor
public class AuthService {

    private final SecurityConfiguration securityConfig;
    //private final DefaultOauthController defaultOauthController;

    @Client()
    private final HttpClient httpClient;
/*
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
                    .thenApply(result-> {
                        log.debug("get token: " + result);
                        return result.getBody().orElse(null);
                    })
                    .exceptionally((ex)->{
                        log.error("Error get token", ex);
                        throw new CompletionException(ex);
                    });
    }

    public CompletionStage<AuthTokens> refreshToken(String refreshToken) {
        String body = "grant_type=refresh_token"
                + "&refresh_token=" + refreshToken
                + "&client_id=" + securityConfig.getClientId()
                + "&client_secret=" + securityConfig.getClientSecret();

        log.info("refreshing token at {}", securityConfig.getTokenUrl());
        return httpClient.toAsync().exchange(
                        HttpRequest.POST(securityConfig.getTokenUrl(), body)
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED_TYPE), AuthTokens.class)
                .thenApply(result-> {
                    log.debug("response refresh: " + result);
                    return result.getBody().orElse(null);
                })
                .exceptionally((ex)->{
                    log.error("Error refreshing token", ex);
                    throw new CompletionException(ex);
                });
    }*/
/*
    public CompletionStage<AuthTokens> login() {
//localhost:8080/realms/ai360-demo/protocol/openid-connect/auth?response_type=code&client_id=demoClientId&redirect_uri=http://localhost:8081/callback&scope=openid&state=123
        //"code", securityConfig.getClientId(), securityConfig.getRedirectUri(), "openid", "123"
        HttpRequest request = HttpRequest.GET(securityConfig.getAuthUrl() + "?response_type=code&client_id=" + securityConfig.getClientId() + "&redirect_uri=" + securityConfig.getRedirectUri() + "&scope=openid&state=123");
        return defaultOauthController.login();

       return HttpResponse.redirect(
                URI.create("/oauth/login/keycloak")
        );
    }*/
}
