package com.campus.complaints.service;

import com.campus.complaints.dao.MaintenanceStaffDAO;
import com.campus.complaints.dao.StaffDAO;
import com.campus.complaints.dao.UserDAO;
import com.campus.complaints.dao.impl.MaintenanceStaffDAOImpl;
import com.campus.complaints.dao.impl.UserDAOImpl;
import com.campus.complaints.model.ComplaintCategory;
import com.campus.complaints.model.MaintenanceStaff;
import com.campus.complaints.model.User;
import com.campus.complaints.model.UserRole;
import com.campus.complaints.security.PasswordHasher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

/**
 * Service for staff management operations.
 */
public class StaffService {

    private static final Logger log = LoggerFactory.getLogger(StaffService.class);

    private final MaintenanceStaffDAO staffDAO;
    private final UserDAO  userDAO;

    public StaffService() {
        this.staffDAO = new MaintenanceStaffDAOImpl();
        this.userDAO  = new UserDAOImpl();
    }

    public StaffService(MaintenanceStaffDAO staffDAO, UserDAO userDAO) {
        this.staffDAO = staffDAO;
        this.userDAO  = userDAO;
    }

    public List<MaintenanceStaff> getAllStaff() {
        return staffDAO.findAll();
    }

    public List<MaintenanceStaff> getAllStaffWithLoad() {
        return staffDAO.findAllWithLoadAndSkills();
    }

    public Optional<MaintenanceStaff> findById(int id) {
        return staffDAO.findById(id);
    }

    /**
     * Creates a new maintenance staff record with a linked user account.
     */
    public MaintenanceStaff addStaff(String name, String email, String phone,
                                      List<ComplaintCategory> skills) {
        if (name == null || name.isBlank())  throw new IllegalArgumentException("Name is required");
        if (email == null || email.isBlank()) throw new IllegalArgumentException("Email is required");
        if (userDAO.emailExists(email.trim().toLowerCase())) {
            throw new IllegalStateException("A user with this email already exists");
        }

        // Create a linked user account for the staff member
        User user = new User();
        user.setName(name.trim());
        user.setEmail(email.trim().toLowerCase());
        user.setPasswordHash(PasswordHasher.hashPassword("Staff@123"));
        user.setRole(UserRole.STUDENT);
        userDAO.save(user);

        MaintenanceStaff staff = new MaintenanceStaff();
        staff.setUserId(user.getId());
        staff.setName(name.trim());
        staff.setEmail(email.trim().toLowerCase());
        staff.setPhone(phone != null ? phone.trim() : "");
        staff.setAvailabilityStatus(MaintenanceStaff.AvailabilityStatus.AVAILABLE);
        if (skills != null) staff.setSkills(skills);

        return staffDAO.save(staff);
    }

    public boolean updateStaff(MaintenanceStaff staff) {
        if (staff.getName() == null || staff.getName().isBlank()) {
            throw new IllegalArgumentException("Name is required");
        }
        return staffDAO.update(staff);
    }

    public void updateSkills(int staffId, List<ComplaintCategory> skills) {
        staffDAO.updateSkills(staffId, skills);
    }
}
