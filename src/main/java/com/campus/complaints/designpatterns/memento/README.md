# Memento Pattern — Viva Demonstration Guide

## 1. Pattern Name
**Memento Pattern** (GoF Behavioral Design Pattern)

## 2. Problem Solved by the Pattern
Administrators occasionally make mistakes when managing campus complaints (e.g. accidentally changing a status, mistakenly assigning the wrong technician, or making incorrect notes). The Memento pattern captures a snapshot of the complaint's internal state before any modification, storing it in a history stack without violating the complaint's encapsulation. The state can be restored later using the **"Undo Last Change"** feature.

## 3. Classes Implementing the Pattern
- `com.campus.complaints.designpatterns.memento.ComplaintMemento`: Immutable value object capturing a state snapshot.
- `com.campus.complaints.designpatterns.memento.ComplaintHistory`: Caretaker class managing the LIFO undo stack.
- `com.campus.complaints.dao.ComplaintHistoryDAO`: Persists snapshots as JSON to MySQL so undo stacks survive restarts.

## 4. Responsibility of Each Class
- **`ComplaintMemento`**: Stores immutable fields: `complaintId`, `status`, `assignedStaffId`, `description`, `resolutionNotes`, `timestamp`, `changedByUserId`, and `actionLabel`.
- **`ComplaintHistory`**: Caretaker managing an `ArrayDeque<ComplaintMemento>` with `push()`, `pop()`, `peek()`, and `canUndo()`. Enforces maximum stack size limit.
- **`ComplaintService`**: Originator coordinator that captures mementos before modifications and restores state upon undo requests.

## 5. Where the Pattern is Used in the Application
- `com.campus.complaints.service.ComplaintService#captureMemento(...)`: Called prior to any status change, staff assignment, or description edit.
- `com.campus.complaints.service.ComplaintService#undoLastChange(...)`: Restores the previous snapshot and saves the rolled-back state to the database.
- `com.campus.complaints.mvc.view.ComplaintDetailsView`: Exposes the **"Undo Last Change"** button.

## 6. Code Example from Implementation
```java
// 1. Snapshot captured before mutation
captureMemento(complaint, userId, "STAFF_ASSIGNMENT");
complaint.setAssignedStaffId(staffId);
complaintDAO.update(complaint);

// 2. Undo restores the exact snapshot
public boolean undoLastChange(Complaint complaint, int userId) {
    ComplaintHistory history = historyMap.get(complaint.getId());
    if (history == null || !history.canUndo()) return false;

    ComplaintMemento memento = history.pop(); // Pop top snapshot
    complaint.setStatus(memento.getStatus());
    complaint.setAssignedStaffId(memento.getAssignedStaffId());
    complaint.setDescription(memento.getDescription());
    complaint.setResolutionNotes(memento.getResolutionNotes());

    complaintDAO.update(complaint); // Persist restored state
    return true;
}
```

## 7. Step-by-Step Viva Demonstration
1. Open [`ComplaintMemento.java`](file:///d:/DP_mini_project/src/main/java/com/campus/complaints/designpatterns/memento/ComplaintMemento.java) and [`ComplaintHistory.java`](file:///d:/DP_mini_project/src/main/java/com/campus/complaints/designpatterns/memento/ComplaintHistory.java).
2. Log in as **Admin** (`admin@campus.edu` / `admin123`).
3. Open a complaint with status `Pending`.
4. Click **"Auto-Assign Staff"** (or manual assignment). The status transitions to `Assigned`.
5. Point out that the **"Undo Last Change"** button becomes active.
6. Click **"Undo Last Change"**. Show that the status reverts back to `Pending` and the assigned staff is removed.
7. Show that MySQL and the in-memory cache reflect the restored state.

## 8. Viva Quick Answer (Summary for Professor)
> *"The Memento Pattern allows capturing and restoring an object's internal state without exposing its private implementation details. Before any critical state transition or technician assignment, an immutable `ComplaintMemento` snapshot is pushed to `ComplaintHistory` (caretaker). If an administrator makes an error, the system pops the previous snapshot and rolls back the complaint state safely."*
