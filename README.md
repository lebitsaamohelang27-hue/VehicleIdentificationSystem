# 🚗 Vehicle Identification System (VIS)

**Course:** Object Oriented Programming II — B/DIOP2210  
**Faculty:** Information & Communication Technology  
**Programs:** BSc./Diploma in Information Technology | Software Engineering | Business IT  
**Weight:** 35% | Year 2 Semester 2

---

## Project Overview

The Vehicle Identification System (VIS) is a JavaFX desktop application backed by a PostgreSQL database. It assists police, workshops, insurance companies, and vehicle owners in identifying, tracking, and managing vehicle information.

---

## Technologies Used

| Layer       | Technology              |
|-------------|-------------------------|
| Frontend    | JavaFX 21               |
| Backend     | PostgreSQL 15+          |
| DB Driver   | JDBC (PostgreSQL Driver)|
| Build Tool  | Maven                   |
| Pattern     | MVC (Model-View-Controller) |
| Version Control | Git + GitHub        |

---

## Module Structure

```
com.vis
├── MainApp.java                    ← Entry point
├── model/
│   ├── Person.java                 ← Abstract base (Inheritance)
│   ├── Customer.java               ← Extends Person (Polymorphism)
│   ├── Officer.java                ← Extends Person (Polymorphism)
│   ├── Vehicle.java
│   ├── ServiceRecord.java
│   ├── PoliceReport.java
│   ├── Violation.java
│   └── Insurance.java
├── database/
│   ├── DatabaseManager.java        ← Singleton JDBC connection + schema init
│   ├── VehicleDAO.java             ← CRUD + stored procedure call
│   ├── CustomerDAO.java
│   ├── PoliceDAO.java
│   └── InsuranceDAO.java
├── controller/
│   ├── DashboardController.java    ← ScrollPane (22+ items) + Pagination
│   ├── VehicleController.java      ← TableView + CRUD + DropShadow
│   ├── CustomerController.java     ← TableView + CRUD + Polymorphism display
│   ├── PoliceController.java       ← Reports + Violations sub-tabs
│   └── InsuranceController.java    ← Insurance CRUD
└── view/
    └── MainView.java               ← MenuBar + TabPane + FadeTransition + ProgressBar
```

---

## How to Set Up

### 1. Database Setup

```bash
psql -U postgres
\i database_setup.sql
```

Then open `DatabaseManager.java` and update:
```java
private static final String PASSWORD = "your_actual_postgres_password";
```

### 2. Run with Maven

```bash
cd VehicleIdentificationSystem
mvn javafx:run
```

---

## Key Features Checklist

| Feature | Status | Where |
|---------|--------|-------|
| Menu Bar & Menu Items | ✅ | `MainView.java` |
| TableView | ✅ | All module controllers |
| Pagination | ✅ | `DashboardController.java` |
| ScrollPane (20+ items) | ✅ | `DashboardController.java` |
| ProgressBar | ✅ | `MainView.java` header |
| ProgressIndicator | ✅ | `MainView.java` header |
| DropShadow Effect | ✅ | Add buttons + title label |
| FadeTransition | ✅ | "LIVE" badge in header |
| PostgreSQL via JDBC | ✅ | `DatabaseManager.java` + DAOs |
| Exception Handling | ✅ | All controllers & DAOs |
| Inheritance | ✅ | `Person` → `Customer`, `Officer` |
| Polymorphism | ✅ | `getRole()` override + display |
| DB Views | ✅ | `vw_vehicle_details`, `vw_violation_summary` |
| Stored Procedures | ✅ | `sp_add_vehicle` |
| MVC Pattern | ✅ | Fully separated model/view/controller |
| GitHub | ✅ | Push to GitHub (link in docs) |

---

## OOP Concepts Demonstrated

### Inheritance
```
Person (abstract)
├── Customer  → getRole() returns "Customer"
└── Officer   → getRole() returns "Police Officer"
```

### Polymorphism
The `getRole()` method is declared `abstract` in `Person` and overridden in each subclass. The `CustomerController` calls `customer.getRole()` on any `Person` reference, demonstrating runtime polymorphism.

---

## GitHub Repository
> **Add your repository link here after pushing:**  
> `https://github.com/YOUR_USERNAME/VehicleIdentificationSystem`

```bash
git init
git add .
git commit -m "Initial commit: Vehicle Identification System"
git remote add origin https://github.com/YOUR_USERNAME/VehicleIdentificationSystem.git
git push -u origin main
```
"# VehicleIdentificationSystem" 
