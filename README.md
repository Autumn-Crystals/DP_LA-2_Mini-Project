# Smart Campus Complaint & Maintenance System

> **A Modular Java Swing + JDBC + MySQL Application Demonstrating Gang of Four (GoF) Design Patterns and Clean MVC Architecture.**

---

## 1. Project Overview

The **Smart Campus Complaint & Maintenance System** is an enterprise-grade desktop application designed for university campuses. It allows students to submit, track, and duplicate facility maintenance issues while empowering campus administrators to manage staff workloads, auto-assign technicians using pluggable algorithms, resolve complaints through specialized workflow templates, and roll back state changes via snapshot restoration.

The codebase strictly adheres to:
- **Clean Architecture & SOLID Principles**
- **Strict Package Separation for Every GoF Design Pattern**
- **Explicit Model-View-Controller (MVC) Structure**
- **Dual Runtime Support**: Connects to MySQL 8.x with an automatic offline in-memory fallback (`InMemoryDatabase`) for 100% reliable lab demonstrations.

---

## 2. Design Pattern Implementation Mapping

When demonstrating a specific design pattern to your professor, open its dedicated package below:

| # | Design Pattern | GoF Category | Dedicated Java Package | Key Class to Open | Demonstration README |
|---|---|---|---|---|---|
| **1** | **Builder** | Creational | `com.campus.complaints.designpatterns.builder` | [`ComplaintBuilder.java`](file:///d:/DP_mini_project/src/main/java/com/campus/complaints/designpatterns/builder/ComplaintBuilder.java) | [builder/README.md](file:///d:/DP_mini_project/src/main/java/com/campus/complaints/designpatterns/builder/README.md) |
| **2** | **Prototype** | Creational | `com.campus.complaints.designpatterns.prototype` | [`ComplaintPrototype.java`](file:///d:/DP_mini_project/src/main/java/com/campus/complaints/designpatterns/prototype/ComplaintPrototype.java), [`ComplaintCloner.java`](file:///d:/DP_mini_project/src/main/java/com/campus/complaints/designpatterns/prototype/ComplaintCloner.java) | [prototype/README.md](file:///d:/DP_mini_project/src/main/java/com/campus/complaints/designpatterns/prototype/README.md) |
| **3** | **Mediator** | Behavioral | `com.campus.complaints.designpatterns.mediator` | [`ComplaintMediator.java`](file:///d:/DP_mini_project/src/main/java/com/campus/complaints/designpatterns/mediator/ComplaintMediator.java), [`ComplaintMediatorImpl.java`](file:///d:/DP_mini_project/src/main/java/com/campus/complaints/designpatterns/mediator/ComplaintMediatorImpl.java) | [mediator/README.md](file:///d:/DP_mini_project/src/main/java/com/campus/complaints/designpatterns/mediator/README.md) |
| **4** | **Template Method** | Behavioral | `com.campus.complaints.designpatterns.template` | [`ComplaintWorkflow.java`](file:///d:/DP_mini_project/src/main/java/com/campus/complaints/designpatterns/template/ComplaintWorkflow.java), [`ITSupportWorkflow.java`](file:///d:/DP_mini_project/src/main/java/com/campus/complaints/designpatterns/template/ITSupportWorkflow.java) | [template/README.md](file:///d:/DP_mini_project/src/main/java/com/campus/complaints/designpatterns/template/README.md) |
| **5** | **Strategy** | Behavioral | `com.campus.complaints.designpatterns.strategy` | [`StaffAssignmentStrategy.java`](file:///d:/DP_mini_project/src/main/java/com/campus/complaints/designpatterns/strategy/StaffAssignmentStrategy.java), [`SkillBasedAssignmentStrategy.java`](file:///d:/DP_mini_project/src/main/java/com/campus/complaints/designpatterns/strategy/SkillBasedAssignmentStrategy.java) | [strategy/README.md](file:///d:/DP_mini_project/src/main/java/com/campus/complaints/designpatterns/strategy/README.md) |
| **6** | **Memento** | Behavioral | `com.campus.complaints.designpatterns.memento` | [`ComplaintMemento.java`](file:///d:/DP_mini_project/src/main/java/com/campus/complaints/designpatterns/memento/ComplaintMemento.java), [`ComplaintHistory.java`](file:///d:/DP_mini_project/src/main/java/com/campus/complaints/designpatterns/memento/ComplaintHistory.java) | [memento/README.md](file:///d:/DP_mini_project/src/main/java/com/campus/complaints/designpatterns/memento/README.md) |

---

## 3. Project Directory Structure

```text
CampusComplaintSystem/
├── pom.xml
├── README.md
├── .gitignore
│
├── database/
│   ├── schema.sql                               # MySQL DDL (tables, keys, indexes)
│   ├── seed.sql                                 # Seed data for campus users & complaints
│   └── README.md                                # Database setup & fallback guide
│
├── docs/
│   ├── architecture/
│   │   ├── application-architecture.md          # Multi-tier design & SOLID mapping
│   │   └── mvc-architecture.puml                # PlantUML diagram of MVC architecture
│   │
│   ├── design-patterns/
│   │   ├── builder-pattern.md                   # Builder documentation & code example
│   │   ├── prototype-pattern.md                 # Prototype documentation & code example
│   │   ├── mediator-pattern.md                  # Mediator documentation & code example
│   │   ├── template-method-pattern.md           # Template Method documentation & code example
│   │   ├── strategy-pattern.md                  # Strategy documentation & code example
│   │   └── memento-pattern.md                   # Memento documentation & code example
│   │
│   ├── uml/
│   │   └── design-pattern-class-diagram.puml    # Complete PlantUML GoF class diagram
│   │
│   └── demonstration/
│       ├── viva-guide.md                        # Academic questions & answers guide
│       └── demonstration-workflow.md            # Step-by-step live demonstration script
│
└── src/
    ├── main/
    │   ├── java/
    │   │   └── com/campus/complaints/
    │   │       ├── CampusComplaintApplication.java      # Application bootstrap entry point
    │   │       ├── Main.java                            # Backwards-compatible launch alias
    │   │       │
    │   │       ├── config/
    │   │       │   ├── DatabaseConfiguration.java       # Database connection pooling & test
    │   │       │   ├── ApplicationConfiguration.java    # Application-wide constants & flags
    │   │       │   └── DatabaseConfig.java              # Backward-compatibility alias
    │   │       │
    │   │       ├── model/                               # Canonical Domain Models
    │   │       │   ├── Complaint.java                   # Core entity implementing ComplaintPrototype
    │   │       │   ├── User.java                        # Base user credentials & role
    │   │       │   ├── Student.java                     # Specialized Student entity
    │   │       │   ├── MaintenanceStaff.java            # Technician load, skills, availability
    │   │       │   ├── ComplaintStatus.java             # Lifecycle transition state machine
    │   │       │   ├── ComplaintCategory.java           # Category enumeration
    │   │       │   ├── Priority.java                    # Urgency levels
    │   │       │   └── UserRole.java                    # Access control roles
    │   │       │
    │   │       ├── designpatterns/                      # DEDICATED GOF PATTERN PACKAGES
    │   │       │   ├── builder/
    │   │       │   │   ├── ComplaintBuilder.java        # Fluent builder with mandatory validation
    │   │       │   │   └── README.md
    │   │       │   ├── prototype/
    │   │       │   │   ├── ComplaintPrototype.java      # Prototype interface
    │   │       │   │   ├── ComplaintCloner.java         # Prototype client utility
    │   │       │   │   └── README.md
    │   │       │   ├── mediator/
    │   │       │   │   ├── ComplaintMediator.java       # Mediator interface
    │   │       │   │   ├── ComplaintMediatorImpl.java   # Centralized view coordinator
    │   │       │   │   └── README.md
    │   │       │   ├── template/
    │   │       │   │   ├── ComplaintWorkflow.java       # Abstract template method class
    │   │       │   │   ├── GeneralMaintenanceWorkflow.java # Concrete physical workflow
    │   │       │   │   ├── ITSupportWorkflow.java       # Concrete IT technical workflow
    │   │       │   │   ├── WorkflowException.java       # Validation failure exception
    │   │       │   │   └── README.md
    │   │       │   ├── strategy/
    │   │       │   │   ├── StaffAssignmentStrategy.java # Common strategy contract
    │   │       │   │   ├── LeastLoadedStaffStrategy.java# Minimizes active ticket load
    │   │       │   │   ├── SkillBasedAssignmentStrategy.java # Matches skills & load
    │   │       │   │   ├── RoundRobinAssignmentStrategy.java # Cyclic staff distribution
    │   │       │   │   └── README.md
    │   │       │   └── memento/
    │   │       │       ├── ComplaintMemento.java        # Immutable state snapshot
    │   │       │       ├── ComplaintHistory.java        # Caretaker LIFO undo stack
    │   │       │       └── README.md
    │   │       │
    │   │       ├── mvc/                                 # EXPLICIT MVC ORGANIZATION
    │   │       │   ├── model/
    │   │       │   │   └── README.md                    # Canonical model integration doc
    │   │       │   ├── view/
    │   │       │   │   ├── LoginView.java               # Authentication card UI
    │   │       │   │   ├── StudentDashboardView.java    # Student KPI metrics & shortcuts
    │   │       │   │   ├── AdminDashboardView.java      # Admin KPI overview & quick actions
    │   │       │   │   ├── ComplaintFormView.java       # Ticket submission form
    │   │       │   │   ├── ComplaintDetailsView.java    # Full ticket viewer & action buttons
    │   │       │   │   ├── ComplaintManagementView.java # Filterable complaint table
    │   │       │   │   ├── StaffManagementView.java     # Technician directory & load view
    │   │       │   │   └── MainFrame.java               # App window shell & sidebar
    │   │       │   └── controller/
    │   │       │       ├── LoginController.java         # Authentication flow controller
    │   │       │       ├── ComplaintController.java     # Complaint operations controller
    │   │       │       ├── DashboardController.java     # Metric calculations controller
    │   │       │       └── StaffController.java         # Technician management controller
    │   │       │
    │   │       ├── dao/                                 # DATA ACCESS LAYER
    │   │       │   ├── ComplaintDAO.java
    │   │       │   ├── UserDAO.java
    │   │       │   ├── MaintenanceStaffDAO.java
    │   │       │   ├── StaffDAO.java
    │   │       │   ├── ComplaintHistoryDAO.java
    │   │       │   ├── impl/
    │   │       │   │   ├── ComplaintDAOImpl.java        # JDBC MySQL implementation
    │   │       │   │   ├── UserDAOImpl.java             # JDBC MySQL implementation
    │   │       │   │   └── MaintenanceStaffDAOImpl.java # JDBC MySQL implementation
    │   │       │   └── inmemory/
    │   │       │       └── InMemoryDatabase.java        # Offline fallback repository
    │   │       │
    │   │       ├── service/                             # BUSINESS LOGIC & ORCHESTRATION
    │   │       │   ├── AuthenticationService.java       # Password hashing & sessions
    │   │       │   ├── ComplaintService.java            # Coordinates Builder, Prototype, Memento
    │   │       │   ├── StaffAssignmentService.java      # Coordinates Strategy execution
    │   │       │   ├── DashboardService.java            # Aggregates KPI metric counters
    │   │       │   ├── AuthService.java                 # Compatibility wrapper
    │   │       │   └── StaffService.java                # Technician administration
    │   │       │
    │   │       ├── component/                           # REUSABLE UI PRESENTATION COMPONENTS
    │   │       │   ├── NavigationSidebar.java
    │   │       │   ├── SummaryCard.java
    │   │       │   ├── StatusBadge.java
    │   │       │   ├── SearchableComplaintTable.java
    │   │       │   ├── CardPanel.java
    │   │       │   ├── BadgeLabel.java
    │   │       │   ├── NotificationBanner.java
    │   │       │   └── StyledButton.java
    │   │       │
    │   │       ├── security/                            # APPLICATION SECURITY
    │   │       │   ├── PasswordHasher.java              # BCrypt salt hashing & verification
    │   │       │   └── AccessControlService.java        # Role-based action authorization
    │   │       │
    │   │       └── util/                                # STYLING & FORMATTING UTILITIES
    │   │           ├── AppTheme.java                    # Colors, typography, spacing tokens
    │   │           ├── IconFactory.java                 # Vector SVG graphics rendering
    │   │           ├── DateTimeFormatterUtil.java       # Human-readable date-time formats
    │   │           └── ValidationUtil.java              # Input sanitizer & email validation
    │   │
    │   └── resources/
    │       ├── application.properties                   # Connection settings
    │       └── application.properties.example           # Template credentials
    │
    └── test/
        └── java/
            └── com/campus/complaints/
                ├── designpatterns/
                │   ├── builder/BuilderPatternTest.java
                │   ├── prototype/PrototypePatternTest.java
                │   ├── mediator/MediatorPatternTest.java
                │   ├── template/TemplateMethodPatternTest.java
                │   ├── strategy/StrategyPatternTest.java
                │   └── memento/MementoPatternTest.java
                │
                ├── mvc/MvcFlowTest.java
                ├── service/ComplaintServiceTest.java
                └── dao/ComplaintDAOTest.java
```

---

## 4. How to Build & Run

### 4.1 Prerequisites
- **Java Development Kit (JDK)**: Java 23 installed
- **Apache Maven**: 3.9+ or 3.10+ installed

### 4.2 Compile & Execute
```bash
# Compile and package fat JAR
mvn clean package -DskipTests

# Run the packaged application JAR
java -jar target/CampusComplaintSystem.jar
```

Or run directly from Maven:
```bash
mvn compile exec:java -Dexec.mainClass="com.campus.complaints.CampusComplaintApplication"
```

### 4.3 Default Demo Credentials
| Role | User Name | Email | Password | Preloaded Complaints |
|---|---|---|---|---|
| **Administrator** | Campus Administrator | `admin@campus.edu` | `admin123` | All complaints |
| **Student 1** | Student 1 | `student1@campus.edu` | `student123` | 2 complaints |
| **Student 2** | Student 2 | `student2@campus.edu` | `student123` | 3 complaints |

---

## 5. Verification & Test Suite Execution

Run the complete automated unit test suite with:
```bash
mvn test
```

### Test Results Summary:
- **Total Tests**: `32`
- **Failures**: `0`
- **Errors**: `0`
- **Skipped**: `0`
- **Build Status**: `BUILD SUCCESS` (100% Passing)
