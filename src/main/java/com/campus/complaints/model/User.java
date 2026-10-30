package com.campus.complaints.model;

import java.time.LocalDateTime;

/**
 * Domain model representing a system user (student or admin).
 */
public class User {

    private int id;
    private String name;
    private String email;
    private String passwordHash;
    private UserRole role;
    private LocalDateTime createdAt;

    public User() {}

    public User(int id, String name, String email, String passwordHash,
                UserRole role, LocalDateTime createdAt) {
        this.id           = id;
        this.name         = name;
        this.email        = email;
        this.passwordHash = passwordHash;
        this.role         = role;
        this.createdAt    = createdAt;
    }

    // ---- Getters & Setters ----

    public int getId()                           { return id; }
    public void setId(int id)                   { this.id = id; }

    public String getName()                      { return name; }
    public void setName(String name)            { this.name = name; }

    public String getEmail()                     { return email; }
    public void setEmail(String email)          { this.email = email; }

    public String getPasswordHash()              { return passwordHash; }
    public void setPasswordHash(String h)       { this.passwordHash = h; }

    public UserRole getRole()                    { return role; }
    public void setRole(UserRole role)          { this.role = role; }

    public LocalDateTime getCreatedAt()          { return createdAt; }
    public void setCreatedAt(LocalDateTime dt)  { this.createdAt = dt; }

    public boolean isAdmin()                     { return role == UserRole.ADMIN; }
    public boolean isStudent()                   { return role == UserRole.STUDENT; }

    @Override
    public String toString() {
        return name + " (" + email + ")";
    }
}
