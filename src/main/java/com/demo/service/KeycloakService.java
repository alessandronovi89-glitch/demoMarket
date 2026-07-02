package com.demo.service;

import com.demo.configuration.KeycloakEndpointConfiguration;
import com.demo.dto.keycloak.*;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import jakarta.inject.Singleton;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;

/**
 * Calls Keycloak's Admin REST API (https://www.keycloak.org/docs-api/latest/rest-api/index.html).
 * The bearer token used to authenticate against the admin API is supplied by the caller
 * (forwarded from the incoming request) for every call - this service does not fetch or cache one itself.
 */
@Slf4j
@Singleton
@AllArgsConstructor
public class KeycloakService implements IdentityProviderService {

    private final KeycloakEndpointConfiguration endpointConfiguration;

    @Client()
    private final HttpClient httpClient;

    @Override
    public CompletionStage<List<User>> getUsers(String token, UserSearchCriteria criteria) {
        String url = endpointConfiguration.getUsersUrl() + buildQueryString(criteria);
        log.info("Fetching users from {}", url);
        return httpClient.toAsync()
                .exchange(HttpRequest.GET(url).bearerAuth(token), Argument.listOf(User.class))
                .thenApply(response -> response.getBody().orElse(List.of()))
                .exceptionally(ex -> {
                    log.error("Error fetching users", ex);
                    throw new CompletionException(ex);
                });
    }

    @Override
    public CompletionStage<Optional<User>> getUser(String token, String id) {
        String url = endpointConfiguration.getUsersUrl() + "/" + id;
        log.info("Fetching user {} from {}", id, url);
        return httpClient.toAsync()
                .exchange(HttpRequest.GET(url).bearerAuth(token), User.class)
                .<Optional<User>>thenApply(HttpResponse::getBody)
                .exceptionally(ex -> {
                    if (isNotFound(ex)) {
                        return Optional.empty();
                    }
                    log.error("Error fetching user {}", id, ex);
                    throw new CompletionException(ex);
                });
    }

    @Override
    public CompletionStage<String> createUser(String token, User user) {
        log.info("Creating user {} at {}", user.getUsername(), endpointConfiguration.getUsersUrl());
        return httpClient.toAsync()
                .exchange(HttpRequest.POST(endpointConfiguration.getUsersUrl(), user).bearerAuth(token), Object.class)
                .thenApply(response -> extractIdFromLocation(response)
                        .orElseThrow(() -> new IllegalStateException("Keycloak did not return a Location header for the created user")))
                .exceptionally(ex -> {
                    log.error("Error creating user", ex);
                    throw new CompletionException(ex);
                });
    }

    @Override
    public CompletionStage<Void> deleteUser(String token, String id) {
        String url = endpointConfiguration.getUsersUrl() + "/" + id;
        log.info("Deleting user {} at {}", id, url);
        return httpClient.toAsync()
                .exchange(HttpRequest.DELETE(url).bearerAuth(token), Object.class)
                .thenApply(KeycloakService::voidify)
                .exceptionally(ex -> {
                    log.error("Error deleting user {}", id, ex);
                    throw new CompletionException(ex);
                });
    }

    @Override
    public CompletionStage<Void> resetPassword(String token, ResetPasswordRequest request) {
        return findUserIdByEmail(token, request.getEmail()).thenCompose(userId -> {
            String url = endpointConfiguration.getUsersUrl() + "/" + userId + "/execute-actions-email";
            log.info("Triggering password reset email for user {}", userId);
            return httpClient.toAsync()
                    .exchange(HttpRequest.PUT(url, List.of("UPDATE_PASSWORD")).bearerAuth(token), Object.class)
                    .thenApply(KeycloakService::voidify);
        }).exceptionally(ex -> {
            log.error("Error triggering password reset for {}", request.getEmail(), ex);
            throw new CompletionException(ex);
        });
    }

