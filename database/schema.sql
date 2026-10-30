-- ============================================================
-- Smart Campus Complaint & Maintenance System
-- Database Schema
-- MySQL 8.x
-- ============================================================

CREATE DATABASE IF NOT EXISTS campus_complaint_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE campus_complaint_db;

-- ============================================================
-- Table: users
-- Stores all system users (students and administrators)
-- ============================================================
CREATE TABLE IF NOT EXISTS users (
    id            INT UNSIGNED    NOT NULL AUTO_INCREMENT,
    name          VARCHAR(150)    NOT NULL,
    email         VARCHAR(255)    NOT NULL,
    password_hash VARCHAR(255)    NOT NULL,
    role          ENUM('STUDENT','ADMIN') NOT NULL DEFAULT 'STUDENT',
    created_at    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uq_users_email (email),
    INDEX idx_users_role (role)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- Table: maintenance_staff
-- Maintenance personnel linked to user accounts
-- ============================================================
CREATE TABLE IF NOT EXISTS maintenance_staff (
    id                  INT UNSIGNED    NOT NULL AUTO_INCREMENT,
    user_id             INT UNSIGNED    NOT NULL,
    name                VARCHAR(150)    NOT NULL,
    email               VARCHAR(255)    NOT NULL,
    phone               VARCHAR(20)     NULL,
    availability_status ENUM('AVAILABLE','UNAVAILABLE','ON_LEAVE') NOT NULL DEFAULT 'AVAILABLE',
    created_at          DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uq_staff_user (user_id),
    INDEX idx_staff_availability (availability_status),
    CONSTRAINT fk_staff_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- Table: staff_skills
-- Many-to-one: each staff member may have multiple skill categories
-- ============================================================
CREATE TABLE IF NOT EXISTS staff_skills (
    staff_id    INT UNSIGNED    NOT NULL,
    category    VARCHAR(50)     NOT NULL,
    PRIMARY KEY (staff_id, category),
    CONSTRAINT fk_skills_staff FOREIGN KEY (staff_id) REFERENCES maintenance_staff (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- Table: complaints
-- Core complaint records
-- ============================================================
CREATE TABLE IF NOT EXISTS complaints (
    id                INT UNSIGNED    NOT NULL AUTO_INCREMENT,
    title             VARCHAR(255)    NOT NULL,
    description       TEXT            NOT NULL,
    category          VARCHAR(50)     NOT NULL,
    location          VARCHAR(255)    NOT NULL,
    priority          VARCHAR(20)     NOT NULL DEFAULT 'MEDIUM',
    status            VARCHAR(30)     NOT NULL DEFAULT 'PENDING',
    reporter_id       INT UNSIGNED    NOT NULL,
    assigned_staff_id INT UNSIGNED    NULL,
    created_at        DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at        DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    resolved_at       DATETIME        NULL,
    resolution_notes  TEXT            NULL,
    PRIMARY KEY (id),
    INDEX idx_complaints_status   (status),
    INDEX idx_complaints_priority (priority),
    INDEX idx_complaints_category (category),
    INDEX idx_complaints_reporter (reporter_id),
    INDEX idx_complaints_staff    (assigned_staff_id),
    CONSTRAINT fk_complaint_reporter FOREIGN KEY (reporter_id)       REFERENCES users (id)             ON DELETE RESTRICT,
    CONSTRAINT fk_complaint_staff    FOREIGN KEY (assigned_staff_id) REFERENCES maintenance_staff (id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- Table: complaint_history
-- Audit log for complaint changes — supports Memento persistence
-- ============================================================
CREATE TABLE IF NOT EXISTS complaint_history (
    id              INT UNSIGNED    NOT NULL AUTO_INCREMENT,
    complaint_id    INT UNSIGNED    NOT NULL,
    changed_by      INT UNSIGNED    NOT NULL,
    action          VARCHAR(100)    NOT NULL,
    previous_state  JSON            NULL,
    new_state       JSON            NULL,
    created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    INDEX idx_history_complaint (complaint_id),
    INDEX idx_history_user      (changed_by),
    INDEX idx_history_created   (created_at),
    CONSTRAINT fk_history_complaint FOREIGN KEY (complaint_id) REFERENCES complaints (id) ON DELETE CASCADE,
    CONSTRAINT fk_history_user      FOREIGN KEY (changed_by)   REFERENCES users (id)      ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
