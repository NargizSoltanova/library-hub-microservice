package org.project.userservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserUpdateDto {
    @NotBlank(message = "Email must not be blank")
    @Email(message = "Email must be in a valid format")
    private String email;

    @NotBlank(message = "Full name must not be blank")
    private String fullName;
}
