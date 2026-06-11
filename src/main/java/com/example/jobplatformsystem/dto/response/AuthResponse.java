package com.example.jobplatformsystem.dto.response;

import com.example.jobplatformsystem.entity.enums.Role;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {

    private String accessToken;

    private Long id;

    private String username;

    private String email;

    private Role role;
}