package org.project.userservice.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class LoginRequest {
    @NotBlank
    private String username;

    @ToString.Exclude
    @NotBlank
    private String password;
}
