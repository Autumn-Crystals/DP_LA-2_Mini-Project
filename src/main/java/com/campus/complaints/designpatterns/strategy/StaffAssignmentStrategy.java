package com.campus.complaints.designpatterns.strategy;

import com.campus.complaints.model.Complaint;
import com.campus.complaints.model.MaintenanceStaff;

import java.util.List;
import java.util.Optional;

/**
 * <b>GoF Strategy Pattern</b> — Strategy Interface.
 *
 * <p>Defines the contract for algorithms that assign maintenance staff to complaints.
 * Different strategies implement different business policies (workload balancing,
 * skill matching, round-robin), allowing the context (service layer/admin) to switch
 * strategies at runtime.
 */
public interface StaffAssignmentStrategy {

    /**
     * Returns the human-readable display name of the strategy.
     */
    String getStrategyName();

    /**
     * Selects the most appropriate available staff member for the given complaint.
     *
     * @param complaint      the complaint requiring assignment
     * @param availableStaff list of eligible staff candidates
     * @return Optional containing the assigned technician, or empty if no candidate matches
     */
    Optional<MaintenanceStaff> assign(Complaint complaint, List<MaintenanceStaff> availableStaff);
}
