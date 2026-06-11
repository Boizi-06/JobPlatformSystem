package com.example.jobplatformsystem.controller;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import com.example.jobplatformsystem.security.JwtService;
import lombok.RequiredArgsConstructor;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestController {

    private final JwtService jwtService;

    @GetMapping("/token")
    public String token() {

        UserDetails user =
                User.builder()
                        .username("admin@gmail.com")
                        .password("")
                        .authorities("ROLE_ADMIN")
                        .build();

        return jwtService.generateToken(user);
    }
    @GetMapping("/token-info")
    public String tokenInfo(
            @RequestParam String token) {

        return jwtService.extractUsername(token);
    }
}