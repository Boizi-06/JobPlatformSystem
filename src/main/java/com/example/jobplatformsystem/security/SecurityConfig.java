package com.example.jobplatformsystem.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth

                        // PUBLIC API
                        .requestMatchers(
                                "/api/v1/auth/login",
                                "/api/v1/auth/register",
                                "/api/v1/auth/refresh-token",
                                "/api/v1/auth/forgot-password"
                        ).permitAll()

                        // AUTHENTICATED API
                        .requestMatchers(
                                "/api/v1/auth/change-password",
                                "/api/v1/auth/logout"
                        ).authenticated()

                        // ADMIN - USER MANAGEMENT
                        .requestMatchers(
                                "/api/v1/users/**"
                        ).hasRole("ADMIN")

                        // ADMIN - APPROVE / REJECT JOB
                        .requestMatchers(
                                org.springframework.http.HttpMethod.PUT,
                                "/api/v1/jobs/*/approve",
                                "/api/v1/jobs/*/reject"
                        ).hasRole("ADMIN")

                        // EMPLOYER / ADMIN - CREATE JOB
                        .requestMatchers(
                                org.springframework.http.HttpMethod.POST,
                                "/api/v1/jobs"
                        ).hasAnyRole("EMPLOYER", "ADMIN")

                        // EMPLOYER / ADMIN - UPDATE JOB
                        .requestMatchers(
                                org.springframework.http.HttpMethod.PUT,
                                "/api/v1/jobs/*"
                        ).hasAnyRole("EMPLOYER", "ADMIN")

                        // EMPLOYER / ADMIN - DELETE JOB
                        .requestMatchers(
                                org.springframework.http.HttpMethod.DELETE,
                                "/api/v1/jobs/*"
                        ).hasAnyRole("EMPLOYER", "ADMIN")

                        // AUTHENTICATED - VIEW JOBS
                        .requestMatchers(
                                org.springframework.http.HttpMethod.GET,
                                "/api/v1/jobs/**"
                        ).authenticated()

                        // CANDIDATE - APPLY JOB
                        .requestMatchers(
                                org.springframework.http.HttpMethod.POST,
                                "/api/v1/applications/apply"
                        ).hasRole("CANDIDATE")

                        // CANDIDATE - VIEW OWN APPLICATIONS
                        .requestMatchers(
                                org.springframework.http.HttpMethod.GET,
                                "/api/v1/applications/candidate/**"
                        ).hasRole("CANDIDATE")

                        // EMPLOYER / ADMIN - VIEW APPLICATIONS BY JOB
                        .requestMatchers(
                                org.springframework.http.HttpMethod.GET,
                                "/api/v1/applications/job/**"
                        ).hasAnyRole("EMPLOYER", "ADMIN")

                        // EMPLOYER / ADMIN - UPDATE APPLICATION STATUS
                        .requestMatchers(
                                org.springframework.http.HttpMethod.PUT,
                                "/api/v1/applications/*/status"
                        ).hasAnyRole("EMPLOYER", "ADMIN")

                        // CANDIDATE - UPLOAD CV
                        .requestMatchers(
                                org.springframework.http.HttpMethod.POST,
                                "/api/v1/files/upload-cv/**"
                        ).hasRole("CANDIDATE")

                        .anyRequest()
                        .authenticated()
                )

                .formLogin(form -> form.disable())

                .httpBasic(httpBasic -> httpBasic.disable())

                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config)
            throws Exception {

        return config.getAuthenticationManager();
    }

}