package com.campus.complaints.designpatterns.strategy;

import com.campus.complaints.model.Complaint;
import com.campus.complaints.model.ComplaintCategory;
import com.campus.complaints.model.MaintenanceStaff;
import com.campus.complaints.model.Priority;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("GoF Pattern: Strategy Pattern Tests")
public class StrategyPatternTest {

    private MaintenanceStaff staffAlice;
    private MaintenanceStaff staffBob;
    private MaintenanceStaff staffCharlie;
    private List<MaintenanceStaff> staffList;
    private Complaint complaint;

    @BeforeEach
    void setUp() {
        staffAlice = new MaintenanceStaff(1, 10, "Alice Smith", "alice@campus.edu", "111",
                MaintenanceStaff.AvailabilityStatus.AVAILABLE, LocalDateTime.now());
        staffAlice.addSkill(ComplaintCategory.NETWORK);
        staffAlice.addSkill(ComplaintCategory.IT_SUPPORT);
        staffAlice.setActiveComplaintCount(3);

        staffBob = new MaintenanceStaff(2, 11, "Bob Jones", "bob@campus.edu", "222",
                MaintenanceStaff.AvailabilityStatus.AVAILABLE, LocalDateTime.now());
        staffBob.addSkill(ComplaintCategory.ELECTRICAL);
        staffBob.addSkill(ComplaintCategory.EQUIPMENT);
        staffBob.setActiveComplaintCount(1);

        staffCharlie = new MaintenanceStaff(3, 12, "Charlie Brown", "charlie@campus.edu", "333",
                MaintenanceStaff.AvailabilityStatus.UNAVAILABLE, LocalDateTime.now());
        staffCharlie.addSkill(ComplaintCategory.NETWORK);
        staffCharlie.setActiveComplaintCount(0);

        staffList = List.of(staffAlice, staffBob, staffCharlie);

        complaint = new Complaint.Builder()
                .title("Wi-Fi Router Down")
                .description("Signal lost in Block C")
                .category(ComplaintCategory.NETWORK)
                .location("Block C Room 101")
                .priority(Priority.HIGH)
                .reporterId(1)
                .build();
    }

    @Test
    @DisplayName("LeastLoadedStaffStrategy should pick available staff with lowest active complaints")
    void testLeastLoadedStrategy() {
        StaffAssignmentStrategy strategy = new LeastLoadedStaffStrategy();

        Optional<MaintenanceStaff> assigned = strategy.assign(complaint, staffList);

        assertTrue(assigned.isPresent());
        assertEquals(staffBob.getId(), assigned.get().getId());
        assertEquals("Bob Jones", assigned.get().getName());
    }

    @Test
    @DisplayName("RoundRobinAssignmentStrategy should rotate cyclically across available staff")
    void testRoundRobinStrategy() {
        StaffAssignmentStrategy strategy = new RoundRobinAssignmentStrategy();

        Optional<MaintenanceStaff> first = strategy.assign(complaint, staffList);
        Optional<MaintenanceStaff> second = strategy.assign(complaint, staffList);
        Optional<MaintenanceStaff> third = strategy.assign(complaint, staffList);

        assertTrue(first.isPresent());
        assertTrue(second.isPresent());
        assertTrue(third.isPresent());

        assertEquals(1, first.get().getId());
        assertEquals(2, second.get().getId());
        assertEquals(1, third.get().getId());
    }

    @Test
    @DisplayName("SkillBasedAssignmentStrategy should pick available staff with matching skill and lowest load")
    void testSkillBasedStrategyMatch() {
        StaffAssignmentStrategy strategy = new SkillBasedAssignmentStrategy();

        Optional<MaintenanceStaff> assigned = strategy.assign(complaint, staffList);

        assertTrue(assigned.isPresent());
        assertEquals(staffAlice.getId(), assigned.get().getId());
        assertEquals("Alice Smith", assigned.get().getName());
    }

    @Test
    @DisplayName("SkillBasedAssignmentStrategy should return empty if no available staff has matching skill")
    void testSkillBasedStrategyNoMatch() {
        StaffAssignmentStrategy strategy = new SkillBasedAssignmentStrategy();

        Complaint plumbingComplaint = new Complaint.Builder()
                .title("Water Leak")
                .description("Leaking pipe in bathroom")
                .category(ComplaintCategory.PLUMBING)
                .location("Floor 2")
                .priority(Priority.MEDIUM)
                .reporterId(2)
                .build();

        Optional<MaintenanceStaff> assigned = strategy.assign(plumbingComplaint, staffList);

        assertFalse(assigned.isPresent(), "No staff has PLUMBING skill, should return Optional.empty()");
    }

    @Test
    @DisplayName("Should allow swapping strategies at runtime seamlessly")
    void testRuntimeStrategySwapping() {
        StaffAssignmentStrategy currentStrategy = new SkillBasedAssignmentStrategy();
        assertEquals("Alice Smith", currentStrategy.assign(complaint, staffList).get().getName());

        currentStrategy = new LeastLoadedStaffStrategy();
        assertEquals("Bob Jones", currentStrategy.assign(complaint, staffList).get().getName());
    }
}
