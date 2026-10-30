package com.campus.complaints.service;

import com.campus.complaints.dao.MaintenanceStaffDAO;
import com.campus.complaints.dao.StaffDAO;
import com.campus.complaints.dao.impl.MaintenanceStaffDAOImpl;
import com.campus.complaints.designpatterns.strategy.LeastLoadedStaffStrategy;
import com.campus.complaints.designpatterns.strategy.SkillBasedAssignmentStrategy;
import com.campus.complaints.designpatterns.strategy.StaffAssignmentStrategy;
import com.campus.complaints.model.Complaint;
import com.campus.complaints.model.MaintenanceStaff;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Service orchestrating the Strategy Pattern for maintenance staff assignment.
 * Encapsulates algorithm selection (Least Loaded, Skill-Based, Round Robin)
 * and dispatches assignment evaluations.
 */
public class StaffAssignmentService {

    private static final Logger log = LoggerFactory.getLogger(StaffAssignmentService.class);

    private final MaintenanceStaffDAO staffDAO;
    private StaffAssignmentStrategy strategy;

    public StaffAssignmentService() {
        this(new MaintenanceStaffDAOImpl(), new LeastLoadedStaffStrategy());
    }

    public StaffAssignmentService(MaintenanceStaffDAO staffDAO) {
        this(staffDAO, new LeastLoadedStaffStrategy());
    }

    public StaffAssignmentService(MaintenanceStaffDAO staffDAO, StaffAssignmentStrategy strategy) {
        this.staffDAO = staffDAO;
        this.strategy = Objects.requireNonNull(strategy, "Strategy cannot be null");
    }

    public void setStrategy(StaffAssignmentStrategy strategy) {
        this.strategy = Objects.requireNonNull(strategy, "Strategy cannot be null");
        log.info("StaffAssignmentService strategy set to: {}", strategy.getStrategyName());
    }

    public StaffAssignmentStrategy getStrategy() {
        return strategy;
    }

    /**
     * Executes the configured strategy to select the most appropriate staff member.
     *
     * @param complaint the complaint requiring staff assignment
     * @return Optional containing the selected staff member, or empty if none eligible
     */
    public Optional<MaintenanceStaff> assignStaff(Complaint complaint) {
        List<MaintenanceStaff> staffList = staffDAO.findAllWithLoadAndSkills();
        Optional<MaintenanceStaff> selected = strategy.assign(complaint, staffList);

        if (selected.isPresent()) {
            log.info("Assigned complaint #{} to staff '{}' using strategy '{}'",
                    complaint.getId(), selected.get().getName(), strategy.getStrategyName());
        } else {
            log.warn("Strategy '{}' could not find eligible staff for complaint #{}",
                    strategy.getStrategyName(), complaint.getId());
        }
        return selected;
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
}
