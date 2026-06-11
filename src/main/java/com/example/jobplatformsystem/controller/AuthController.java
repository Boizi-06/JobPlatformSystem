package com.example.jobplatformsystem.controller;

import com.example.jobplatformsystem.dto.request.LoginRequest;
import com.example.jobplatformsystem.dto.request.RegisterRequest;
import com.example.jobplatformsystem.dto.response.AuthResponse;
import com.example.jobplatformsystem.dto.response.LoginResponse;
import com.example.jobplatformsystem.dto.response.UserResponse;
import com.example.jobplatformsystem.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;


    @PostMapping("/register")
    public UserResponse register(
            @Valid @RequestBody RegisterRequest request) {

        return userService.register(request);
    }
    @PostMapping("/login")
    public AuthResponse login(
            @Valid @RequestBody
            LoginRequest request) {

        return userService.login(request);
    }
}
