package com.ilyadudnikov.cloudfilestorage.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Entity
@Table(name = "users")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @NotEmpty(message = "Username can't be empty")
    @Size(min = 2, max = 50, message = "The username must be between 2 and 50 characters long")
    @Column(name = "username")
    private String username;

    @NotEmpty(message = "Password can't be empty")
    @Column(name = "password")
    private String password;

    @Column(name = "role")
    private String role;

    @Size(min = 8, max = 100, message = "The email must be between 2 and 50 characters long")
    @Column(name = "email")
    private String email;
}