# Template Method Pattern — Viva Demonstration Guide

## 1. Pattern Name
**Template Method Pattern** (GoF Behavioral Design Pattern)

## 2. Problem Solved by the Pattern
Resolving a complaint requires a mandatory sequence of steps:
1. Validate complaint state (cannot be already resolved or cancelled).
2. Confirm staff assignment.
3. Transition status to IN_PROGRESS.
4. Execute specialized resolution validation (IT vs. General Maintenance).
5. Mark as RESOLVED and timestamp.
6. Notify stakeholders and record audit log.

Subclasses should not change this mandatory execution order. The Template Method pattern declares the master process algorithm as `final` and allows subclasses to override hook methods for category-specific validation.

## 3. Classes Implementing the Pattern
- `com.campus.complaints.designpatterns.template.ComplaintWorkflow`: Abstract base class with `final void processComplaint(...)`.
- `com.campus.complaints.designpatterns.template.GeneralMaintenanceWorkflow`: Subclass enforcing physical repair notes.
- `com.campus.complaints.designpatterns.template.ITSupportWorkflow`: Subclass enforcing technical diagnostic notes.
- `com.campus.complaints.designpatterns.template.WorkflowException`: Custom exception for workflow validation failures.

## 4. Responsibility of Each Class
- **`ComplaintWorkflow`**: Defines the invariant template method `processComplaint()` and common steps.
- **`GeneralMaintenanceWorkflow`**: Implements `executeResolution()` requiring notes detailing physical repair work.
- **`ITSupportWorkflow`**: Implements `executeResolution()` requiring technical diagnostic notes (VLAN, patch cord, switch reboot, driver update).

## 5. Where the Pattern is Used in the Application
- `com.campus.complaints.service.ComplaintService#resolveComplaint(...)`: Selects the appropriate workflow based on `complaint.getCategory()` and executes the template method.
- `com.campus.complaints.mvc.controller.ComplaintController#handleResolve()`: Prompts the admin for resolution notes and delegates to the workflow.

## 6. Code Example from Implementation
```java
// Abstract base class:
public abstract class ComplaintWorkflow {
    // Template Method is FINAL — algorithm structure cannot be modified
    public final void processComplaint(Complaint complaint, String notes, int userId)
            throws WorkflowException {
        validateComplaint(complaint);
        confirmAssignment(complaint);
        setInProgress(complaint);
        executeResolution(complaint, notes); // Subclass Hook
        markResolved(complaint, notes);
        recordAndNotify(complaint, userId);  // Subclass Hook
    }
    protected abstract void executeResolution(Complaint complaint, String notes)
            throws WorkflowException;
}
```

## 7. Step-by-Step Viva Demonstration
1. Open [`ComplaintWorkflow.java`](file:///d:/DP_mini_project/src/main/java/com/campus/complaints/designpatterns/template/ComplaintWorkflow.java) and show the `final void processComplaint(...)` method.
2. Open [`ITSupportWorkflow.java`](file:///d:/DP_mini_project/src/main/java/com/campus/complaints/designpatterns/template/ITSupportWorkflow.java) and [`GeneralMaintenanceWorkflow.java`](file:///d:/DP_mini_project/src/main/java/com/campus/complaints/designpatterns/template/GeneralMaintenanceWorkflow.java) to show the specialized hook implementations.
3. In the UI (logged in as Admin), attempt to resolve an unassigned complaint. Show that it fails validation at Step 2 (*"No staff assigned"*).
4. Attempt to resolve with empty resolution notes. Show that Step 4 enforces descriptive notes before resolving.

## 8. Viva Quick Answer (Summary for Professor)
> *"The Template Method Pattern defines the skeleton of the complaint resolution workflow in an abstract class, declaring the template method `final` to guarantee the mandatory sequence of execution. Specialized subclasses (`ITSupportWorkflow`, `GeneralMaintenanceWorkflow`) override hook methods to provide category-specific validation rules without altering the overall lifecycle algorithm."*
