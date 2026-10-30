package com.campus.complaints.designpatterns.strategy;

import com.campus.complaints.model.Complaint;
import com.campus.complaints.model.MaintenanceStaff;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * <b>GoF Strategy Pattern</b> — Concrete Strategy: Skill-Based Assignment.
 *
 * <p>Matches the complaint's category with technician certified skills.
 * Among staff possessing the required skill, selects the one with the lowest active workload.
 */
public class SkillBasedAssignmentStrategy implements StaffAssignmentStrategy {

    @Override
    public String getStrategyName() {
        return "Skill-Based Assignment";
    }

    @Override
    public Optional<MaintenanceStaff> assign(Complaint complaint, List<MaintenanceStaff> availableStaff) {
        if (availableStaff == null || availableStaff.isEmpty()) {
            return Optional.empty();
        }

        return availableStaff.stream()
                .filter(MaintenanceStaff::isAvailable)
                .filter(staff -> staff.hasSkillFor(complaint.getCategory()))
                .min(Comparator.comparingInt(MaintenanceStaff::getActiveComplaintCount)
                               .thenComparingInt(MaintenanceStaff::getId));
    }
}
