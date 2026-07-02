package com.demo.configuration;

import io.micronaut.context.annotation.Property;
import io.micronaut.context.event.ApplicationEventListener;
import io.micronaut.context.event.StartupEvent;
import io.micronaut.scheduling.annotation.Async;
import jakarta.inject.Singleton;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * Resolved URLs for Keycloak's Admin REST API.
 * See https://www.keycloak.org/docs-api/latest/rest-api/index.html
 */
@Singleton
@Slf4j
@Getter
public class KeycloakEndpointConfiguration implements ApplicationEventListener<StartupEvent> {

    @Property(name = "keycloak.realm")
    private String realm;

    @Property(name = "keycloak.endpoints.users")
    private String usersUrl;

    @Property(name = "keycloak.endpoints.roles")
    private String rolesUrl;

    @Property(name = "micronaut.security.oauth2.clients.keycloak.client-id")
    private String clientId;

    @Property(name = "micronaut.security.oauth2.clients.keycloak.client-secret")
    private String clientSecret;

    @Async
    public void onApplicationEvent(final StartupEvent event) {
        log.atInfo().log("initial configuration: Keycloak Endpoint Configuration {}", this); //to be removed
    }

    @Override
    public String toString() {
        return "KeycloakEndpointConfiguration{" +
                "realm='" + realm + '\'' +
                ", usersUrl='" + usersUrl + '\'' +
                ", rolesUrl='" + rolesUrl + '\'' +
                ", clientId='" + clientId + '\'' +
                '}';
    }
}
