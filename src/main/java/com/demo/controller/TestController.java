package com.demo.controller;

import com.demo.annotation.HasPermission;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.rules.SecurityRule;
import jakarta.annotation.security.RolesAllowed;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;

import static com.demo.models.Permission.READ_USER;

@Slf4j
@Controller("/api/test")
public class TestController {

    /** Endpoint pubblico — accessibile senza token. */
    @Get("/public")
    @Secured(SecurityRule.IS_ANONYMOUS)
    public HttpResponse<String> publicEndpoint() {
        return HttpResponse.ok("{\"message\": \"Public endpoint OK\", \"ts\": \"" + Instant.now() + "\"}");
    }

    /** Endpoint protetto — richiede un JWT valido. */
    @Get("/private")
    @Secured(SecurityRule.IS_AUTHENTICATED)
    public HttpResponse<String> privateEndpoint(Authentication authentication) {
        String subject = authentication.getName();
        return HttpResponse.ok("{\"message\": \"Private endpoint OK\", \"user\": \"" + subject + "\", \"ts\": \"" + Instant.now() + "\"}");
    }


    /** endpoint protetto, richiede ruolo admin. */
    @Get("/admin")
    @RolesAllowed("ADMIN")
    public HttpResponse<String> endpointAdmin(Authentication authentication) {
        String subject = authentication.getName();
        return HttpResponse.ok("{\"message\": \"Admin endpoint OK\", \"user\": \"" + subject + "\", \"ts\": \"" + Instant.now() + "\"}");
    }

    /** Endpoint protetto — richiede ruolo user */
    @Get("/user")
    @RolesAllowed("USER")
    public HttpResponse<String> endpointUser(Authentication authentication) {
        String subject = authentication.getName();
        return HttpResponse.ok("{\"message\": \"user endpoint OK\", \"user\": \"" + subject + "\", \"ts\": \"" + Instant.now() + "\"}");
    }

    /** Controlli sui permessi */
    @Get("/permission")
    @Secured(SecurityRule.IS_AUTHENTICATED)
    @HasPermission(READ_USER) //controlli sui permessi
    public HttpResponse<String> checkPermissions(Authentication authentication) {
        String subject = authentication.getName();
        return HttpResponse.ok("{\"message\": \"permission read_user is OK\", \"user\": \"" + subject + "\", \"ts\": \"" + Instant.now() + "\"}");
    }
}
