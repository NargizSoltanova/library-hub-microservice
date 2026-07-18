package org.project.userservice.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class LoginRequest {
    @NotBlank(message = "Username must not be blank")
    private String username;

    @ToString.Exclude
    @NotBlank(message = "Password must not be blank")
    private String password;
}
