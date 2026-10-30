# Mediator Pattern — Viva Demonstration Guide

## 1. Pattern Name
**Mediator Pattern** (GoF Behavioral Design Pattern)

## 2. Problem Solved by the Pattern
In a rich Swing GUI application with multiple views (complaint submission form, complaint list table, KPI dashboard cards, details view, and notification banners), actions in one component require updates across several other components. Having views directly reference each other creates tight, circular coupling and spaghetti code. The Mediator centralizes communication so peers communicate only via the mediator.

## 3. Classes Implementing the Pattern
- `com.campus.complaints.designpatterns.mediator.ComplaintMediator`: Interface defining component coordination events.
- `com.campus.complaints.designpatterns.mediator.ComplaintMediatorImpl`: Concrete mediator maintaining references to views and orchestrating event notifications.

## 4. Responsibility of Each Class
- **`ComplaintMediator`**: Declares callback events: `onComplaintSubmitted()`, `onComplaintStatusChanged()`, `onStaffAssigned()`, `onUndoPerformed()`, `refreshDashboard()`, `showNotification()`.
- **`ComplaintMediatorImpl`**: Dispatches events to registered peers (`MainFrame`, `ComplaintManagementView`, `DashboardView`, `ComplaintDetailsView`).

## 5. Where the Pattern is Used in the Application
- `com.campus.complaints.mvc.controller.ComplaintController`: After submitting a complaint, assigning staff, or undoing a change, the controller notifies the mediator.
- The mediator updates the dashboard KPI cards, table rows, and the sliding notification banner simultaneously.

## 6. Code Example from Implementation
```java
// Controller triggers mediator event after staff assignment
mediator.onStaffAssigned(complaint, assignedStaff);

// Mediator coordinates updates across decoupled components:
@Override
public void onStaffAssigned(Complaint complaint, MaintenanceStaff staff) {
    refreshComplaintList(); // Updates table
    refreshDashboard();     // Updates KPI counters
    showNotification("Complaint assigned to: " + staff.getName(), NotificationType.SUCCESS);
}
```

## 7. Step-by-Step Viva Demonstration
1. Open [`ComplaintMediator.java`](file:///d:/DP_mini_project/src/main/java/com/campus/complaints/designpatterns/mediator/ComplaintMediator.java) and [`ComplaintMediatorImpl.java`](file:///d:/DP_mini_project/src/main/java/com/campus/complaints/designpatterns/mediator/ComplaintMediatorImpl.java).
2. Show that `ComplaintFormView` or `ComplaintDetailsView` have zero imports of each other.
3. In the UI, perform an action like auto-assigning staff or resolving a complaint.
4. Show that the top notification banner displays the result and the KPI cards update immediately without any direct view-to-view coupling.

## 8. Viva Quick Answer (Summary for Professor)
> *"The Mediator Pattern encapsulates how a set of UI components and services interact. Instead of having views hold direct references to one another, all cross-view updates (table refreshes, KPI recalculations, sliding notification banners) are coordinated through `ComplaintMediator`, achieving true loose coupling."*
