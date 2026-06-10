package com.example.jobplatformsystem.mapper;

import com.example.jobplatformsystem.dto.response.UserResponse;
import com.example.jobplatformsystem.entity.User;

public class UserMapper {

    public static UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .active(user.getActive())
                .build();
    }
}