# Smart Campus Complaint & Maintenance System - Database Guide

## 1. Overview
The database backend is implemented in MySQL 8.x and utilizes InnoDB with foreign key integrity, ACID compliance, and indexed queries for optimal retrieval speeds.

## 2. SQL Scripts
- [`schema.sql`](file:///d:/DP_mini_project/database/schema.sql): Creates the `campus_complaint_db` database, tables (`users`, `maintenance_staff`, `staff_skills`, `complaints`, `complaint_history`), foreign keys, check constraints, and performance indexes.
- [`seed.sql`](file:///d:/DP_mini_project/database/seed.sql): Populates realistic initial campus records including administrator accounts, student accounts, maintenance staff with diverse skills, and complaints in various lifecycle states.

## 3. Database Setup Instructions
### Prerequisites
- MySQL Server 8.0+ running on `localhost:3306`

### Execution
Execute the SQL scripts using MySQL CLI:
```bash
mysql -u root -p < database/schema.sql
mysql -u root -p campus_complaint_db < database/seed.sql
```

Or execute within MySQL Workbench:
1. Open and execute `schema.sql`.
2. Open and execute `seed.sql`.

## 4. Configuration
Database credentials can be adjusted in:
1. `src/main/resources/application.properties` (or `.env` file in project root)
2. Canonical configuration class: `com.campus.complaints.config.DatabaseConfiguration`

```properties
db.url=jdbc:mysql://localhost:3306/campus_complaint_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
db.user=root
db.password=root123
```

## 5. Offline Demo Fallback
If MySQL is not running on `localhost:3306`, the application automatically falls back to an in-memory test double repository (`InMemoryDatabase`) containing identical seed data. This allows 100% of GUI interactions and all six design patterns to be demonstrated during an academic lab viva without external database configuration!
