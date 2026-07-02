package com.demo.configuration.roles;

import java.util.List;
import java.util.Map;

public interface GetRoles {
    List<String> resolveRoles(Map<String, Object> attributes);
}
