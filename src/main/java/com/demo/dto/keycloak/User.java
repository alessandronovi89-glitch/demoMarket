package com.demo.dto.keycloak;

import io.micronaut.serde.annotation.Serdeable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * Subset of Keycloak's UserRepresentation, used both for reading users back
 * from the admin REST API and for building the payload of a create request.
 */
@Serdeable
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private String id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private boolean enabled;
    private boolean emailVerified;
    private Long createdTimestamp;
    private Map<String, List<String>> attributes;
    private List<String> requiredActions;
    private List<Credential> credentials;
}