    @Override
    public CompletionStage<Void> sendVerifyEmail(String token, EmailRequest request) {
        return findUserIdByEmail(token, request.getEmail()).thenCompose(userId -> {
            String url = endpointConfiguration.getUsersUrl() + "/" + userId + "/send-verify-email";
            log.info("Triggering verification email for user {}", userId);
            return httpClient.toAsync()
                    .exchange(HttpRequest.PUT(url, "").bearerAuth(token), Object.class)
                    .thenApply(KeycloakService::voidify);
        }).exceptionally(ex -> {
            log.error("Error triggering verification email for {}", request.getEmail(), ex);
            throw new CompletionException(ex);
        });
    }

    @Override
    public CompletionStage<Void> assignRealmRole(String token, String userId, String roleName) {
        String roleUrl = endpointConfiguration.getRolesUrl() + "/" + roleName;
        return httpClient.toAsync()
                .exchange(HttpRequest.GET(roleUrl).bearerAuth(token), Role.class)
                .thenApply(response -> response.getBody()
                        .orElseThrow(() -> new IllegalArgumentException("Realm role not found: " + roleName)))
                .thenCompose(role -> {
                    String mappingUrl = endpointConfiguration.getUsersUrl() + "/" + userId + "/role-mappings/realm";
                    log.info("Assigning role {} to user {}", roleName, userId);
                    return httpClient.toAsync()
                            .exchange(HttpRequest.POST(mappingUrl, List.of(role)).bearerAuth(token), Object.class)
                            .thenApply(KeycloakService::voidify);
                }).exceptionally(ex -> {
                    log.error("Error assigning role {} to user {}", roleName, userId, ex);
                    throw new CompletionException(ex);
                });
    }

    private CompletionStage<String> findUserIdByEmail(String token, String email) {
        UserSearchCriteria criteria = UserSearchCriteria.builder().email(email).exact(true).build();
        String url = endpointConfiguration.getUsersUrl() + buildQueryString(criteria);
        return httpClient.toAsync()
                .exchange(HttpRequest.GET(url).bearerAuth(token), Argument.listOf(User.class))
                .thenApply(response -> response.getBody().orElse(List.of()).stream()
                        .findFirst()
                        .map(User::getId)
                        .orElseThrow(() -> new IllegalArgumentException("No Keycloak user found for email " + email)));
    }

    private Optional<String> extractIdFromLocation(HttpResponse<?> response) {
        return response.getHeaders().get("Location") == null
                ? Optional.empty()
                : Optional.of(response.getHeaders().get("Location"))
                        .map(location -> location.substring(location.lastIndexOf('/') + 1));
    }

    private boolean isNotFound(Throwable ex) {
        Throwable cause = ex instanceof CompletionException ? ex.getCause() : ex;
        return cause instanceof HttpClientResponseException httpEx && httpEx.getStatus() == HttpStatus.NOT_FOUND;
    }

    private static <T> Void voidify(HttpResponse<T> response) {
        return null;
    }

    private String buildQueryString(UserSearchCriteria criteria) {
        if (criteria == null) {
            return "";
        }
        StringBuilder query = new StringBuilder();
        appendParam(query, "username", criteria.getUsername());
        appendParam(query, "email", criteria.getEmail());
        appendParam(query, "firstName", criteria.getFirstName());
        appendParam(query, "lastName", criteria.getLastName());
        appendParam(query, "search", criteria.getSearch());
        appendParam(query, "enabled", criteria.getEnabled());
        appendParam(query, "exact", criteria.getExact());
        appendParam(query, "first", criteria.getFirst());
        appendParam(query, "max", criteria.getMax());
        appendParam(query, "briefRepresentation", criteria.getBriefRepresentation());
        return query.isEmpty() ? "" : "?" + query;
    }

    private void appendParam(StringBuilder query, String name, Object value) {
        if (value == null) {
            return;
        }
        if (!query.isEmpty()) {
            query.append('&');
        }
        query.append(name).append('=').append(URLEncoder.encode(String.valueOf(value), StandardCharsets.UTF_8));
    }
}
