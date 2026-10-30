package com.campus.complaints.designpatterns.strategy;

import com.campus.complaints.model.Complaint;
import com.campus.complaints.model.MaintenanceStaff;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <b>GoF Strategy Pattern</b> — Concrete Strategy: Round-Robin.
 *
 * <p>Evenly distributes complaints by cycling sequentially through all available technicians.
 */
public class RoundRobinAssignmentStrategy implements StaffAssignmentStrategy {

    private final AtomicInteger roundRobinIndex = new AtomicInteger(0);

    @Override
    public String getStrategyName() {
        return "Round-Robin Assignment";
    }

    @Override
    public Optional<MaintenanceStaff> assign(Complaint complaint, List<MaintenanceStaff> availableStaff) {
        if (availableStaff == null || availableStaff.isEmpty()) {
            return Optional.empty();
        }

        List<MaintenanceStaff> eligible = availableStaff.stream()
                .filter(MaintenanceStaff::isAvailable)
                .sorted(Comparator.comparingInt(MaintenanceStaff::getId))
                .toList();

        if (eligible.isEmpty()) {
            return Optional.empty();
        }

        int index = Math.abs(roundRobinIndex.getAndIncrement() % eligible.size());
        return Optional.of(eligible.get(index));
    }
}
