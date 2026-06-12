package com.example.jobplatformsystem.controller;

import com.example.jobplatformsystem.dto.request.LoginRequest;
import com.example.jobplatformsystem.dto.request.RefreshTokenRequest;
import com.example.jobplatformsystem.dto.request.RegisterRequest;
import com.example.jobplatformsystem.dto.response.AuthResponse;
import com.example.jobplatformsystem.dto.response.RefreshTokenResponse;
import com.example.jobplatformsystem.dto.response.UserResponse;
import com.example.jobplatformsystem.entity.RefreshToken;
import com.example.jobplatformsystem.entity.User;
import com.example.jobplatformsystem.security.CustomUserDetailsService;
import com.example.jobplatformsystem.security.JwtService;
import com.example.jobplatformsystem.service.RefreshTokenService;
import com.example.jobplatformsystem.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.jobplatformsystem.dto.request.LogoutRequest;
import org.springframework.security.core.Authentication;
import com.example.jobplatformsystem.dto.request.ChangePasswordRequest;
import com.example.jobplatformsystem.dto.request.ForgotPasswordRequest;
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final RefreshTokenService refreshTokenService;
    private final JwtService jwtService;
    private final CustomUserDetailsService customUserDetailsService;


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
    @PostMapping("/refresh-token")
    public RefreshTokenResponse refreshToken(
            @RequestBody RefreshTokenRequest request) {

        RefreshToken refreshToken =
                refreshTokenService
                        .verifyRefreshToken(
                                request.getRefreshToken());

        User user = refreshToken.getUser();

        UserDetails userDetails =
                customUserDetailsService
                        .loadUserByUsername(
                                user.getEmail());

        String accessToken =
                jwtService.generateToken(
                        userDetails);

        return RefreshTokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(
                        refreshToken.getToken()
                )
                .build();
    }


    @PostMapping("/logout")
    public String logout(
            @RequestBody LogoutRequest request) {

        refreshTokenService.revokeRefreshToken(
                request.getRefreshToken()
        );

        return "Logout successful";
    }
    @PostMapping("/change-password")
    public String changePassword(
            Authentication authentication,
            @RequestBody ChangePasswordRequest request) {
        System.out.println(authentication.getName());
        System.out.println("Authentication = " + authentication);
        System.out.println("Name = " + authentication.getName());
        return userService.changePassword(
                authentication.getName(),
                request);
    }
    @PostMapping("/forgot-password")
    public String forgotPassword(
            @RequestBody ForgotPasswordRequest request) {

        String newPassword =
                userService.forgotPassword(
                        request.getEmail());

        return "New password: " + newPassword;
    }
}
