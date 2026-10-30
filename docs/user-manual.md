# User Manual & Operator Guide

The **Smart Campus Complaint & Maintenance System** provides dedicated interfaces for Students and Administrators.

---

## 1. Demo Credentials

| Role | Email | Password | Primary Functions |
|---|---|---|---|
| **Student** | `john.doe@campus.edu` | `student123` | Submit complaints, track status, duplicate complaints |
| **Student** | `jane.smith@campus.edu` | `student123` | Submit complaints, view personal dashboard |
| **Admin** | `admin@campus.edu` | `admin123` | Auto/manual staff assignment, change status, undo actions, staff management |

---

## 2. Student Workflow

### Step 1: Login
- Launch the application and enter student email and password.
- Click **"Sign In"**. You will be directed to the **Student Dashboard**.

### Step 2: Dashboard Overview
- View your real-time statistics cards: **Pending**, **In Progress**, and **Resolved** complaints.
- Click **"Submit Complaint"** in the sidebar to open the form.

### Step 3: Submitting a Complaint (Builder Pattern)
- Fill in:
  - **Title**: Brief summary (e.g., `Wi-Fi drop in Lab 3`).
  - **Category**: Select appropriate category (e.g., `Network / Wi-Fi`).
  - **Priority**: Select priority (`Low`, `Medium`, `High`, `Critical`).
  - **Location**: Specific building/room (e.g., `Science Block, Lab 304`).
  - **Description**: Detailed explanation of the breakdown.
- Click **"Submit"**. The Mediator notifies the system and updates your complaint list.

### Step 4: Duplicate an Existing Complaint (Prototype Pattern)
- Open **"My Complaints"** from the navigation sidebar.
- Click on any complaint row to view details.
- Click **"Duplicate"**. A new complaint form opens with the category, priority, location, and description pre-filled, with `(Copy)` added to the title.
- Adjust the location or room number as desired and click **"Submit"**.

---

## 3. Administrator Workflow

### Step 1: Admin Dashboard & Analytics
- Log in with `admin@campus.edu` / `admin123`.
- View campus-wide metrics: Total Complaints, Pending Backlog, Active Assignments, and Resolved Issues.

### Step 2: Selecting Assignment Algorithm (Strategy Pattern)
- On the Admin Dashboard or Complaint Details panel, locate the **"Assignment Strategy"** selector:
  - **Least-Loaded Assignment**: Automatically assigns the technician with the fewest pending tasks.
  - **Skill-Based Assignment**: Matches the category (e.g., Electrical) to the technician's certified skills.
  - **Round-Robin Assignment**: Evenly balances assignments sequentially.

### Step 3: Auto-Assigning Staff
- Select any **Pending** complaint from the table.
- Click **"Auto-Assign Staff"**. The selected strategy determines the best staff member, changes status to **Assigned**, and notifies the view.
- Alternatively, select a specific staff member from the dropdown for manual assignment.

### Step 4: Resolving a Complaint (Template Method Pattern)
- On an assigned or in-progress complaint, click **"Resolve Complaint"**.
- A modal prompts for resolution notes.
- The **Template Method** enforces:
  - Validation check.
  - Verification that staff was assigned.
  - Specific technical note verification (for IT complaints) or physical repair note verification (for general maintenance).
  - Marking status as **Resolved** and timestamping.

### Step 5: Undo Last Modification (Memento Pattern)
- If an accidental status change or mistaken technician assignment was made, open the complaint details.
- If history exists, the **"Undo Last Change"** button is active.
- Click **"Undo"**. The system pops the previous snapshot from the Memento stack, restores the prior fields in MySQL, updates the UI, and displays an informational banner.

### Step 6: Managing Maintenance Staff
- Click **"Maintenance Staff"** in the sidebar.
- View technician availability status (`Available`, `Unavailable`, `On Leave`).
- Manage staff skills or toggle availability status as required.
