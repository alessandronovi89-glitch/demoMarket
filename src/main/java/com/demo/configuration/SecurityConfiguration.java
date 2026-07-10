package com.demo.configuration;

import io.micronaut.context.annotation.Property;
import io.micronaut.context.event.ApplicationEventListener;
import io.micronaut.context.event.StartupEvent;
import io.micronaut.scheduling.annotation.Async;
import jakarta.inject.Singleton;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;


@Singleton
@Slf4j
@Getter
public class SecurityConfiguration implements ApplicationEventListener<StartupEvent> {

    @Property(name = "micronaut.security.enabled")
    private Boolean enabledSecurity;

    @Property(name = "micronaut.security.oauth2.clients.keycloak.client-id")
    private String clientId;

    @Property(name = "micronaut.security.oauth2.clients.keycloak.client-secret")
    private String clientSecret;

    @Property(name = "keycloak.host")
    private String host;

    @Property(name = "keycloak.port")
    private Integer port;

    @Property(name = "keycloak.realm")
    private String realm;

    @Property(name = "micronaut.security.oauth2.clients.keycloak.token.url")
    private String tokenUrl; //non necessario..

    @Property(name = "micronaut.security.oauth2.clients.keycloak.openid.issuer")
    private String issuerUrl; //necessario..

    @Property(name = "micronaut.security.oauth2.clients.keycloak.grant-type")
    private String grantType;

    @Property(name = "keycloak.redirect-uri")
    private String redirectUri;

    @Async
    public void onApplicationEvent(final StartupEvent event) {
        log.atInfo().log(" initial configuration: Security Configuration {}", this); //to be removed
    }

    @Override
    public String toString() {
        return "SecurityConfiguration{" +
                "enabledSecurity=" + enabledSecurity +
                ", host='" + host + '\'' +
                ", port=" + port +
                ", realm='" + realm + '\'' +
                ", clientId='" + clientId + '\'' +
                ", clientSecret='" + clientSecret + '\'' + //TO BE REMOVED..
                ", tokenUrl='" + tokenUrl + '\'' +
                ", grantType='" + grantType + '\'' +
                '}';
    }
}
