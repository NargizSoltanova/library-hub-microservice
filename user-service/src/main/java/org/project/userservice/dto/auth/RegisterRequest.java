package org.project.userservice.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class RegisterRequest {
    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min=8)
    @ToString.Exclude
    private String password;

    @NotBlank
    @Size(min=3, max=50)
    private String username;

    @Size(max=100)
    private String fullName;
}
