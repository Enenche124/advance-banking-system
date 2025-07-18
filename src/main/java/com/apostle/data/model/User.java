package com.apostle.data.model;

import jakarta.persistence.*;
import lombok.Data;


@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String email;
    private String password;

    @Column(name = "profile_image_path")
    private String profileImagePath;

    @Enumerated(EnumType.STRING)
    private Role role;
}
