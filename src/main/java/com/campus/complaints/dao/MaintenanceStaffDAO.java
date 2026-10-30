package com.campus.complaints.dao;

import com.campus.complaints.model.ComplaintCategory;
import com.campus.complaints.model.MaintenanceStaff;

import java.util.List;
import java.util.Optional;

/**
 * Data Access Object interface for {@link MaintenanceStaff} entities.
 */
public interface MaintenanceStaffDAO {

    Optional<MaintenanceStaff> findById(int id);

    List<MaintenanceStaff> findAll();

    /** Returns all staff with their current active workload and certified skills. */
    List<MaintenanceStaff> findAllWithLoadAndSkills();

    MaintenanceStaff save(MaintenanceStaff staff);

    boolean update(MaintenanceStaff staff);

    void updateSkills(int staffId, List<ComplaintCategory> skills);
}
