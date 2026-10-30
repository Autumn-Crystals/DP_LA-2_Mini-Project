# Template Method Design Pattern

## 1. Pattern Name & Category
- **Pattern**: Template Method Pattern
- **GoF Category**: Behavioral Pattern
- **Dedicated Package**: `com.campus.complaints.designpatterns.template`

## 2. Problem Solved
Different categories of campus complaints require different verification, resolution, and notification protocols. For example:
- **IT / Network complaints**: Require network connectivity verification, technical port/VLAN logging, and security audit trail notifications.
- **General Maintenance (Plumbing / Electrical / Equipment)**: Require physical on-site inspection, inventory replacement checks, and standard facilities notifications.

However, the high-level resolution algorithm is invariant:
`validateEligibility() -> executeResolution() -> updateComplaintState() -> notifyParties() -> logWorkflowAudit()`

The Template Method Pattern fixes the skeleton of the algorithm in a `final` template method while delegating specific steps to category subclasses.

## 3. Classes Implementing the Pattern
- [`ComplaintWorkflow.java`](file:///d:/DP_mini_project/src/main/java/com/campus/complaints/designpatterns/template/ComplaintWorkflow.java): Abstract base class defining the `final` template method `processComplaint()`.
- [`GeneralMaintenanceWorkflow.java`](file:///d:/DP_mini_project/src/main/java/com/campus/complaints/designpatterns/template/GeneralMaintenanceWorkflow.java): Subclass customizing physical inspection and facility notes.
- [`ITSupportWorkflow.java`](file:///d:/DP_mini_project/src/main/java/com/campus/complaints/designpatterns/template/ITSupportWorkflow.java): Subclass customizing technical notes verification and IT audit logging.
- [`WorkflowException.java`](file:///d:/DP_mini_project/src/main/java/com/campus/complaints/designpatterns/template/WorkflowException.java): Checked exception thrown when validation rules are violated.

## 4. Responsibility of Each Class
- **`ComplaintWorkflow`**: Defines the invariant 5-step algorithm in `processComplaint()`.
- **`GeneralMaintenanceWorkflow`**: Implements `validateEligibility()`, `executeResolution()`, `notifyParties()`, and `logWorkflowAudit()` for physical repairs.
- **`ITSupportWorkflow`**: Enforces non-empty technical notes and logs IT audit diagnostics.

## 5. Where the Pattern Is Used in the Application
- In `ComplaintService.resolveComplaint()`, which dynamically selects the appropriate workflow subclass based on `complaint.getCategory()` and executes the template method.

## 6. Implementation Code Snippet
```java
// From ComplaintWorkflow.java
public final void processComplaint(Complaint complaint, String resolutionNotes, int changedByUserId) 
        throws WorkflowException {
    validateEligibility(complaint);
    executeResolution(complaint, resolutionNotes);
    updateComplaintState(complaint, resolutionNotes, changedByUserId);
    notifyParties(complaint);
    logWorkflowAudit(complaint, changedByUserId);
}
```

## 7. Step-by-Step Demonstration Procedure
1. Log in as an Administrator.
2. Select a `NETWORK` complaint (or submit one).
3. Open details and click **Resolve Complaint**.
4. If you leave the resolution notes blank, `ITSupportWorkflow` immediately throws a `WorkflowException` ("IT Support requires technical resolution notes").
5. Enter "Reconfigured access switch port and verified DHCP lease" and confirm.
6. The complaint is resolved according to the IT Support workflow protocol.

## 8. Viva Q&A Guide
- **Q: Why is the template method declared `final`?**
  *A:* Declaring `public final void processComplaint(...)` prevents subclasses from overriding the sequence of execution, ensuring invariant business policies cannot be bypassed.
