package com.campus.complaints.config;

/**
 * Global application constants and configuration parameters.
 */
public final class ApplicationConfiguration {

    private ApplicationConfiguration() {}

    public static final String APPLICATION_TITLE = "Smart Campus Complaint & Maintenance System";
    public static final String APPLICATION_VERSION = "1.0.0";
    public static final String LAB_NAME = "Design Pattern Lab";
    public static final String ACADEMIC_YEAR = "2024-2025";

    // Session and Stack limits
    public static final int MAX_UNDO_STACK_SIZE = 50;
    public static final int DEFAULT_RECENT_LIMIT = 10;
    public static final int BCRYPT_LOG_ROUNDS = 10;

    // Window dimensions
    public static final int WINDOW_WIDTH = 1280;
    public static final int WINDOW_HEIGHT = 800;
    public static final int MIN_WINDOW_WIDTH = 1100;
    public static final int MIN_WINDOW_HEIGHT = 700;
}
