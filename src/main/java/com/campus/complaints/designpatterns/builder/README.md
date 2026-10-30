# Builder Pattern — Viva Demonstration Guide

## 1. Pattern Name
**Builder Pattern** (GoF Creational Design Pattern)

## 2. Problem Solved by the Pattern
A campus `Complaint` requires mandatory parameters (title, description, category, location, priority, reporterId) and several optional lifecycle parameters (assignedStaffId, status, resolutionNotes, timestamps). Constructing this object with constructors leads to telescoping constructors, parameter ordering errors, and unvalidated initial states. The Builder pattern encapsulates the step-by-step construction and enforces domain validation before object instantiation.

## 3. Classes Implementing the Pattern
- `com.campus.complaints.designpatterns.builder.ComplaintBuilder`: Dedicated fluent builder with field validation.
- `com.campus.complaints.model.Complaint`: Target domain entity constructed by the builder.

## 4. Responsibility of Each Class
- **`ComplaintBuilder`**: Accumulates properties fluently (`title()`, `category()`, etc.), validates that mandatory fields are not blank or null in `build()`, and creates an immutable, valid `Complaint`.
- **`Complaint`**: Represents the complaint domain model with getters and state transitions.

## 5. Where the Pattern is Used in the Application
- `com.campus.complaints.service.ComplaintService#submitComplaint(...)`: The student complaint submission flow calls `ComplaintBuilder` to assemble the complaint before passing it to `ComplaintDAO`.
- `com.campus.complaints.mvc.controller.ComplaintController#handleSubmit()`: Captures form inputs from `ComplaintFormView` and triggers the builder workflow.

## 6. Code Example from Implementation
```java
Complaint complaint = new ComplaintBuilder()
        .title("Wi-Fi Router Offline")
        .description("Cannot connect to Campus_Secure in Lab 2")
        .category(ComplaintCategory.NETWORK)
        .location("Computer Lab 2, 2nd Floor")
        .priority(Priority.HIGH)
        .reporterId(currentUser.getId())
        .build(); // Validates constraints and produces Complaint
```

## 7. Step-by-Step Viva Demonstration
1. Open [`ComplaintBuilder.java`](file:///d:/DP_mini_project/src/main/java/com/campus/complaints/designpatterns/builder/ComplaintBuilder.java).
2. Show the fluent setter methods returning `this`.
3. Point out the validation logic inside `build()` that checks for empty titles, missing categories, and invalid IDs.
4. Open the running application, log in as a Student (`john.doe@campus.edu` / `student123`).
5. Open **"Submit Complaint"**, enter values, and submit. Show that leaving fields blank triggers immediate validation.

## 8. Viva Quick Answer (Summary for Professor)
> *"The Builder Pattern separates the construction of a complex Complaint object from its representation. It eliminates telescoping constructors, allows optional fields to be configured fluently, and guarantees that no invalid Complaint object can be instantiated without satisfying all business validation rules in `build()`."*
