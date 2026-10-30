package com.campus.complaints.fake;

import com.campus.complaints.dao.StaffDAO;
import com.campus.complaints.model.ComplaintCategory;
import com.campus.complaints.model.MaintenanceStaff;

import java.util.*;

/**
 * In-memory test double for {@link StaffDAO}.
 */
public class FakeStaffDAO implements StaffDAO {

    private final Map<Integer, MaintenanceStaff> storage = new LinkedHashMap<>();

    @Override
    public Optional<MaintenanceStaff> findById(int id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<MaintenanceStaff> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public List<MaintenanceStaff> findAllWithLoadAndSkills() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public MaintenanceStaff save(MaintenanceStaff staff) {
        storage.put(staff.getId(), staff);
        return staff;
    }

    @Override
    public boolean update(MaintenanceStaff staff) {
        storage.put(staff.getId(), staff);
        return true;
    }

    @Override
    public void updateSkills(int staffId, List<ComplaintCategory> skills) {
        MaintenanceStaff staff = storage.get(staffId);
        if (staff != null) {
            staff.setSkills(new ArrayList<>(skills));
        }
    }
}
