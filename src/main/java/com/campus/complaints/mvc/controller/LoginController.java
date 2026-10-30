package com.campus.complaints.mvc.controller;

import com.campus.complaints.model.User;
import com.campus.complaints.mvc.view.LoginView;
import com.campus.complaints.mvc.view.MainFrame;
import com.campus.complaints.service.AuthenticationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * MVC Controller for login operations.
 * Validates credentials, manages authentication session, and triggers role-based routing.
 */
public class LoginController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    private final LoginView view;
    private final AuthenticationService authService;
    private final MainFrame mainFrame;

    private final Runnable onAdminLogin;
    private final Runnable onStudentLogin;

    public LoginController(LoginView view,
                           AuthenticationService authService,
                           MainFrame mainFrame,
                           Runnable onAdminLogin,
                           Runnable onStudentLogin) {
        this.view           = view;
        this.authService    = authService;
        this.mainFrame      = mainFrame;
        this.onAdminLogin   = onAdminLogin;
        this.onStudentLogin = onStudentLogin;

        attachListeners();
    }

    private void attachListeners() {
        view.addLoginListener(e -> handleLogin());
    }

    private void handleLogin() {
        view.clearError();
        String email    = view.getEmail();
        String password = view.getPassword();

        if (email.isBlank()) {
            view.setErrorMessage("Email address is required.");
            return;
        }
        if (password.isBlank()) {
            view.setErrorMessage("Password is required.");
            return;
        }
        if (!email.contains("@")) {
            view.setErrorMessage("Please enter a valid email address.");
            return;
        }

        Optional<User> result = authService.login(email, password);
        if (result.isEmpty()) {
            view.setErrorMessage("Invalid email or password. Please try again.");
            log.warn("Login failed for email: {}", email);
            return;
        }

        User user = result.get();
        log.info("Login successful: {} ({})", user.getName(), user.getRole());

        if (user.isAdmin()) {
            if (onAdminLogin != null) onAdminLogin.run();
        } else {
            if (onStudentLogin != null) onStudentLogin.run();
        }
    }
}
