package com.demo.configuration.roles;

import io.micronaut.security.token.DefaultRolesFinder;
import io.micronaut.security.token.config.TokenConfiguration;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

import static com.demo.configuration.roles.Providers.KEYCLOAK;


@Slf4j
@Singleton
//@Replaces(DefaultRolesFinder.class) -> per adesso ignoriamo!
public class IdentityProviderRolesFinder extends DefaultRolesFinder {


    private final GetRoles getRoles;

    /**
     * Constructs a Roles Parser.
     *
     * @param tokenConfiguration General Token Configuration
     */
    public IdentityProviderRolesFinder(TokenConfiguration tokenConfiguration, @Named(KEYCLOAK) GetRoles getRoles) {
        super(tokenConfiguration);
        this.getRoles = getRoles;
    }


    @Override
    public List<String> resolveRoles(Map<String, Object> attributes) {
       return getRoles.resolveRoles(attributes);
    }
}
