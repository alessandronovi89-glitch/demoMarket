package com.demo.dto.keycloak;

import io.micronaut.serde.annotation.Serdeable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Subset of Keycloak's RoleRepresentation, used for realm role-mapping calls.
 */
@Serdeable
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Role {
    private String id;
    private String name;
    private String description;
    private boolean composite;
    private boolean clientRole;
}
