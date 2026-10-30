# Prototype Pattern — Viva Demonstration Guide

## 1. Pattern Name
**Prototype Pattern** (GoF Creational Design Pattern)

## 2. Problem Solved by the Pattern
Campus facilities frequently experience identical or recurring issues (e.g. standard projector faults across classrooms, repeating Wi-Fi drops across floor wings, plumbing leaks). Creating these from scratch requires tedious repetitive typing. The Prototype pattern allows cloning an existing complaint while automatically clearing lifecycle-specific data (IDs, staff assignment, resolution notes, and status) so the duplicate functions as a fresh independent entity.

## 3. Classes Implementing the Pattern
- `com.campus.complaints.designpatterns.prototype.ComplaintPrototype`: Interface declaring the prototype contract `deepCopy()`.
- `com.campus.complaints.designpatterns.prototype.ComplaintCloner`: Client helper coordinating duplication with custom modifications.
- `com.campus.complaints.model.Complaint`: Concrete prototype implementing `deepCopy()`.

## 4. Responsibility of Each Class
- **`ComplaintPrototype`**: Declares `Complaint deepCopy()`.
- **`Complaint`**: Overrides `deepCopy()` to clone itself into a new instance, resetting `id = 0`, `status = PENDING`, `assignedStaffId = null`, and appending `(Copy)` to the title.
- **`ComplaintCloner`**: Provides client abstraction for duplicating complaints and modifying fields like location.

## 5. Where the Pattern is Used in the Application
- `com.campus.complaints.service.ComplaintService#duplicateComplaint(int originalId)`: Fetches the source complaint and invokes `deepCopy()`.
- `com.campus.complaints.mvc.controller.ComplaintController#handleDuplicate(int originalId)`: Invoked when the user clicks the "Duplicate" button in `ComplaintDetailsView`.

## 6. Code Example from Implementation
```java
// Inside Complaint.java (implements ComplaintPrototype)
@Override
public Complaint deepCopy() {
    Complaint copy = new Complaint();
    copy.setId(0); // Reset ID: new database entity
    copy.setTitle(this.title + " (Copy)");
    copy.setDescription(this.description);
    copy.setCategory(this.category);
    copy.setLocation(this.location);
    copy.setPriority(this.priority);
    copy.setStatus(ComplaintStatus.PENDING); // Lifecycle reset
    copy.setReporterId(this.reporterId);
    copy.setAssignedStaffId(null);
    return copy;
}
```

## 7. Step-by-Step Viva Demonstration
1. Open [`ComplaintPrototype.java`](file:///d:/DP_mini_project/src/main/java/com/campus/complaints/designpatterns/prototype/ComplaintPrototype.java) and [`Complaint.java:deepCopy()`](file:///d:/DP_mini_project/src/main/java/com/campus/complaints/model/Complaint.java).
2. Show that mutating the cloned instance does not affect the original complaint.
3. In the UI, navigate to **"My Complaints"**, select any complaint, and click the **"Duplicate"** button.
4. Point out that the form opens with all fields pre-filled, `(Copy)` added to the title, and the status reset to `Pending`.

## 8. Viva Quick Answer (Summary for Professor)
> *"The Prototype Pattern enables cloning an existing Complaint without coupling code to the concrete construction process. It copies domain values while safely resetting lifecycle fields like tracking ID, status, and assigned technician to create a completely decoupled, independent duplicate."*
