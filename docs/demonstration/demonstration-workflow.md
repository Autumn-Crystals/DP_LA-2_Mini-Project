# Demonstration Workflow Script

This document details the exact sequence of actions to perform during a live system demonstration.

---

## 1. Application Launch

### Launch via Maven:
```bash
mvn compile exec:java -Dexec.mainClass="com.campus.complaints.CampusComplaintApplication"
```
Or run the packaged JAR:
```bash
java -jar target/CampusComplaintSystem.jar
```

> **Note on Database**: If MySQL is running on localhost:3306, it connects seamlessly. If MySQL is offline, the system automatically detects this and launches in **In-Memory Demo Mode** with preloaded campus data and notifies via a banner!

---

## 2. Live Demo Script (Step-by-Step)

### Step 1: Student Login & Complaint Submission (Builder Pattern)
1. **Screen**: Login Screen
2. **Action**: Click "Sign In" with default student credentials (`alice@student.edu` / `student123`).
3. **Screen**: Student Dashboard
   - Highlight the 4 KPI stat cards (`Total Submitted`, `Pending`, `In Progress`, `Resolved`).
4. **Action**: Click **Submit New Complaint**.
5. **Action**: Enter:
   - *Title*: `Air Conditioner Leaking`
   - *Category*: `ELECTRICAL`
   - *Priority*: `HIGH`
   - *Location*: `Engineering Block, Lab 202`
   - *Description*: `Water dripping continuously onto electrical outlets.`
6. **Action**: Click **Submit Complaint**.
7. **Observer**: The green notification banner animates at the top: *"Complaint submitted successfully: Air Conditioner Leaking"*.
8. **Explain to Professor**: Point to `com.campus.complaints.designpatterns.builder.ComplaintBuilder` to show how this object was constructed and validated.

---

### Step 2: Duplicating a Recurring Issue (Prototype Pattern)
1. **Screen**: My Complaints Table
2. **Action**: In the Actions column for the newly submitted complaint, click the **Duplicate** icon.
3. **Observer**: The submission form automatically opens pre-filled with:
   - *Title*: `Air Conditioner Leaking (Copy)`
   - *Category*: `ELECTRICAL`
   - *Priority*: `HIGH`
4. **Action**: Change the location to `Engineering Block, Lab 203` and click Submit.
5. **Explain to Professor**: Open `com.campus.complaints.designpatterns.prototype.ComplaintCloner` to explain how `Complaint.deepCopy()` resets ID, status, and creates an independent entity.

---

### Step 3: Admin Login & Workflow Mediation (Mediator Pattern)
1. **Action**: Click **Logout** at the bottom of the sidebar.
2. **Action**: Sign in as administrator (`admin@campus.edu` / `admin123`).
3. **Screen**: Admin Dashboard
   - Notice 5 metric cards, including the newly reported pending complaints.
4. **Action**: Click **Manage Complaints** in the sidebar.
5. **Observer**: Both students' submissions are visible.

---

### Step 4: Runtime Strategy Selection (Strategy Pattern)
1. **Action**: Click **View Details** on the complaint.
2. **Action**: Click **Assign Staff**.
3. **Observer**: The Strategy Selection Dialog appears:
   - *Least-Loaded Staff*
   - *Skill-Based Assignment*
   - *Round-Robin Assignment*
4. **Action**: Select *Skill-Based Assignment* and confirm.
5. **Observer**: The system matches the `ELECTRICAL` category to a technician with electrical skills (e.g., Bob Jones) and updates the assigned technician.
6. **Explain to Professor**: Open `com.campus.complaints.designpatterns.strategy.SkillBasedAssignmentStrategy` to show the stream filter algorithm.

---

### Step 5: Resolving via Category Workflow (Template Method Pattern)
1. **Action**: On the Complaint Details screen, click **Resolve Complaint**.
2. **Action**: Enter resolution notes: `Replaced condenser pan and sealed pipe valve.`
3. **Action**: Click OK.
4. **Observer**: The status transitions to `RESOLVED` and the resolution notes are saved.
5. **Explain to Professor**: Open `com.campus.complaints.designpatterns.template.GeneralMaintenanceWorkflow` to show the invariant 5-step template method and specialized steps.

---

### Step 6: Rollback & Recovery (Memento Pattern)
1. **Action**: On the Complaint Details screen, click **Undo Last Change**.
2. **Observer**: A confirmation prompt asks to restore the previous snapshot.
3. **Action**: Confirm YES.
4. **Observer**: The complaint immediately reverts from `RESOLVED` back to `ASSIGNED`!
5. **Explain to Professor**: Open `com.campus.complaints.designpatterns.memento.ComplaintHistory` and `ComplaintMemento` to explain the caretaker stack and immutable snapshot restoration.
