package com.campus.complaints.service;

import com.campus.complaints.dao.UserDAO;

/**
 * Legacy alias for {@link AuthenticationService} to preserve backward compatibility.
 */
public class AuthService extends AuthenticationService {

    public AuthService() {
        super();
    }

    public AuthService(UserDAO userDAO) {
        super(userDAO);
    }
}
