# Strategy Design Pattern

## 1. Pattern Name & Category
- **Pattern**: Strategy Pattern
- **GoF Category**: Behavioral Pattern
- **Dedicated Package**: `com.campus.complaints.designpatterns.strategy`

## 2. Problem Solved
When allocating maintenance tasks to technicians, campus administrators need different algorithms depending on operational workload and campus priorities:
- **Least-Loaded Assignment**: Assigns the complaint to whichever available technician currently has the fewest open tickets (balances overall workload).
- **Skill-Based Assignment**: Filters exclusively for staff members whose expertise covers the complaint category (e.g., Electrician for Electrical, Network Engineer for Wi-Fi), selecting the least loaded among them.
- **Round-Robin Assignment**: Evenly distributes tasks in cyclic order across available staff.

Hardcoding assignment logic or using giant `if-else` blocks violates the Open/Closed Principle. The Strategy Pattern defines a family of interchangeable algorithms, encapsulates each one, and allows the administrator to switch them at runtime.

## 3. Classes Implementing the Pattern
- [`StaffAssignmentStrategy.java`](file:///d:/DP_mini_project/src/main/java/com/campus/complaints/designpatterns/strategy/StaffAssignmentStrategy.java): Common strategy interface declaring `assign(Complaint, List<MaintenanceStaff>)`.
- [`LeastLoadedStaffStrategy.java`](file:///d:/DP_mini_project/src/main/java/com/campus/complaints/designpatterns/strategy/LeastLoadedStaffStrategy.java): Finds available staff with minimum active complaint count.
- [`SkillBasedAssignmentStrategy.java`](file:///d:/DP_mini_project/src/main/java/com/campus/complaints/designpatterns/strategy/SkillBasedAssignmentStrategy.java): Filters staff by matching skill category and picks the least loaded.
- [`RoundRobinAssignmentStrategy.java`](file:///d:/DP_mini_project/src/main/java/com/campus/complaints/designpatterns/strategy/RoundRobinAssignmentStrategy.java): Cycles sequentially through available staff.

## 4. Responsibility of Each Class
- **`StaffAssignmentStrategy`**: Declares algorithm contract and human-readable strategy name.
- **`LeastLoadedStaffStrategy`**: Computes minimum active load across available staff.
- **`SkillBasedAssignmentStrategy`**: Validates `staff.getSkills().contains(complaint.getCategory())`.
- **`RoundRobinAssignmentStrategy`**: Maintains atomic cyclic index pointer.

## 5. Where the Pattern Is Used in the Application
- In `ComplaintController.handleAutoAssign()`, where an interactive dialog allows the administrator to select between the three strategies at runtime, which sets `complaintService.setAssignmentStrategy(selectedStrategy)`.

## 6. Implementation Code Snippet
```java
// From SkillBasedAssignmentStrategy.java
@Override
public Optional<MaintenanceStaff> assign(Complaint complaint, List<MaintenanceStaff> allStaff) {
    return allStaff.stream()
            .filter(MaintenanceStaff::isAvailable)
            .filter(staff -> staff.getSkills().contains(complaint.getCategory()))
            .min(Comparator.comparingInt(MaintenanceStaff::getActiveComplaintCount));
}
```

## 7. Step-by-Step Demonstration Procedure
1. Log in as an Administrator.
2. Open any unassigned complaint in **Complaint Details**.
3. Click **Assign Staff**.
4. A popup dialog appears with a dropdown containing:
   - *Least-Loaded Staff*
   - *Skill-Based Assignment*
   - *Round-Robin Assignment*
5. Select *Skill-Based Assignment* and click OK.
6. The system evaluates the complaint's category against registered staff skills and assigns the best match.

## 8. Viva Q&A Guide
- **Q: How does the Strategy Pattern fulfill the Open/Closed Principle (OCP)?**
  *A:* If the campus introduces a new strategy (e.g., "Priority-Weighted Assignment"), we simply create a new class implementing `StaffAssignmentStrategy`. Existing services and controllers do not require any modifications.
