# Memento Design Pattern

## 1. Pattern Name & Category
- **Pattern**: Memento Pattern
- **GoF Category**: Behavioral Pattern
- **Dedicated Package**: `com.campus.complaints.designpatterns.memento`

## 2. Problem Solved
Administrators frequently make accidental status transitions, reassign complaints to the wrong staff, or edit ticket descriptions by mistake. To guarantee audit integrity and user trust, the application requires an **Undo Last Change** capability without violating encapsulation or exposing private internal representation.

The Memento Pattern captures and externalizes an object's internal state into an immutable snapshot (`ComplaintMemento`) so that the object can be restored to this exact state later.

## 3. Classes Implementing the Pattern
- [`ComplaintMemento.java`](file:///d:/DP_mini_project/src/main/java/com/campus/complaints/designpatterns/memento/ComplaintMemento.java): The Memento. An immutable value object storing complaint state (`id`, `status`, `assignedStaffId`, `description`, `resolutionNotes`, `changedByUserId`, `actionLabel`, `timestamp`).
- [`ComplaintHistory.java`](file:///d:/DP_mini_project/src/main/java/com/campus/complaints/designpatterns/memento/ComplaintHistory.java): The Caretaker. Manages a stack of mementos for undo operations.
- [`ComplaintService.java`](file:///d:/DP_mini_project/src/main/java/com/campus/complaints/service/ComplaintService.java): The Originator / Client that captures state before mutations and restores state on undo.

## 4. Responsibility of Each Class
- **`ComplaintMemento`**: Immutable snapshot containing fields necessary to restore a complaint.
- **`ComplaintHistory`**: Maintains a LIFO stack of mementos for a given complaint ID.
- **`ComplaintService`**: Automatically calls `captureMemento(...)` before executing any status change, assignment, or description modification. In `undoLastChange()`, it pops the latest memento and resets the complaint's attributes.

## 5. Where the Pattern Is Used in the Application
- In `ComplaintDetailsView`, clicking **Undo Last Change** calls `ComplaintController.handleUndo()`, which executes `complaintService.undoLastChange(...)` and persists the reverted state back to MySQL.

## 6. Implementation Code Snippet
```java
// From ComplaintService.java
public boolean undoLastChange(Complaint complaint, int changedByUserId) {
    ComplaintHistory history = historyMap.get(complaint.getId());
    if (history == null || !history.canUndo()) return false;

    ComplaintMemento memento = history.pop();

    complaint.setStatus(memento.getStatus());
    complaint.setAssignedStaffId(memento.getAssignedStaffId());
    if (memento.getDescription() != null) {
        complaint.setDescription(memento.getDescription());
    }
    complaint.setResolutionNotes(memento.getResolutionNotes());

    complaintDAO.update(complaint);
    return true;
}
```

## 7. Step-by-Step Demonstration Procedure
1. Log in as an Administrator.
2. Select any `PENDING` complaint.
3. Assign a staff member to it (status changes to `ASSIGNED`).
4. Click **Undo Last Change**.
5. A confirmation dialog appears: "Undo the last state change to complaint #X? Restores snapshot via Memento Pattern."
6. Confirm OK.
7. Notice that:
   - The status immediately reverts back to `PENDING`.
   - The assigned staff reverts back to `Not assigned`.
   - The change is persisted in the database.
   - The UI immediately refreshes via the Mediator!

## 8. Viva Q&A Guide
- **Q: Who are the Originator, Memento, and Caretaker in this implementation?**
  *A:*
  - **Originator**: The `Complaint` / `ComplaintService` which creates and restores snapshots.
  - **Memento**: `ComplaintMemento`, an immutable snapshot object.
  - **Caretaker**: `ComplaintHistory`, which holds the history stack without inspecting or modifying memento contents.
