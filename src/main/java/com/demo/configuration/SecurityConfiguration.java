package com.demo.configuration;

import com.nimbusds.oauth2.sdk.GeneralException;
import com.nimbusds.oauth2.sdk.id.Issuer;
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;
import io.micronaut.context.annotation.Property;
import io.micronaut.context.event.ApplicationEventListener;
import io.micronaut.context.event.StartupEvent;
import io.micronaut.scheduling.annotation.Async;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Singleton;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;


@Singleton
@Slf4j
@Getter
public class SecurityConfiguration implements ApplicationEventListener<StartupEvent> {

    private OIDCProviderMetadata provider;
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

    @Property(name = "micronaut.security.oauth2.clients.keycloak.openid.issuer")
    private String issuerUrl;

    @Property(name = "micronaut.security.oauth2.clients.keycloak.grant-type")
    private String grantType;

    @Property(name = "keycloak.redirect-uri")
    private String redirectUri;

    @Property(name = "oauth.cookie-secure")
    private Boolean cookiesSecure; //da abilitare quando hai https

    @PostConstruct
    public void init() throws GeneralException, IOException {
        if(enabledSecurity) {
            provider = OIDCProviderMetadata.resolve(new Issuer(issuerUrl));
        }
    }

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
                ", grantType='" + grantType + '\'' +
                ", redirectUri='" + redirectUri + '\'' +
                ", cookiesSecure='" + cookiesSecure + '\'' +
                '}';
    }
}
