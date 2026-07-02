package com.demo.configuration.roles;

import com.demo.configuration.SecurityConfiguration;
import io.micronaut.security.token.DefaultRolesFinder;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.demo.configuration.roles.Providers.KEYCLOAK;

/**
 * Keycloak puts client roles under resource_access.{client-id}.roles instead of
 * the top-level "roles" claim that {@link DefaultRolesFinder} looks for.
 */

@Slf4j
@Singleton
@AllArgsConstructor
@Named(KEYCLOAK)
public class KeycloakRolesFinder implements GetRoles {
    private static final String RESOURCE_ACCESS_CLAIM = "resource_access";
    private static final String ROLES_CLAIM = "roles";
    private final SecurityConfiguration securityConfiguration;

    @Override
    public List<String> resolveRoles(Map<String, Object> attributes) {
        if (attributes == null) {
            return Collections.emptyList();
        }

        String clientId = securityConfiguration.getClientId();
        Object resourceAccess = attributes.get(RESOURCE_ACCESS_CLAIM);
        if (!(resourceAccess instanceof Map<?, ?> resourceAccessMap)) {
            log.warn("Claim '{}' not found in token, no roles resolved", RESOURCE_ACCESS_CLAIM);
            return Collections.emptyList();
        }

        Object clientAccess = resourceAccessMap.get(clientId);
        if (!(clientAccess instanceof Map<?, ?> clientAccessMap)) {
            log.warn("No '{}' entry found for client '{}'", RESOURCE_ACCESS_CLAIM, clientId);
            return Collections.emptyList();
        }

        Object roles = clientAccessMap.get(ROLES_CLAIM);
        if (!(roles instanceof List<?> rolesList)) {
            return Collections.emptyList();
        }

        return rolesList.stream()
                .map(String::valueOf)
                .toList();
    }
}
