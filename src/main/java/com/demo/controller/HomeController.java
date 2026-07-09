package com.demo.controller;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.rules.SecurityRule;

@Controller
public class HomeController {

    @Secured(SecurityRule.IS_ANONYMOUS)
    @Get("/")
    public HttpResponse<String> index(Authentication authentication) {
        return HttpResponse.ok("Login OK");
    }
}