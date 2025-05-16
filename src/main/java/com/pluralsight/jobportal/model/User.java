package com.pluralsight.jobportal.model;

import jakarta.persistence.*;

@Entity
@Table(name = "users") // Avoid using "user" as it's a reserved keyword in MySQL
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name; // Needed for Google users

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = true) // OAuth users won't have passwords
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuthProvider authProvider;

    public enum AuthProvider {
        LOCAL, GOOGLE
    }

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    public enum Role {
        APPLICANT, ADMIN
    }

    /* No Argument Constructor */
    public User() {}

    /* Constructor for Normal Users (Requires Password) */
    public User(String name, String email, String password, AuthProvider provider, Role role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.authProvider = provider;
        this.role = role;
    }

    /* Constructor for Google Users (No Password) */
    public User(String name, String email, AuthProvider provider, Role role) {
        this.name = name;
        this.email = email;
        this.password = null; // Google Users Don't Have Passwords
        this.authProvider = provider;
        this.role = role;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public AuthProvider getAuthProvider() { return authProvider; }
    public void setAuthProvider(AuthProvider authProvider) { this.authProvider = authProvider; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
}