package com.demo.controller;

import com.demo.dto.AuthTokens;
import com.demo.service.AuthService;
import io.micronaut.http.annotation.*;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.validation.Validated;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletionStage;

@Slf4j
@Controller
@AllArgsConstructor
@Validated
public class AuthCallbackController {

    private final AuthService authService;
    private static final String COOKIE_REFRESH_TOKEN = "refreshToken";


    @Secured(SecurityRule.IS_ANONYMOUS) //da vedere..
    @Get("/callback")
    public CompletionStage<AuthTokens> callback(@NotBlank @QueryValue String code) {
        log.info("Received auth code: {}", code);
        return authService.exchangeCodeForToken(code);
        //il refreshtoken andrebbe nel cookie
    }

    @Secured(SecurityRule.IS_ANONYMOUS) //da vedere..
    @Post("/refreshToken")
    public CompletionStage<AuthTokens> refreshToken(@NotBlank @CookieValue(COOKIE_REFRESH_TOKEN) String refreshToken) {
        return authService.refreshToken(refreshToken);
        // mettere il refresh token nel cookie
    }

    //logout?
}
