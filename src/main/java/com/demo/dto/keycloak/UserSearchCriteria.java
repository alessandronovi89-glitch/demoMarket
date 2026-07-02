package com.demo.dto.keycloak;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Optional filters accepted by Keycloak's GET /admin/realms/{realm}/users endpoint.
 * Any null field is omitted from the outgoing query string.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSearchCriteria {
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String search;
    private Boolean enabled;
    private Boolean exact;
    private Integer first;
    private Integer max;
    private Boolean briefRepresentation;
}
