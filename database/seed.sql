-- ============================================================
-- Smart Campus Complaint & Maintenance System
-- Seed Data
-- Run AFTER schema.sql
-- Safe to re-run: uses INSERT IGNORE / ON DUPLICATE KEY
-- ============================================================

USE campus_complaint_db;

-- ============================================================
-- Users
-- Passwords (BCrypt cost 10):
--   admin@campus.edu     -> admin123
--   student1@campus.edu  -> student123
--   student2@campus.edu  -> student123
--   alice@student.edu    -> student123
-- ============================================================
INSERT IGNORE INTO users (id, name, email, password_hash, role) VALUES
(1, 'Campus Administrator', 'admin@campus.edu',
    '$2a$10$jopyhIMX0nqT1hM6rbywOe3X3l4dwvfWs.Omv90UcnQ9zCGR5GF.G',
    'ADMIN'),
(2, 'Student 1', 'student1@campus.edu',
    '$2a$10$gTGHpnuwNq7NqmwrIn7oXuVzBSzjGKBMpKwfPv2Eqm3aqcsA76dqe',
    'STUDENT'),
(3, 'Student 2', 'student2@campus.edu',
    '$2a$10$gTGHpnuwNq7NqmwrIn7oXuVzBSzjGKBMpKwfPv2Eqm3aqcsA76dqe',
    'STUDENT'),
(4, 'Alice Johnson', 'alice@student.edu',
    '$2a$10$gTGHpnuwNq7NqmwrIn7oXuVzBSzjGKBMpKwfPv2Eqm3aqcsA76dqe',
    'STUDENT'),
(5, 'Dave Martinez', 'dave@student.edu',
    '$2a$10$gTGHpnuwNq7NqmwrIn7oXuVzBSzjGKBMpKwfPv2Eqm3aqcsA76dqe',
    'STUDENT'),
