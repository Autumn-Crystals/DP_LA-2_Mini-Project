# Prototype Design Pattern

## 1. Pattern Name & Category
- **Pattern**: Prototype Pattern
- **GoF Category**: Creational Pattern
- **Dedicated Package**: `com.campus.complaints.designpatterns.prototype`

## 2. Problem Solved
On a campus, identical maintenance problems frequently reoccur across different rooms, labs, or blocks (e.g., "Air Conditioner Not Cooling", "Wi-Fi Router Unreachable"). Requiring students to retype repetitive issue reports from scratch causes friction.

The Prototype Pattern enables cloning an existing complaint prototype, automatically producing an independent copy while resetting lifecycle state (`id=0`, `status=PENDING`, `assignedStaffId=null`, `resolvedAt=null`) and appending `(Copy)` to the title.

## 3. Classes Implementing the Pattern
- [`ComplaintPrototype.java`](file:///d:/DP_mini_project/src/main/java/com/campus/complaints/designpatterns/prototype/ComplaintPrototype.java): Prototype interface declaring `deepCopy()`.
- [`Complaint.java`](file:///d:/DP_mini_project/src/main/java/com/campus/complaints/model/Complaint.java): Concrete prototype implementing `deepCopy()`.
- [`ComplaintCloner.java`](file:///d:/DP_mini_project/src/main/java/com/campus/complaints/designpatterns/prototype/ComplaintCloner.java): Client utility that manages the duplication workflow and ensures independent object identity.

## 4. Responsibility of Each Class
- **`ComplaintPrototype`**: Declares the cloning contract.
- **`Complaint`**: Implements deep copying of complaint properties, resetting ID and lifecycle state.
- **`ComplaintCloner`**: Coordinates cloning and optional location adjustment.

## 5. Where the Pattern Is Used in the Application
- **Duplicate Action**: In `ComplaintManagementView` and `ComplaintDetailsView`, clicking the **Duplicate** button triggers `ComplaintController.duplicateComplaint()`, which clones the complaint via `ComplaintCloner.cloneComplaint()` and pre-populates `ComplaintFormView`.

## 6. Implementation Code Snippet
```java
// From Complaint.java
@Override
public Complaint deepCopy() {
    Complaint copy = new Complaint();
    copy.setId(0); // Reset ID for new database row
    copy.setTitle(this.title != null ? this.title + " (Copy)" : "New Complaint");
    copy.setDescription(this.description);
    copy.setCategory(this.category);
    copy.setLocation(this.location);
    copy.setPriority(this.priority);
    copy.setReporterId(this.reporterId);
    copy.setStatus(ComplaintStatus.PENDING); // Reset lifecycle
    copy.setAssignedStaffId(null);
    copy.setResolutionNotes(null);
    copy.setResolvedAt(null);
    copy.setCreatedAt(LocalDateTime.now());
    copy.setUpdatedAt(LocalDateTime.now());
    return copy;
}
```

## 7. Step-by-Step Demonstration Procedure
1. Open the application.
2. In the Complaints table, find an existing complaint (e.g., #1 "Broken Projector in Lab 3").
3. Click the **Duplicate** icon in the Actions column.
4. The system clones the complaint using the Prototype pattern.
5. The submission form opens pre-filled with the title `"Broken Projector in Lab 3 (Copy)"`, identical category and priority.
6. Simply modify the location to `"Lab 4"` and submit.

## 8. Viva Q&A Guide
- **Q: Why not use Java's `Object.clone()`?**
  *A:* Java's built-in `Cloneable` performs a shallow copy by default, does not invoke constructors, and throws `CloneNotSupportedException`. Implementing a dedicated `ComplaintPrototype` interface provides type-safe, explicit deep copying and custom lifecycle reset logic.
