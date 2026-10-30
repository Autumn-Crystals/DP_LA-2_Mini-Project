# Design Pattern Lab Viva Examination Guide

This guide is specifically crafted for demonstrating the **Smart Campus Complaint & Maintenance System** to an academic evaluator or lab professor.

---

## 1. Quick Pattern Locator Table

| Pattern | Category | Dedicated Package Directory | Key Files to Open |
|---|---|---|---|
| **Builder** | Creational | `com.campus.complaints.designpatterns.builder` | [`ComplaintBuilder.java`](file:///d:/DP_mini_project/src/main/java/com/campus/complaints/designpatterns/builder/ComplaintBuilder.java) |
| **Prototype** | Creational | `com.campus.complaints.designpatterns.prototype` | [`ComplaintPrototype.java`](file:///d:/DP_mini_project/src/main/java/com/campus/complaints/designpatterns/prototype/ComplaintPrototype.java), [`ComplaintCloner.java`](file:///d:/DP_mini_project/src/main/java/com/campus/complaints/designpatterns/prototype/ComplaintCloner.java) |
| **Mediator** | Behavioral | `com.campus.complaints.designpatterns.mediator` | [`ComplaintMediator.java`](file:///d:/DP_mini_project/src/main/java/com/campus/complaints/designpatterns/mediator/ComplaintMediator.java), [`ComplaintMediatorImpl.java`](file:///d:/DP_mini_project/src/main/java/com/campus/complaints/designpatterns/mediator/ComplaintMediatorImpl.java) |
| **Template Method** | Behavioral | `com.campus.complaints.designpatterns.template` | [`ComplaintWorkflow.java`](file:///d:/DP_mini_project/src/main/java/com/campus/complaints/designpatterns/template/ComplaintWorkflow.java), [`ITSupportWorkflow.java`](file:///d:/DP_mini_project/src/main/java/com/campus/complaints/designpatterns/template/ITSupportWorkflow.java) |
| **Strategy** | Behavioral | `com.campus.complaints.designpatterns.strategy` | [`StaffAssignmentStrategy.java`](file:///d:/DP_mini_project/src/main/java/com/campus/complaints/designpatterns/strategy/StaffAssignmentStrategy.java), [`LeastLoadedStaffStrategy.java`](file:///d:/DP_mini_project/src/main/java/com/campus/complaints/designpatterns/strategy/LeastLoadedStaffStrategy.java), [`SkillBasedAssignmentStrategy.java`](file:///d:/DP_mini_project/src/main/java/com/campus/complaints/designpatterns/strategy/SkillBasedAssignmentStrategy.java) |
| **Memento** | Behavioral | `com.campus.complaints.designpatterns.memento` | [`ComplaintMemento.java`](file:///d:/DP_mini_project/src/main/java/com/campus/complaints/designpatterns/memento/ComplaintMemento.java), [`ComplaintHistory.java`](file:///d:/DP_mini_project/src/main/java/com/campus/complaints/designpatterns/memento/ComplaintHistory.java) |

---

## 2. Professor Viva Questions & Answers

### Q1: "Show me your Builder pattern implementation and explain why you used it."
- **Files to open**: `src/main/java/com/campus/complaints/designpatterns/builder/ComplaintBuilder.java`
- **Code to highlight**:
  - The fluent chaining methods (`title()`, `category()`, `location()`, etc.).
  - The `build()` method where mandatory field validation occurs.
- **Viva Answer**:
  > *"Professor, a campus complaint object has numerous mandatory parameters (title, description, location, category, priority, reporterId) alongside optional lifecycle fields (assignedStaffId, resolutionNotes, status). Rather than writing telescoping constructors or using setters that could leave the object in an invalid state, our `ComplaintBuilder` validates all constraints before creating the instance. In `ComplaintService.submitComplaint()`, we invoke this builder to construct the complaint safely."*

---

### Q2: "Show me your Prototype pattern. How does it ensure the clone is independent?"
- **Files to open**: `src/main/java/com/campus/complaints/designpatterns/prototype/ComplaintPrototype.java`, `ComplaintCloner.java`, `Complaint.java#deepCopy()`
- **Code to highlight**:
  - `deepCopy()` method inside `Complaint.java`.
  - Notice `copy.setId(0)` and `copy.setStatus(ComplaintStatus.PENDING)`.
- **Viva Answer**:
  > *"When students report recurring campus issues—such as identical projector failures across labs—we use the Prototype pattern to clone an existing complaint. Our `Complaint.deepCopy()` allocates a new object instance, resets the database ID to 0 so it becomes an insert rather than an update, resets the lifecycle status to PENDING, clears previous staff assignments, and appends '(Copy)' to the title. Mutating the clone does not affect the original prototype."*

---

### Q3: "Explain how your Mediator pattern decouples the GUI components."
- **Files to open**: `src/main/java/com/campus/complaints/designpatterns/mediator/ComplaintMediator.java` and `ComplaintMediatorImpl.java`
- **Code to highlight**: `onComplaintSubmitted()`, `onStaffAssigned()`, `refreshDashboard()`, `refreshComplaintList()`.
- **Viva Answer**:
  > *"In a multi-panel Swing desktop application, submitting or assigning a complaint requires updating the Complaint Table, the KPI Summary Cards on the Dashboard, the Details View, and displaying a Notification Banner. If each view held direct references to all other views, we would have tight N-to-N coupling. Instead, every view communicates solely with `ComplaintMediatorImpl`. When a controller triggers `mediator.onComplaintSubmitted(complaint)`, the mediator orchestrates updating each colleague panel independently."*

---

### Q4: "Where is the Template Method pattern? Why is the template method final?"
- **Files to open**: `src/main/java/com/campus/complaints/designpatterns/template/ComplaintWorkflow.java` and `ITSupportWorkflow.java`
- **Code to highlight**: The `public final void processComplaint(...)` method in `ComplaintWorkflow.java`.
- **Viva Answer**:
  > *"Resolving complaints requires a 5-step lifecycle: eligibility verification, resolution execution, status transition, user notification, and audit logging. In `ComplaintWorkflow.java`, `processComplaint()` is marked `final` to fix this skeleton algorithm. Subclasses like `ITSupportWorkflow` and `GeneralMaintenanceWorkflow` customize specific steps—for instance, `ITSupportWorkflow` strictly validates that technical notes are non-empty and writes to the IT audit log."*

---

### Q5: "How does your Strategy pattern allow runtime algorithm swapping?"
- **Files to open**: `src/main/java/com/campus/complaints/designpatterns/strategy/StaffAssignmentStrategy.java`, `LeastLoadedStaffStrategy.java`, `SkillBasedAssignmentStrategy.java`
- **Code to highlight**: `ComplaintController.handleAutoAssign()`.
- **Viva Answer**:
  > *"When assigning complaints to campus technicians, administrators can choose among three algorithms: Least-Loaded (assigns technician with fewest tickets), Skill-Based (filters for technicians whose skill category matches the complaint), and Round-Robin. All three implement `StaffAssignmentStrategy`. In `ComplaintController`, an interactive Swing dialog allows the administrator to choose a strategy at runtime, which dynamically reconfigures `ComplaintService.setAssignmentStrategy()` without changing existing code, adhering to OCP."*

---

### Q6: "How does the Memento pattern implement Undo without violating encapsulation?"
- **Files to open**: `src/main/java/com/campus/complaints/designpatterns/memento/ComplaintMemento.java`, `ComplaintHistory.java`, and `ComplaintService.undoLastChange()`
- **Code to highlight**: The immutable record `ComplaintMemento` and the stack push/pop in `ComplaintService`.
- **Viva Answer**:
  > *"Before any status change or technician reassignment is persisted, `ComplaintService` captures an immutable `ComplaintMemento` containing the complaint's state snapshot and pushes it onto the `ComplaintHistory` caretaker stack. When the administrator clicks 'Undo Last Change', the service pops the top memento, restores the previous status, assigned staff ID, and description, and saves it to the database. The caretaker stores mementos without inspecting or mutating their private state."*
