package com.example.jobplatformsystem.service;

import com.example.jobplatformsystem.dto.request.ChangePasswordRequest;
import com.example.jobplatformsystem.dto.request.LoginRequest;
import com.example.jobplatformsystem.dto.request.RegisterRequest;
import com.example.jobplatformsystem.dto.response.AuthResponse;
import com.example.jobplatformsystem.dto.response.JobResponse;
import com.example.jobplatformsystem.dto.response.LoginResponse;
import com.example.jobplatformsystem.dto.response.UserResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface UserService {

    UserResponse register(RegisterRequest request);
    List<UserResponse> getAllUsers();

    UserResponse getUserById(Long id);

    void deleteUser(Long id);
    List<UserResponse> searchUsers(String keyword);

    Page<UserResponse> getUsers(int page, int size);
    AuthResponse login(LoginRequest request);

    String changePassword(
            String username,
            ChangePasswordRequest request);

    String forgotPassword(
            String email);
}