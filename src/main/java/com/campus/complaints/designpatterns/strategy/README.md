# Strategy Pattern — Viva Demonstration Guide

## 1. Pattern Name
**Strategy Pattern** (GoF Behavioral Design Pattern)

## 2. Problem Solved by the Pattern
Campus administrators need different staff assignment policies depending on operational requirements:
- **Workload Balancing**: Prevent technician overload during normal operations (*Least-Loaded Strategy*).
- **Skill Specialization**: Assign technicians certified in electrical, plumbing, or networking (*Skill-Based Strategy*).
- **Shift Rotation**: Fair distribution during peak shifts (*Round-Robin Strategy*).

Hardcoding conditional `if-else` blocks violates the Open/Closed Principle. The Strategy pattern encapsulates each algorithm behind a common interface, allowing dynamic runtime selection.

## 3. Classes Implementing the Pattern
- `com.campus.complaints.designpatterns.strategy.StaffAssignmentStrategy`: Strategy interface.
- `com.campus.complaints.designpatterns.strategy.LeastLoadedStaffStrategy`: Algorithm selecting the technician with fewest active complaints.
- `com.campus.complaints.designpatterns.strategy.SkillBasedAssignmentStrategy`: Algorithm matching complaint category with technician certified skills.
- `com.campus.complaints.designpatterns.strategy.RoundRobinAssignmentStrategy`: Algorithm cycling sequentially through available staff.

## 4. Responsibility of Each Class
- **`StaffAssignmentStrategy`**: Defines `Optional<MaintenanceStaff> assign(Complaint, List<MaintenanceStaff>)`.
- **`LeastLoadedStaffStrategy`**: Filters available staff and finds minimum active complaints count.
- **`SkillBasedAssignmentStrategy`**: Filters staff whose `hasSkillFor(category)` is true, then picks the least loaded among them.
- **`RoundRobinAssignmentStrategy`**: Uses an atomic counter to cycle staff deterministically.

## 5. Where the Pattern is Used in the Application
- `com.campus.complaints.service.StaffAssignmentService` / `ComplaintService`: Holds reference to the active `StaffAssignmentStrategy` (Context) and delegates `autoAssignStaff()`.
- `com.campus.complaints.mvc.view.AdminDashboardView`: Provides a dropdown selector allowing the admin to switch strategies at runtime without restarting the application.

## 6. Code Example from Implementation
```java
// Strategy interface
public interface StaffAssignmentStrategy {
    String getStrategyName();
    Optional<MaintenanceStaff> assign(Complaint complaint, List<MaintenanceStaff> availableStaff);
}

// In ComplaintService (Context):
public Optional<MaintenanceStaff> autoAssignStaff(Complaint complaint, int userId) {
    List<MaintenanceStaff> staff = staffDAO.findAllWithLoadAndSkills();
    // Dynamically invokes the currently selected strategy
    return assignmentStrategy.assign(complaint, staff);
}
```

## 7. Step-by-Step Viva Demonstration
1. Open [`StaffAssignmentStrategy.java`](file:///d:/DP_mini_project/src/main/java/com/campus/complaints/designpatterns/strategy/StaffAssignmentStrategy.java) and the concrete strategy classes.
2. Log in as **Admin** (`admin@campus.edu` / `admin123`).
3. On the dashboard, switch the strategy dropdown to **"Skill-Based Assignment"**.
4. Click **"Auto-Assign Staff"** on a Network complaint; show that Priya Singh (certified in Network/IT) is assigned.
5. Switch the strategy to **"Least-Loaded Assignment"**; show that the technician with 0 active complaints is selected regardless of skills.

## 8. Viva Quick Answer (Summary for Professor)
> *"The Strategy Pattern encapsulates a family of staff assignment algorithms behind a common interface (`StaffAssignmentStrategy`). The application context (`ComplaintService`) can switch between Least-Loaded, Skill-Based, and Round-Robin strategies dynamically at runtime based on the administrator's operational choice, adhering to the Open/Closed Principle."*
