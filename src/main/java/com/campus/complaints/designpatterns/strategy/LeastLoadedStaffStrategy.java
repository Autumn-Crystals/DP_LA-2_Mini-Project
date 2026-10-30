package com.campus.complaints.designpatterns.strategy;

import com.campus.complaints.model.Complaint;
import com.campus.complaints.model.MaintenanceStaff;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * <b>GoF Strategy Pattern</b> — Concrete Strategy: Least Loaded.
 *
 * <p>Selects the available staff member who currently has the fewest active
 * (non-resolved, non-cancelled) complaints. In case of ties, favors lower staff ID.
 */
public class LeastLoadedStaffStrategy implements StaffAssignmentStrategy {

    @Override
    public String getStrategyName() {
        return "Least-Loaded Assignment";
    }

    @Override
    public Optional<MaintenanceStaff> assign(Complaint complaint, List<MaintenanceStaff> availableStaff) {
        if (availableStaff == null || availableStaff.isEmpty()) {
            return Optional.empty();
        }

        return availableStaff.stream()
                .filter(MaintenanceStaff::isAvailable)
                .min(Comparator.comparingInt(MaintenanceStaff::getActiveComplaintCount)
                               .thenComparingInt(MaintenanceStaff::getId));
    }
}
