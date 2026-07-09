package com.demo.configuration;

import io.micronaut.context.annotation.Replaces;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.authentication.AuthenticationResponse;
import io.micronaut.security.handlers.LoginHandler;
import io.micronaut.security.token.cookie.CookieLoginHandler;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;

@Slf4j
@Singleton
@Replaces(CookieLoginHandler.class)
public class KeycloakLoginHandler implements LoginHandler<HttpRequest<?>, HttpResponse<?>> {


    @Override
    public MutableHttpResponse<?> loginSuccess(Authentication authentication, HttpRequest<?> request) {
        log.info("OIDC login successful: user={}, roles={}",
                authentication.getName(),
                authentication.getRoles());


        return HttpResponse.redirect(URI.create("/"));
    }

    @Override
    public MutableHttpResponse<?> loginRefresh(Authentication authentication, String refreshToken, HttpRequest<?> request) {
        return null;
    }

    @Override
    public MutableHttpResponse<?> loginFailed(AuthenticationResponse authenticationResponse, HttpRequest<?> request) {
        return null;
    }
}
