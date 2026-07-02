package com.demo.dto.keycloak;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;


@Data
public class ResetPasswordRequest {
    @NotBlank(message = "email cannot be blank")
    @Email(message = "email not valid")
    private String email;
}
