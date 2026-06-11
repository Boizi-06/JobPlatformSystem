package com.example.jobplatformsystem.dto.response;

import com.example.jobplatformsystem.entity.enums.Role;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {

    private Long id;

    private String username;

    private String email;

    private Role role;

    private String message;
}