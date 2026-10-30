package com.campus.complaints.model;

import java.time.LocalDateTime;

/**
 * Domain model representing a Student user.
 * Inherits authentication and core profile properties from {@link User}.
 */
public class Student extends User {

    private String studentIdNumber;
    private String department;

    public Student() {
        super();
        setRole(UserRole.STUDENT);
    }

    public Student(int id, String name, String email, String passwordHash,
                   String studentIdNumber, String department, LocalDateTime createdAt) {
        super(id, name, email, passwordHash, UserRole.STUDENT, createdAt);
        this.studentIdNumber = studentIdNumber;
        this.department = department;
    }

    public String getStudentIdNumber() {
        return studentIdNumber;
    }

    public void setStudentIdNumber(String studentIdNumber) {
        this.studentIdNumber = studentIdNumber;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    @Override
    public String toString() {
        return getName() + " [Student - " + (studentIdNumber != null ? studentIdNumber : getEmail()) + "]";
    }
}
