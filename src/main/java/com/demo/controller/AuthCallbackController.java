package com.demo.controller;

import com.demo.dto.AuthTokens;
import com.demo.service.AuthService;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletionStage;

@Slf4j
@Controller
@AllArgsConstructor
public class AuthCallbackController {

    private final AuthService authService;


    @Secured(SecurityRule.IS_ANONYMOUS) //da vedere..
    @Get("/callback")
    public CompletionStage<AuthTokens> callback(@QueryValue String code) {
        log.info("Received auth code: {}", code);
        return authService.exchangeCodeForToken(code);
    }
}
