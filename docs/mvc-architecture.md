# Model-View-Controller (MVC) Architecture

The **Smart Campus Complaint & Maintenance System** enforces strict Model-View-Controller (MVC) architectural separation to maintain clean boundaries between business rules, user interface rendering, and application flow control.

---

## Architecture Overview

```mermaid
graph TD
    subgraph View Layer [View Layer - Java Swing]
        MainFrame[MainFrame]
        LoginView[LoginView]
        StudentDashboardView[StudentDashboardView]
        AdminDashboardView[AdminDashboardView]
        ComplaintListView[ComplaintListView]
        ComplaintDetailsView[ComplaintDetailsView]
        ComplaintFormView[ComplaintFormView]
        StaffManagementView[StaffManagementView]
    end

    subgraph Controller Layer [Controller Layer]
        LoginController[LoginController]
        ComplaintController[ComplaintController]
        StaffController[StaffController]
        Mediator[ComplaintMediatorImpl]
    end

    subgraph Model Layer [Model & Service Layer]
        Complaint[Complaint Domain Entity]
        User[User Domain Entity]
        MaintenanceStaff[MaintenanceStaff Domain Entity]
        ComplaintService[ComplaintService]
        AuthService[AuthService]
        StaffService[StaffService]
        ComplaintDAO[ComplaintDAO]
        StaffDAO[StaffDAO]
        UserDAO[UserDAO]
    end

    View Layer -->|User UI Events| Controller Layer
    Controller Layer -->|Invokes Operations| Model Layer
    Model Layer -.->|Data & State Updates| Controller Layer
    Controller Layer -->|Updates View Components| View Layer
    Mediator -.->|Coordinates View Elements| View Layer
```

---

## 1. Model Layer

The Model represents the data structures, database persistence mechanisms, domain rules, and business logic. Models have zero dependency on Swing GUI components.

### Domain Entities
- [`Complaint`](file:///d:/DP_mini_project/src/main/java/com/campus/complaints/model/Complaint.java): Core domain object with lifecycle states, builder, and prototype deep copy.
- [`User`](file:///d:/DP_mini_project/src/main/java/com/campus/complaints/model/User.java): Authentication principal (Student / Admin).
- [`MaintenanceStaff`](file:///d:/DP_mini_project/src/main/java/com/campus/complaints/model/MaintenanceStaff.java): Maintenance technician with availability status and skill sets.
- Enums: `ComplaintStatus`, `ComplaintCategory`, `Priority`, `UserRole`.

### Service & Business Logic
- [`AuthService`](file:///d:/DP_mini_project/src/main/java/com/campus/complaints/service/AuthService.java): Manages user sessions, authentication, and BCrypt credential checks.
- [`ComplaintService`](file:///d:/DP_mini_project/src/main/java/com/campus/complaints/service/ComplaintService.java): Coordinates Builder creation, Strategy assignment, Template Method workflows, and Memento snapshots.
- [`StaffService`](file:///d:/DP_mini_project/src/main/java/com/campus/complaints/service/StaffService.java): Manages staff directory and skill assignments.

### Data Access Objects (DAO)
- [`ComplaintDAO`](file:///d:/DP_mini_project/src/main/java/com/campus/complaints/dao/ComplaintDAO.java) / [`ComplaintDAOImpl`](file:///d:/DP_mini_project/src/main/java/com/campus/complaints/dao/ComplaintDAOImpl.java)
- [`StaffDAO`](file:///d:/DP_mini_project/src/main/java/com/campus/complaints/dao/StaffDAO.java) / [`StaffDAOImpl`](file:///d:/DP_mini_project/src/main/java/com/campus/complaints/dao/StaffDAOImpl.java)
- [`UserDAO`](file:///d:/DP_mini_project/src/main/java/com/campus/complaints/dao/UserDAO.java) / [`UserDAOImpl`](file:///d:/DP_mini_project/src/main/java/com/campus/complaints/dao/UserDAOImpl.java)
- [`ComplaintHistoryDAO`](file:///d:/DP_mini_project/src/main/java/com/campus/complaints/dao/ComplaintHistoryDAO.java)

---

## 2. View Layer

The View layer provides the graphical user interface. Views display information provided by controllers and capture user interactions (button clicks, table selections, text inputs).

### Swing Views
- [`MainFrame`](file:///d:/DP_mini_project/src/main/java/com/campus/complaints/view/MainFrame.java): Application window with navigation sidebar, responsive card layout container, and notification banner.
- [`LoginView`](file:///d:/DP_mini_project/src/main/java/com/campus/complaints/view/LoginView.java): Authenticates users into Student or Admin dashboards.
- [`StudentDashboardView`](file:///d:/DP_mini_project/src/main/java/com/campus/complaints/view/StudentDashboardView.java): Student KPI summary cards (Pending, In Progress, Resolved) and recent activity.
- [`AdminDashboardView`](file:///d:/DP_mini_project/src/main/java/com/campus/complaints/view/AdminDashboardView.java): System-wide analytics cards and strategy selectors.
- [`ComplaintListView`](file:///d:/DP_mini_project/src/main/java/com/campus/complaints/view/ComplaintListView.java): Searchable table with filtering by status and category.
- [`ComplaintDetailsView`](file:///d:/DP_mini_project/src/main/java/com/campus/complaints/view/ComplaintDetailsView.java): Detailed inspection panel, workflow execution, staff assignment, and undo trigger.
- [`ComplaintFormView`](file:///d:/DP_mini_project/src/main/java/com/campus/complaints/view/ComplaintFormView.java): Form for submitting new or duplicated complaints.
- [`StaffManagementView`](file:///d:/DP_mini_project/src/main/java/com/campus/complaints/view/StaffManagementView.java): Admin panel to view technicians, active workloads, and manage skill sets.

### UI Design Tokens & Components
- [`AppTheme.java`](file:///d:/DP_mini_project/src/main/java/com/campus/complaints/util/AppTheme.java): Curated color palette (Primary Slate/Indigo, Card backgrounds, Status badges, Typography).
- Reusable UI elements in `com.campus.complaints.component`: `CardPanel`, `StyledButton`, `BadgeLabel`, `NotificationBanner`.

---

## 3. Controller Layer

Controllers mediate between Views and Models. They listen to GUI events, extract input parameters, validate presentation constraints, invoke service logic, and update the views or delegate to the Mediator.

- [`LoginController`](file:///d:/DP_mini_project/src/main/java/com/campus/complaints/controller/LoginController.java): Listens for login requests, authenticates via `AuthService`, and configures the role-based workspace.
- [`ComplaintController`](file:///d:/DP_mini_project/src/main/java/com/campus/complaints/controller/ComplaintController.java): Coordinates submission, duplication, filtering, workflow resolution, auto-assignment, and undo operations.
- [`StaffController`](file:///d:/DP_mini_project/src/main/java/com/campus/complaints/controller/StaffController.java): Handles staff status toggling and skill updates.

---

## MVC Separation Verification

1. **No SQL in Views**: All persistence logic is encapsulated behind DAOs and Services.
2. **No Swing imports in Models**: Domain models (`Complaint`, `User`, `MaintenanceStaff`) are pure Java POJOs without `javax.swing.*` or `java.awt.*` imports.
3. **Decoupled Cross-View Interactions**: Views do not hold references to each other; all notifications and cross-component updates pass through `ComplaintMediator`.
