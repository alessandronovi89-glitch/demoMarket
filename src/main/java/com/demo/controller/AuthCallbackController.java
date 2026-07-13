package com.demo.controller;

import com.demo.configuration.SecurityConfiguration;
import com.demo.dto.AuthTokens;
import com.demo.service.AuthService;
import com.nimbusds.oauth2.sdk.id.State;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.*;
import io.micronaut.http.cookie.Cookie;
import io.micronaut.http.cookie.SameSite;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.validation.Validated;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;

@Slf4j
@Controller
@AllArgsConstructor
@Validated
public class AuthCallbackController {

    private final AuthService authService;
    private final SecurityConfiguration securityConfig;
    private static final String COOKIE_REFRESH_TOKEN = "refreshToken";
    private static final String COOKIE_STATE = "oauth_state";

    @Secured(SecurityRule.IS_ANONYMOUS)
    @Get("/login")
    public MutableHttpResponse<Object> login() {
        State state = new State();
        Cookie stateCookie =
                Cookie.of(COOKIE_STATE, state.getValue())
                        .httpOnly(true)
                        .secure(securityConfig.getCookiesSecure().equals("true"))
                        .sameSite(SameSite.Lax)
                        .maxAge(Duration.ofMinutes(5));
        return HttpResponse.redirect(authService.login(state)).cookie(stateCookie);

    }

    @Secured(SecurityRule.IS_ANONYMOUS)
    @Get("/callback")
    public HttpResponse<AuthTokens> callback(@NotBlank @QueryValue String code, @NotBlank @QueryValue String state,
                               @NotBlank @CookieValue(COOKIE_STATE) String stateCookie) {
        log.debug("callback called");
        AuthTokens tokens = authService.exchangeCodeForToken(code, state, stateCookie);
        Cookie refreshTokenCookie =
                Cookie.of(COOKIE_REFRESH_TOKEN, tokens.getRefreshToken())
                        .httpOnly(true)
                        .secure(securityConfig.getCookiesSecure().equals("true"))
                        .sameSite(SameSite.Lax)
                        .maxAge(Duration.ofDays(30));
        tokens.setRefreshToken(null);
        return HttpResponse.ok(tokens).cookie(refreshTokenCookie);
    }

    @Secured(SecurityRule.IS_ANONYMOUS)
    @Post("/refreshToken")
    public HttpResponse<AuthTokens> refreshToken(@NotBlank @CookieValue(COOKIE_REFRESH_TOKEN) String refreshToken) {
        AuthTokens tokens = authService.refreshToken(refreshToken);
        Cookie refreshTokenCookie =
                Cookie.of(COOKIE_REFRESH_TOKEN, tokens.getRefreshToken())
                        .httpOnly(true)
                        .secure(securityConfig.getCookiesSecure().equals("true"))
                        .sameSite(SameSite.Lax)
                        .maxAge(Duration.ofDays(30));
        tokens.setRefreshToken(null);
        return HttpResponse.ok(tokens).cookie(refreshTokenCookie);
    }

    //logout?
}
