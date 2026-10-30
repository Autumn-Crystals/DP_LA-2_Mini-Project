package com.campus.complaints.dao;

import com.campus.complaints.model.MaintenanceStaff;

import java.util.List;
import java.util.Optional;

/**
 * DAO interface for {@link MaintenanceStaff} persistence operations.
 */
public interface StaffDAO extends MaintenanceStaffDAO {

    Optional<MaintenanceStaff> findById(int id);

    List<MaintenanceStaff> findAll();

    /** Returns all available staff with skills and active complaint counts populated. */
    List<MaintenanceStaff> findAllWithLoadAndSkills();

    MaintenanceStaff save(MaintenanceStaff staff);

    boolean update(MaintenanceStaff staff);

    void updateSkills(int staffId, java.util.List<com.campus.complaints.model.ComplaintCategory> skills);
}
