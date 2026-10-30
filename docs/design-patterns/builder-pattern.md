# Builder Design Pattern

## 1. Pattern Name & Category
- **Pattern**: Builder Pattern
- **GoF Category**: Creational Pattern
- **Dedicated Package**: `com.campus.complaints.designpatterns.builder`

## 2. Problem Solved
A `Complaint` in a campus maintenance ecosystem has multiple mandatory attributes (`title`, `description`, `category`, `location`, `priority`, `reporterId`) and numerous optional lifecycle attributes (`id`, `status`, `assignedStaffId`, `resolutionNotes`, `createdAt`, `updatedAt`, `resolvedAt`). Constructing such an object with telescoping constructors creates error-prone, unreadable code.

The Builder Pattern encapsulates object construction, validates all mandatory fields before object instantiation, and provides a clean fluent API.

## 3. Classes Implementing the Pattern
- [`ComplaintBuilder.java`](file:///d:/DP_mini_project/src/main/java/com/campus/complaints/designpatterns/builder/ComplaintBuilder.java): The concrete builder providing fluent setter methods and validation logic in `build()`.
- [`Complaint.java`](file:///d:/DP_mini_project/src/main/java/com/campus/complaints/model/Complaint.java): The product being constructed.

## 4. Responsibility of Each Class
- **`ComplaintBuilder`**: Holds intermediate state during construction. Validates that `title`, `description`, `category`, `location`, `priority`, and `reporterId` are non-empty and valid. Instantiates `Complaint` upon calling `build()`.
- **`Complaint`**: Pure domain entity storing maintenance issue state.

## 5. Where the Pattern Is Used in the Application
- **Complaint Submission Flow**: Called in `ComplaintService.submitComplaint()` when a student submits a new complaint via `ComplaintFormView`.
- **DAO Row Mapping**: Used in `ComplaintDAOImpl.mapRow()` when reconstructing complaints from JDBC `ResultSet` records.

## 6. Implementation Code Snippet
```java
Complaint complaint = new ComplaintBuilder()
        .title("Projector Not Working")
        .description("Lab 3 projector lamp is flickering and shutting down.")
        .category(ComplaintCategory.EQUIPMENT)
        .location("Block B, Room 302")
        .priority(Priority.HIGH)
        .reporterId(studentUser.getId())
        .build();
```

## 7. Step-by-Step Demonstration Procedure
1. Launch the application via `CampusComplaintApplication`.
2. Log in as a student (`alice@student.edu` / `student123`).
3. Click **Submit Complaint** in the sidebar.
4. Fill in Title, Category, Priority, Location, and Description.
5. Click **Submit Complaint**.
6. The controller collects field inputs and invokes `ComplaintService.submitComplaint(...)`, which runs `ComplaintBuilder.build()` and validates the data before persisting.

## 8. Viva Q&A Guide
- **Q: Why use Builder instead of constructor or JavaBeans setters?**
  *A:* Telescoping constructors are unreadable and prone to parameter transposition errors. JavaBeans setters allow partially constructed, inconsistent objects without mandatory field enforcement. Builder guarantees immutability/consistency before the object is returned.
- **Q: Where is validation performed in the Builder?**
  *A:* Inside `ComplaintBuilder.build()`, ensuring the product cannot be constructed in an invalid state.
