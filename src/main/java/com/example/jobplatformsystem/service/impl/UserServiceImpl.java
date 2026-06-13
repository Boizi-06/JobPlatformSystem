package com.example.jobplatformsystem.service.impl;

import com.example.jobplatformsystem.dto.request.ChangePasswordRequest;
import com.example.jobplatformsystem.dto.request.LoginRequest;
import com.example.jobplatformsystem.dto.request.RegisterRequest;

import com.example.jobplatformsystem.dto.response.UserResponse;
import com.example.jobplatformsystem.entity.RefreshToken;
import com.example.jobplatformsystem.entity.User;
import com.example.jobplatformsystem.exception.DuplicateResourceException;
import com.example.jobplatformsystem.exception.ResourceNotFoundException;
import com.example.jobplatformsystem.mapper.UserMapper;
import com.example.jobplatformsystem.repository.UserRepository;
import com.example.jobplatformsystem.security.CustomUserDetailsService;
import com.example.jobplatformsystem.service.RefreshTokenService;
import com.example.jobplatformsystem.service.TokenBlacklistService;
import com.example.jobplatformsystem.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.util.List;
import com.example.jobplatformsystem.dto.response.AuthResponse;
import com.example.jobplatformsystem.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenBlacklistService tokenBlacklistService;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    public UserResponse register(RegisterRequest request) {

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException(
                    "Username already exists");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException(
                    "Email already exists");
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(
                        passwordEncoder.encode(
                                request.getPassword()))
                .role(request.getRole())
                .active(true)
                .build();

        User savedUser = userRepository.save(user);

        return UserResponse.builder()
                .id(savedUser.getId())
                .username(savedUser.getUsername())
                .email(savedUser.getEmail())
                .role(savedUser.getRole())
                .active(savedUser.getActive())
                .build();
    }

    @Override
    public List<UserResponse> getAllUsers() {

        return userRepository.findAll()
                .stream()
                .map(UserMapper::toResponse)
                .toList();
    }

    @Override
    public UserResponse getUserById(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with id: " + id));

        return UserMapper.toResponse(user);
    }

    @Override
    public void deleteUser(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with id: " + id));

        userRepository.delete(user);
    }
    @Override
    public List<UserResponse> searchUsers(String keyword) {

        return userRepository
                .findByUsernameContainingIgnoreCase(keyword)
                .stream()
                .map(UserMapper::toResponse)
                .toList();
    }
    @Override
    public Page<UserResponse> getUsers(
            int page,
            int size) {

        Pageable pageable =
                PageRequest.of(page, size);

        return userRepository.findAll(pageable)
                .map(UserMapper::toResponse);
    }
    @Override
    public AuthResponse login(LoginRequest request) {

        User user = userRepository
                .findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Email not found"));

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword())) {

            throw new RuntimeException(
                    "Invalid password");
        }

        UserDetails userDetails =
                customUserDetailsService
                        .loadUserByUsername(
                                user.getEmail());

        String accessToken =
                jwtService.generateToken(
                        userDetails);

        RefreshToken refreshToken =
                refreshTokenService
                        .createRefreshToken(user);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }
    @Override
    public String changePassword(
            String username,
            ChangePasswordRequest request) {
        System.out.println("Username from JWT = " + username);

        User user = userRepository
                .findByEmail(username)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        if (!passwordEncoder.matches(
                request.getOldPassword(),
                user.getPassword())) {

            throw new RuntimeException(
                    "Old password is incorrect");
        }

        user.setPassword(
                passwordEncoder.encode(
                        request.getNewPassword()));

        userRepository.save(user);

        return "Password changed successfully";
    }
    @Override
    public String forgotPassword(String email) {

        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Email not found"));

        String newPassword =
                "Job" + System.currentTimeMillis() % 10000;

        user.setPassword(
                passwordEncoder.encode(newPassword));

        userRepository.save(user);

        return newPassword;
    }
    @Override
    public void blacklistAccessToken(String accessToken) {
        tokenBlacklistService.blacklistToken(accessToken);
    }

}