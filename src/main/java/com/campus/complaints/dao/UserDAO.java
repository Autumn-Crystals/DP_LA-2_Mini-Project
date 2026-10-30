package com.campus.complaints.dao;

import com.campus.complaints.model.User;
import com.campus.complaints.model.UserRole;

import java.util.Optional;

/**
 * DAO interface for {@link User} persistence operations.
 */
public interface UserDAO {

    Optional<User> findByEmail(String email);

    Optional<User> findById(int id);

    User save(User user);

    boolean emailExists(String email);
}
