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
    @NotBlank @Email
    private String email;
    @NotBlank
    private String fullName;
}
