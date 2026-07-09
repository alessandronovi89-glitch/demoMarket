package com.demo.controller;

import com.demo.service.IdentityProviderService;
import io.micronaut.http.annotation.Controller;
import io.micronaut.validation.Validated;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Exposes a subset of Keycloak's Admin REST API (users + realm role mapping).
 * Every call forwards the caller's own bearer token to Keycloak, so the authenticated
 * user/client must already hold the relevant realm-management permissions.
 */
@Slf4j
@Controller("/api/identity/")
@Validated
@AllArgsConstructor
public class IdentityProviderController {

    private static final String BEARER_PREFIX = "Bearer ";

    private final IdentityProviderService identityProviderService;
/*
    @Secured(SecurityRule.IS_AUTHENTICATED) //da correggere.. con is Admin
    @Get("/users") //it's working..
    public CompletionStage<List<User>> getUsers(@Header("Authorization") String authorization,
                                                 @Nullable @QueryValue String username,
                                                 @Nullable @QueryValue String email,
                                                 @Nullable @QueryValue String firstName,
                                                 @Nullable @QueryValue String lastName,
                                                 @Nullable @QueryValue String search,
                                                 @Nullable @QueryValue Integer first,
                                                 @Nullable @QueryValue Integer max) {
        UserSearchCriteria criteria = UserSearchCriteria.builder()
                .username(username)
                .email(email)
                .firstName(firstName)
                .lastName(lastName)
                .search(search)
                .first(first)
                .max(max)
                .build();
        return identityProviderService.getUsers(extractToken(authorization), criteria);
    }

    @Get("/users/{id}")
    public CompletionStage<User> getUser(@Header("Authorization") String authorization, @PathVariable String id) {
        return identityProviderService.getUser(extractToken(authorization), id)
                .thenApply(user -> user.orElseThrow(() ->
                        new HttpStatusException(HttpStatus.NOT_FOUND, "User not found: " + id)));
    }

    @Post("/users")
    public CompletionStage<HttpResponse<Void>> createUser(@Header("Authorization") String authorization,
                                                           @Body @Valid User user) {
        return identityProviderService.createUser(extractToken(authorization), user)
                .thenApply(id -> HttpResponse.created(URI.create("/api/keycloack/users/" + id)));
    }

    @Delete("/users/{id}")
    public CompletionStage<HttpResponse<Void>> deleteUser(@Header("Authorization") String authorization, @PathVariable String id) {
        return identityProviderService.deleteUser(extractToken(authorization), id)
                .thenApply(v -> HttpResponse.noContent());
    }

    @Post("/users/reset-password")
    public CompletionStage<HttpResponse<Void>> resetPassword(@Header("Authorization") String authorization,
                                                              @Body @Valid ResetPasswordRequest request) {
        return identityProviderService.resetPassword(extractToken(authorization), request)
                .thenApply(v -> HttpResponse.accepted());
    }

    @Post("/users/send-verify-email")
    public CompletionStage<HttpResponse<Void>> sendVerifyEmail(@Header("Authorization") String authorization,
                                                                @Body @Valid EmailRequest request) {
        return identityProviderService.sendVerifyEmail(extractToken(authorization), request)
                .thenApply(v -> HttpResponse.accepted());
    }

    @Post("/users/{id}/roles/{roleName}")
    public CompletionStage<HttpResponse<Void>> assignRealmRole(@Header("Authorization") String authorization,
                                                                @PathVariable String id,
                                                                @PathVariable String roleName) {
        return identityProviderService.assignRealmRole(extractToken(authorization), id, roleName)
                .thenApply(v -> HttpResponse.noContent());
    }

    private String extractToken(String authorizationHeader) {
        return authorizationHeader.startsWith(BEARER_PREFIX)
                ? authorizationHeader.substring(BEARER_PREFIX.length())
                : authorizationHeader;
    }*/
}
