package org.project.userservice.dto;

import lombok.*;
import org.project.userservice.constant.Role;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private String fullName;
    private Role role;
    private Boolean isActive;
}
