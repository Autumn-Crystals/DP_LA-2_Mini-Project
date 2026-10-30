package com.campus.complaints.security;

import com.campus.complaints.config.ApplicationConfiguration;
import org.mindrot.jbcrypt.BCrypt;

/**
 * Security utility for cryptographically secure password hashing and verification.
 * Uses BCrypt with configurable salt cost factors.
 */
public final class PasswordHasher {

    private PasswordHasher() {}

    /**
     * Hashes plaintext password using BCrypt.
     */
    public static String hash(String plaintext) {
        if (plaintext == null || plaintext.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters long.");
        }
        return BCrypt.hashpw(plaintext, BCrypt.gensalt(ApplicationConfiguration.BCRYPT_LOG_ROUNDS));
    }

    /**
     * Verifies plaintext password against a stored BCrypt hash.
     */
    public static boolean verify(String plaintext, String hash) {
        if (plaintext == null || hash == null || hash.isBlank()) {
            return false;
        }
        try {
            return BCrypt.checkpw(plaintext, hash);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public static String hashPassword(String plaintext) {
        return hash(plaintext);
    }

    public static boolean verifyPassword(String plaintext, String hash) {
        return verify(plaintext, hash);
    }
}