-- Staff user accounts (role STUDENT used as placeholder; staff is tracked in maintenance_staff)
(6, 'Ravi Kumar',    'ravi@staff.edu',  '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh/y', 'STUDENT'),
(7, 'Priya Singh',   'priya@staff.edu', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh/y', 'STUDENT'),
(8, 'John Carter',   'john@staff.edu',  '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh/y', 'STUDENT'),
(9, 'Meena Nair',    'meena@staff.edu', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh/y', 'STUDENT');

-- ============================================================
-- Maintenance Staff
-- ============================================================
INSERT IGNORE INTO maintenance_staff (id, user_id, name, email, phone, availability_status) VALUES
(1, 6, 'Ravi Kumar',  'ravi@staff.edu',  '9000000001', 'AVAILABLE'),
(2, 7, 'Priya Singh', 'priya@staff.edu', '9000000002', 'AVAILABLE'),
(3, 8, 'John Carter', 'john@staff.edu',  '9000000003', 'AVAILABLE'),
(4, 9, 'Meena Nair',  'meena@staff.edu', '9000000004', 'AVAILABLE');

-- ============================================================
-- Staff Skills
-- ============================================================
INSERT IGNORE INTO staff_skills (staff_id, category) VALUES
-- Ravi: Electrical + General
(1, 'ELECTRICAL'),
(1, 'GENERAL'),
-- Priya: Network + IT
(2, 'NETWORK'),
(2, 'IT_SUPPORT'),
-- John: Plumbing + Cleaning
(3, 'PLUMBING'),
(3, 'CLEANING'),
-- Meena: Equipment + Electrical
(4, 'EQUIPMENT'),
(4, 'ELECTRICAL');

-- ============================================================
-- Sample Complaints (10 across different categories and statuses)
-- ============================================================
INSERT IGNORE INTO complaints
    (id, title, description, category, location, priority, status, reporter_id, assigned_staff_id, resolved_at, resolution_notes)
VALUES
-- Student 1 (reporter_id = 2) -> 2 complaints
(1,  'Wi-Fi Not Working in Lab 2',
     'Students cannot connect to campus Wi-Fi in Computer Lab 2. The access point appears offline.',
     'NETWORK', 'Computer Lab 2', 'HIGH', 'PENDING', 2, NULL, NULL, NULL),

(2,  'Water Leakage in Washroom B',
     'There is continuous water leakage from the pipe near the sink in Boys Washroom B, Ground Floor.',
     'PLUMBING', 'Boys Washroom B, Ground Floor', 'HIGH', 'IN_PROGRESS', 2, 3, NULL, NULL),

-- Student 2 (reporter_id = 3) -> 3 complaints
(3,  'Broken Ceiling Fan in Room 301',
     'The ceiling fan in Classroom 301 stopped working. It is very hot and affecting lectures.',
     'ELECTRICAL', 'Classroom 301', 'MEDIUM', 'ASSIGNED', 3, 1, NULL, NULL),

(4,  'Projector Not Displaying in Seminar Hall',
     'The projector in the Seminar Hall does not display when connected via HDMI. Affecting all sessions.',
     'EQUIPMENT', 'Seminar Hall', 'HIGH', 'RESOLVED', 3, 4,
     DATE_SUB(NOW(), INTERVAL 1 DAY),
     'Replaced HDMI cable and updated projector firmware. Fully functional now.'),

(5,  'Lights Flickering in Library',
     'Fluorescent lights in the library reading area flicker constantly, causing eye strain.',
     'ELECTRICAL', 'Library - Reading Area', 'MEDIUM', 'PENDING', 3, NULL, NULL, NULL),

(6,  'Garbage Overflow Near Canteen',
     'The garbage bins near the canteen are overflowing. Strong odour and hygiene concern.',
     'CLEANING', 'Canteen Area', 'HIGH', 'ASSIGNED', 4, 3, NULL, NULL),

(7,  'Network Switch Faulty in Server Room',
     'One of the network switches in the server room is causing intermittent connectivity for the entire block.',
     'NETWORK', 'Server Room, Block A', 'CRITICAL', 'IN_PROGRESS', 5, 2, NULL, NULL),

(8,  'Broken Lab Equipment - Oscilloscope',
     'Two oscilloscopes in Electronics Lab 1 are malfunctioning. Students cannot complete practicals.',
     'EQUIPMENT', 'Electronics Lab 1', 'MEDIUM', 'RESOLVED', 2, 4,
     DATE_SUB(NOW(), INTERVAL 3 DAY),
     'Calibrated and repaired both oscilloscopes. Ready for use.'),

(9,  'Air Conditioner Not Cooling in HOD Office',
     'The AC unit in the HOD office is running but not cooling. Room temperature is very high.',
     'ELECTRICAL', 'HOD Office, Block B', 'MEDIUM', 'PENDING', 3, NULL, NULL, NULL),

(10, 'Sanitation Issue in Girls Washroom 2nd Floor',
     'Girls washroom on 2nd floor has not been cleaned for two days. Tiles are dirty and bins are full.',
     'CLEANING', 'Girls Washroom, 2nd Floor', 'HIGH', 'PENDING', 5, NULL, NULL, NULL);

-- ============================================================
-- Complaint History (audit records for existing complaints)
-- ============================================================
INSERT IGNORE INTO complaint_history (id, complaint_id, changed_by, action, previous_state, new_state) VALUES
(1, 2, 1, 'STATUS_CHANGE',
    '{"status":"PENDING","assigned_staff_id":null}',
    '{"status":"ASSIGNED","assigned_staff_id":1}'),
(2, 3, 1, 'STAFF_ASSIGNMENT',
    '{"status":"ASSIGNED","assigned_staff_id":null}',
    '{"status":"IN_PROGRESS","assigned_staff_id":3}'),
(3, 4, 1, 'STATUS_CHANGE',
    '{"status":"IN_PROGRESS","assigned_staff_id":4}',
    '{"status":"RESOLVED","assigned_staff_id":4,"resolution_notes":"Replaced HDMI cable and updated projector firmware. Fully functional now."}'),
(4, 6, 1, 'STAFF_ASSIGNMENT',
    '{"status":"PENDING","assigned_staff_id":null}',
    '{"status":"ASSIGNED","assigned_staff_id":3}'),
(5, 7, 1, 'STATUS_CHANGE',
    '{"status":"ASSIGNED","assigned_staff_id":2}',
    '{"status":"IN_PROGRESS","assigned_staff_id":2}'),
(6, 8, 1, 'STATUS_CHANGE',
    '{"status":"IN_PROGRESS","assigned_staff_id":4}',
    '{"status":"RESOLVED","assigned_staff_id":4,"resolution_notes":"Calibrated and repaired both oscilloscopes. Ready for use."}');
