package com.example.jobplatformsystem.entity;

import com.example.jobplatformsystem.entity.enums.Role;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    private Boolean active;
    @Column(name = "cv_url")
    private String cvUrl;

    @Column(name = "portfolio_url")
    private String portfolioUrl;
}