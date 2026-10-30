# Mediator Design Pattern

## 1. Pattern Name & Category
- **Pattern**: Mediator Pattern
- **GoF Category**: Behavioral Pattern
- **Dedicated Package**: `com.campus.complaints.designpatterns.mediator`

## 2. Problem Solved
In a rich Swing GUI application with multiple screens (MainFrame, Dashboard, Table view, Details view, Submission form), updating one screen usually requires updating others (e.g., submitting a complaint must refresh the dashboard counters, update the complaint table, and show a notification banner).

If each view maintains direct references to every other view, a tangled N-to-N coupling emerges. The Mediator Pattern reduces this to 1-to-N coupling by routing all inter-component events through a centralized coordinator.

## 3. Classes Implementing the Pattern
- [`ComplaintMediator.java`](file:///d:/DP_mini_project/src/main/java/com/campus/complaints/designpatterns/mediator/ComplaintMediator.java): Mediator interface declaring notification and lifecycle callback contracts.
- [`ComplaintMediatorImpl.java`](file:///d:/DP_mini_project/src/main/java/com/campus/complaints/designpatterns/mediator/ComplaintMediatorImpl.java): Concrete mediator holding references to views and orchestrating cross-cutting UI updates.

## 4. Responsibility of Each Class
- **`ComplaintMediator`**: Defines notification types (`SUCCESS`, `ERROR`, `WARNING`, `INFO`) and event dispatch methods: `onComplaintSubmitted`, `onComplaintStatusChanged`, `onStaffAssigned`, `onUndoPerformed`, `showNotification`.
- **`ComplaintMediatorImpl`**: Coordinates the `MainFrame`, `ComplaintManagementView`, `StudentDashboardView`, `AdminDashboardView`, and `ComplaintDetailsView`.

## 5. Where the Pattern Is Used in the Application
- When a complaint is submitted, resolved, or assigned, controllers notify `mediator.onComplaintSubmitted(complaint)` or `mediator.onComplaintStatusChanged(complaint)`. The mediator updates the dashboard counters, refreshes the table, updates the details panel, and triggers top banner alerts.

## 6. Implementation Code Snippet
```java
// From ComplaintMediatorImpl.java
@Override
public void onComplaintSubmitted(Complaint complaint) {
    log.info("Mediator: Complaint #{} submitted. Refreshing views.", complaint.getId());
    refreshComplaintList();
    refreshDashboard();
    showNotification("Complaint submitted successfully: " + complaint.getTitle(), NotificationType.SUCCESS);
}
```

## 7. Step-by-Step Demonstration Procedure
1. Log in as an Administrator (`admin@campus.edu` / `admin123`).
2. Go to **Complaints** and click **View Details** on any complaint.
3. Click **Assign Staff**.
4. The staff is assigned, and you can observe that:
   - The Details view updates the assigned staff name and status.
   - The Complaint Management table updates its status column.
   - The Top Notification Banner animates a green success message.
   - The Admin Dashboard counters update automatically.
5. All 4 views updated without direct references to each other, coordinated entirely by `ComplaintMediatorImpl`!

## 8. Viva Q&A Guide
- **Q: How does Mediator differ from Observer?**
  *A:* Observer defines a one-to-many publish-subscribe dependency between subjects and observers. Mediator centralizes complex many-to-many interactions between colleague components so that they only communicate through the mediator.
