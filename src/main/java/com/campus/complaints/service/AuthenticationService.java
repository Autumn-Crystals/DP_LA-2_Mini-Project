package com.campus.complaints.service;

import com.campus.complaints.dao.UserDAO;
import com.campus.complaints.dao.impl.UserDAOImpl;
import com.campus.complaints.model.User;
import com.campus.complaints.security.PasswordHasher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * Service layer for authentication and session management.
 * Enforces secure password hashing via {@link PasswordHasher} and credential validation.
 */
public class AuthenticationService {

    private static final Logger log = LoggerFactory.getLogger(AuthenticationService.class);

    private final UserDAO userDAO;
    private User currentUser;

    public AuthenticationService() {
        this.userDAO = new UserDAOImpl();
    }

    public AuthenticationService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    /**
     * Authenticates a user by email and plaintext password.
     *
     * @param email    user email
     * @param password user plaintext password
     * @return Optional containing User if authentication succeeds, else empty
     */
    public Optional<User> login(String email, String password) {
        if (email == null || email.isBlank()) return Optional.empty();
        if (password == null || password.isBlank()) return Optional.empty();

        Optional<User> userOpt = userDAO.findByEmail(email.trim().toLowerCase());
        if (userOpt.isEmpty()) {
            log.warn("Authentication failed: unknown email: {}", email);
            return Optional.empty();
        }

        User user = userOpt.get();
        if (!PasswordHasher.verifyPassword(password, user.getPasswordHash())) {
            log.warn("Authentication failed: invalid password for email: {}", email);
            return Optional.empty();
        }

        this.currentUser = user;
        log.info("User authenticated successfully: {} ({})", user.getName(), user.getRole());
        return Optional.of(user);
    }

    public void logout() {
        if (currentUser != null) {
            log.info("User logged out: {}", currentUser.getName());
        }
        this.currentUser = null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public boolean isAdmin() {
        return currentUser != null && currentUser.isAdmin();
    }

    public String hashPassword(String plaintext) {
        return PasswordHasher.hashPassword(plaintext);
    }
}
