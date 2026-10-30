# MVC Model Architecture

## Canonical Domain Models

In strict adherence to clean architecture and DRY (Don't Repeat Yourself) principles, the canonical domain entities reside in the root model package:
`com.campus.complaints.model`

These domain models include:
- `Complaint.java`: The core entity encapsulating maintenance issue attributes, priority, category, and state transitions. Implements `ComplaintPrototype`.
- `User.java`: Base user entity with authentication credentials, email, and role authorization.
- `Student.java`: Subclass of `User` representing students reporting campus maintenance issues.
- `MaintenanceStaff.java`: Maintenance technician entity with current active load, availability status, and skill categories.
- `ComplaintStatus.java`: Finite state machine enum governing complaint lifecycle (`PENDING` -> `ASSIGNED` -> `IN_PROGRESS` -> `RESOLVED` / `CANCELLED`).
- `ComplaintCategory.java`: Categories of campus issues (`ELECTRICAL`, `PLUMBING`, `NETWORK`, `EQUIPMENT`, `CLEANING`, `IT_SUPPORT`, `GENERAL`).
- `Priority.java`: Urgency levels (`LOW`, `MEDIUM`, `HIGH`, `CRITICAL`).
- `UserRole.java`: Role-based access control roles (`STUDENT`, `ADMIN`).

## Role in MVC
1. **Model**: Encapsulates state, business rules (e.g., valid status transitions via `canTransitionTo`), and entity contracts.
2. **View**: Renders domain models via presentation components (`SearchableComplaintTable`, `StatusBadge`, `SummaryCard`).
3. **Controller**: Manipulates models through underlying service layers (`ComplaintService`, `StaffAssignmentService`, `DashboardService`, `AuthenticationService`).
