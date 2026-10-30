# UML Class Diagrams

This document contains comprehensive UML class diagrams illustrating the GoF design patterns and MVC architecture.

---

## 1. Complete System Architecture & Design Patterns

```mermaid
classDiagram
    %% ==========================================
    %% DOMAIN MODELS & CREATIONAL PATTERNS
    %% ==========================================
    class Complaint {
        -int id
        -String title
        -String description
        -ComplaintCategory category
        -String location
        -Priority priority
        -ComplaintStatus status
        -int reporterId
        -Integer assignedStaffId
        -LocalDateTime createdAt
        -LocalDateTime updatedAt
        -LocalDateTime resolvedAt
        -String resolutionNotes
        +deepCopy() Complaint
        +Builder() Builder
    }

    class ComplaintBuilder {
        <<Builder>>
        -String title
        -String description
        -ComplaintCategory category
        -String location
        -Priority priority
        -int reporterId
        +title(String) Builder
        +description(String) Builder
        +category(ComplaintCategory) Builder
        +location(String) Builder
        +priority(Priority) Builder
        +reporterId(int) Builder
        +build() Complaint
    }

    Complaint +-- ComplaintBuilder

    %% ==========================================
    %% STRATEGY PATTERN
    %% ==========================================
    class StaffAssignmentStrategy {
        <<interface>>
        +getStrategyName() String
        +assign(Complaint, List~MaintenanceStaff~) Optional~MaintenanceStaff~
    }

    class LeastLoadedStaffStrategy {
        +assign(Complaint, List~MaintenanceStaff~) Optional~MaintenanceStaff~
    }

    class RoundRobinAssignmentStrategy {
        -AtomicInteger roundRobinIndex
        +assign(Complaint, List~MaintenanceStaff~) Optional~MaintenanceStaff~
    }

    class SkillBasedAssignmentStrategy {
        +assign(Complaint, List~MaintenanceStaff~) Optional~MaintenanceStaff~
    }

    StaffAssignmentStrategy <|.. LeastLoadedStaffStrategy
    StaffAssignmentStrategy <|.. RoundRobinAssignmentStrategy
    StaffAssignmentStrategy <|.. SkillBasedAssignmentStrategy

    %% ==========================================
    %% TEMPLATE METHOD PATTERN
    %% ==========================================
    class ComplaintWorkflow {
        <<abstract>>
        +processComplaint(Complaint, String, int) void
        +getWorkflowName()* String
        #validateComplaint(Complaint) void
        #confirmAssignment(Complaint) void
        #setInProgress(Complaint) void
        #executeResolution(Complaint, String)* void
        #markResolved(Complaint, String) void
        #recordAndNotify(Complaint, int) void
    }

    class GeneralMaintenanceWorkflow {
        +getWorkflowName() String
        #executeResolution(Complaint, String) void
        #recordAndNotify(Complaint, int) void
    }

    class ITSupportWorkflow {
        +getWorkflowName() String
        #validateComplaint(Complaint) void
        #executeResolution(Complaint, String) void
        #recordAndNotify(Complaint, int) void
    }

    ComplaintWorkflow <|-- GeneralMaintenanceWorkflow
    ComplaintWorkflow <|-- ITSupportWorkflow

    %% ==========================================
    %% MEMENTO PATTERN
    %% ==========================================
    class ComplaintMemento {
        <<Memento>>
        -int complaintId
        -ComplaintStatus status
        -Integer assignedStaffId
        -String description
        -String resolutionNotes
        -LocalDateTime timestamp
        -int changedByUserId
        -String actionLabel
    }

    class ComplaintHistory {
        <<Caretaker>>
        -Deque~ComplaintMemento~ history
        +push(ComplaintMemento) void
        +pop() ComplaintMemento
        +peek() ComplaintMemento
        +canUndo() boolean
        +clear() void
    }

    ComplaintHistory o-- ComplaintMemento

    %% ==========================================
    %% MEDIATOR PATTERN
    %% ==========================================
    class ComplaintMediator {
        <<interface>>
        +onComplaintSubmitted(Complaint) void
        +onComplaintStatusChanged(Complaint) void
        +onStaffAssigned(Complaint, MaintenanceStaff) void
        +onComplaintSelected(Complaint) void
        +onUndoPerformed(Complaint) void
        +showNotification(String, NotificationType) void
    }

    class ComplaintMediatorImpl {
        -MainFrame mainFrame
        -ComplaintListView complaintListView
        -AdminDashboardView adminDashboardView
        -ComplaintDetailsView detailsView
        -ComplaintService complaintService
        +setMainFrame(MainFrame) void
        +setDetailsView(ComplaintDetailsView) void
    }

    ComplaintMediator <|.. ComplaintMediatorImpl

    %% ==========================================
    %% SERVICE LAYER (CONTEXT)
    %% ==========================================
    class ComplaintService {
        -ComplaintDAO complaintDAO
        -StaffDAO staffDAO
        -ComplaintHistoryDAO historyDAO
        -StaffAssignmentStrategy assignmentStrategy
        -Map~Integer, ComplaintHistory~ historyMap
        +submitComplaint(...) Complaint
        +duplicateComplaint(int) Complaint
        +autoAssignStaff(Complaint, int) Optional~MaintenanceStaff~
        +resolveComplaint(Complaint, String, int) void
        +undoLastChange(Complaint, int) boolean
        +setAssignmentStrategy(StaffAssignmentStrategy) void
    }

    ComplaintService o-- StaffAssignmentStrategy
    ComplaintService o-- ComplaintHistory
    ComplaintService ..> ComplaintWorkflow
    ComplaintService ..> Complaint

    %% ==========================================
    %% MVC CONTROLLER & VIEW
    %% ==========================================
    class ComplaintController {
        -MainFrame mainFrame
        -ComplaintService complaintService
        -ComplaintMediator mediator
        -AuthService authService
        +handleSubmit() void
        +handleDuplicate(int) void
        +handleAutoAssign(Complaint) void
        +handleResolve(Complaint, String) void
        +handleUndo(Complaint) void
    }

    ComplaintController --> ComplaintService
    ComplaintController --> ComplaintMediator
    ComplaintMediatorImpl --> ComplaintController
```

---

## 2. Design Patterns Classification Summary

| Pattern | Gang of Four Type | Implementation Class / Interface |
|---|---|---|
| **Builder** | Creational | `com.campus.complaints.model.Complaint.Builder` |
| **Prototype** | Creational | `com.campus.complaints.model.Complaint#deepCopy()` |
| **Strategy** | Behavioral | `com.campus.complaints.strategy.StaffAssignmentStrategy` |
| **Template Method** | Behavioral | `com.campus.complaints.template.ComplaintWorkflow` |
| **Mediator** | Behavioral | `com.campus.complaints.mediator.ComplaintMediator` |
| **Memento** | Behavioral | `com.campus.complaints.memento.ComplaintMemento` |
