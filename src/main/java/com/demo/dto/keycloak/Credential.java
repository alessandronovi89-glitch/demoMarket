package com.demo.dto.keycloak;

import io.micronaut.serde.annotation.Serdeable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Keycloak CredentialRepresentation, used when creating a user with an initial password.
 */
@Serdeable
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Credential {
    private String type;
    private String value;
    private boolean temporary;
}
