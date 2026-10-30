# Application Architecture & Design Philosophy

## 1. Architectural Overview

The **Smart Campus Complaint & Maintenance System** follows a layered, modular architecture conforming strictly to:
- **Model-View-Controller (MVC)** for UI separation of concerns.
- **Data Access Object (DAO)** pattern for persistence decoupling.
- **Service Layer Pattern** for orchestrating business operations and design patterns.
- **Gang of Four (GoF) Design Patterns** for specific behavioral, structural, and creational challenges.

```
+-----------------------------------------------------------------------+
|                              VIEW LAYER                               |
|   (LoginView, StudentDashboardView, AdminDashboardView,               |
|    ComplaintManagementView, ComplaintDetailsView, ComplaintFormView)  |
+----------------------------------+------------------------------------+
                                   | User Actions / Events
                                   v
+-----------------------------------------------------------------------+
|                           CONTROLLER LAYER                            |
|   (LoginController, ComplaintController, DashboardController,         |
|    StaffController)                                                   |
+----------------------------------+------------------------------------+
                                   | Delegates to Services
                                   v
+-----------------------------------------------------------------------+
|                            SERVICE LAYER                              |
|   (AuthenticationService, ComplaintService, StaffAssignmentService,   |
|    DashboardService)                                                  |
+-------------------+------------------------------+--------------------+
                    |                              |
                    | Coordinates Patterns         | Database Access
                    v                              v
+------------------------------------+   +------------------------------+
|       GOF DESIGN PATTERNS          |   |          DAO LAYER           |
|  - Builder Pattern                 |   |  - UserDAO & UserDAOImpl     |
|  - Prototype Pattern               |   |  - ComplaintDAO & Impl       |
|  - Mediator Pattern                |   |  - MaintenanceStaffDAO & Impl|
|  - Template Method Pattern         |   |  - ComplaintHistoryDAO       |
|  - Strategy Pattern                |   +--------------+---------------+
|  - Memento Pattern                 |                  |
+------------------------------------+                  v
                                         +------------------------------+
                                         |      PERSISTENCE ENGINE      |
                                         |  - MySQL 8.x via JDBC        |
                                         |  - In-Memory Fallback Demo   |
                                         +------------------------------+
```

## 2. Layer Responsibilities

### 2.1 View Layer (`com.campus.complaints.mvc.view`)
- Built exclusively with Java Swing with custom lightweight components (`AppTheme`, `StatusBadge`, `SummaryCard`, `SearchableComplaintTable`, `CardPanel`).
- Views are strictly decoupled from business logic and database access.
- Views expose user input accessors and event registration methods (`addSubmitListener`, `addLoginListener`).

### 2.2 Controller Layer (`com.campus.complaints.mvc.controller`)
- Bridges the Views and Services.
- Handles user gestures, input validation, and invokes the appropriate service methods.
- Triggers UI state updates through the `ComplaintMediator`.

### 2.3 Service Layer (`com.campus.complaints.service`)
- Contains all core business rules and orchestrates the GoF design patterns:
  - Validates complaint submissions and builds objects via `ComplaintBuilder`.
  - Coordinates automated staff assignment via `StaffAssignmentStrategy`.
  - Executes complaint resolution via `ComplaintWorkflow`.
  - Captures and restores state snapshots via `ComplaintMemento` and `ComplaintHistory`.
  - Manages secure password hashing via `PasswordHasher`.

### 2.4 Data Access Layer (`com.campus.complaints.dao`)
- Provides clean CRUD interfaces for domain entities.
- Implements JDBC prepared statements, SQL injection prevention, and result set mapping.
- Provides an automated in-memory repository fallback (`InMemoryDatabase`) for offline lab demonstrations.

## 3. SOLID Principles Compliance

| Principle | Architectural Implementation |
|---|---|
| **Single Responsibility Principle (SRP)** | Every class has one focused reason to change: DAO handles SQL, View handles Swing rendering, Controller coordinates user interaction, Strategy calculates assignment. |
| **Open/Closed Principle (OCP)** | New staff assignment algorithms can be added by implementing `StaffAssignmentStrategy` without modifying existing controller or UI code. New complaint categories with customized resolution flows can be added by extending `ComplaintWorkflow`. |
| **Liskov Substitution Principle (LSP)** | `Student` seamlessly substitutes `User`. Concrete strategies (`LeastLoadedStaffStrategy`, `SkillBasedAssignmentStrategy`, `RoundRobinAssignmentStrategy`) are fully interchangeable. |
| **Interface Segregation Principle (ISP)** | Compact, cohesive interfaces (`ComplaintDAO`, `MaintenanceStaffDAO`, `ComplaintPrototype`, `ComplaintMediator`, `StaffAssignmentStrategy`). |
| **Dependency Inversion Principle (DIP)** | High-level services depend upon abstractions (`ComplaintDAO`, `MaintenanceStaffDAO`, `StaffAssignmentStrategy`), not concrete database drivers. |
