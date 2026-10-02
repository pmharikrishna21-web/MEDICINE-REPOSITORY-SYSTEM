# Medicine Repository System

A robust, enterprise-grade Java desktop application engineered for centralized medicine information archiving, clinical specification retrieval, stock inventory surveillance, and expiry status monitoring.

Built with **Java 17 / 21**, **Java Swing**, and **MySQL 8** via **JDBC (MySQL Connector/J)** using strict **Object-Oriented Design Principles (OOP)** and clean layered architecture.

---

## 1. Project Overview

The **Medicine Repository System** addresses critical needs in healthcare facilities, clinic pharmacies, and inventory stockrooms:
- Centralized storage of pharmaceutical profiles (dosages, active generic ingredients, therapeutic uses, storage temperatures, prescription status).
- Automatic calculation of inventory health (`AVAILABLE`, `LOW STOCK (< 10 units)`, `OUT OF STOCK`).
- Expiry date vigilance using Java modern `LocalDate` and Java Time API to flag expired medications and alert items expiring within 60 days.
- Role-Based Access Control (**RBAC**) ensuring standard healthcare staff (`USER`) can query catalog data and check stocks, while inventory supervisors (`ADMIN`) maintain stock quantities, add new pharmaceutical products, update clinical specs, delete obsolete records, and audit user accounts.
- Seeded with **40 realistic pharmaceutical records** spanning 10 clinical categories.

> **Important Scope Boundary**: This system is dedicated to medicine archiving and inventory surveillance. It is **not** an online commercial pharmacy; it does not process payments, dispense online customer orders, or provide doctor consultations.

---

## 2. Technology Stack

- **Core Language**: Java 17 LTS / Java 21 LTS
- **User Interface**: Java Swing (with Nimbus Look and Feel)
- **Relational Database**: MySQL 8.0 or later
- **Connectivity**: JDBC (MySQL Connector/J `8.3.0`)
- **Build & Dependency Management**: Apache Maven (`pom.xml`)
- **Date & Time**: Modern Java Time API (`java.time.LocalDate`, `ChronoUnit`, `DateTimeFormatter`)
- **Architecture**: 3-Tier Layered Architecture (Presentation `ui` -> Service `service` -> Data Access `dao` -> Persistence `database` & Entities `model`)

---

## 3. Key Features

### For Normal Users (`USER`)
- **Secure Registration & Login**: Validated registration with role assignment.
- **Multi-Vector Search**: Case-insensitive and partial search across **Medicine Name**, **Generic Name**, **Manufacturer**, and **Category** (e.g. typing `para` instantly locates *Paracetamol / Tylenol*).
- **Category Filtering**: Dropdown filter for 10 medical categories.
- **Detailed Specifications Inspector**: Full inspection modal showing active ingredients, indications/uses, contraindications/storage, prescription mandate (`Rx Only` vs `OTC`), batch numbers, and stock status.
- **Live Stock Alerts**: Color-coded stock indicators. Expired medicines are clearly demarcated and never shown as normally available.

### For Administrators (`ADMIN`)
- **Executive Real-Time Dashboard**: Six dynamic metric cards computed directly from MySQL:
  1. *Total Medicines*
  2. *Available Medicines* (Stock ≥ 10 and unexpired)
  3. *Low Stock Alerts* (Stock 1 to 9 units)
  4. *Out of Stock* (Stock = 0 units)
  5. *Expired Medicines* (Passed expiration date)
  6. *Registered System Accounts*
- **Complete Inventory CRUD**:
  - Add medicine with validation (unique ID, valid positive prices and stocks, verified dates).
  - Update existing medicine records.
  - Delete medicine with mandatory confirmation safety dialog.
  - In-place quick stock quantity modification.
- **Surveillance Filters**: One-click quick views for *Low Stock*, *Expired Drugs*, and *Expiring Soon (Within 60 Days)*.
- **User Directory Audit**: View all registered users and admin accounts.

---

## 4. Default Demonstration Credentials

| Role | Username | Password | Full Name | Access Scope |
| :--- | :--- | :--- | :--- | :--- |
| **Administrator** | `admin` | `admin123` | System Administrator | Full Dashboard, Stock Updates, CRUD, User Audit |
| **Normal User** | `user` | `user123` | Dr. Jane Miller | Medicine Catalog Search, Filter, Details Inspection |

> **Security Note**: In a production environment, never store plain-text passwords. Store salted cryptographic hashes (e.g., Argon2id or BCrypt) and inject credentials via environment variables (`DB_URL`, `DB_USER`, `DB_PASSWORD`). Always change default passwords before deployment.

---

## 5. System Requirements & Installation

### Prerequisites
1. **Java Development Kit (JDK)**: JDK 17 or JDK 21 installed.
   - Verify with: `java -version` and `javac -version`
2. **Apache Maven**: Version 3.8+ installed.
   - Verify with: `mvn -version`
3. **MySQL Server**: Version 8.0 or later installed and running on port 3306.
   - Verify with: `mysql -u root -p`

---

## 6. Database Setup Instructions

1. Start your local MySQL server.
2. Open terminal/command prompt and navigate to the project directory:
   ```bash
   cd MedicineRepositorySystem
   ```
3. Execute the provided database setup script into MySQL:
   ```bash
   mysql -u root -p < database/medicine_repository.sql
   ```
   *Or* open `database/medicine_repository.sql` inside **MySQL Workbench**, **DBeaver**, or **phpMyAdmin** and execute the entire script.

4. Verify database creation:
   ```sql
   USE medicine_repository;
   SELECT COUNT(*) FROM medicines; -- Should return 40
   SELECT COUNT(*) FROM users;     -- Should return 2
   ```

### Database Connection Configuration
Database credentials can be adjusted in `src/main/java/database/DatabaseConnection.java` or provided at runtime via environment variables:

```bash
export DB_URL="jdbc:mysql://localhost:3306/medicine_repository?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
export DB_USER="root"
export DB_PASSWORD="your_mysql_password"
```

If no environment variables are set, defaults to:
- URL: `jdbc:mysql://localhost:3306/medicine_repository`
- User: `root`
- Password: `root`

---

## 7. How to Build and Run the Application

### 1. Compile the Maven Project
```bash
mvn clean compile
```

### 2. Run the Application via Maven Exec Plugin
```bash
mvn exec:java
```

### 3. Alternatively, Package into Runnable JAR
```bash
mvn clean package
java -jar target/medicine-repository-system-1.0.0.jar
```

### 4. Or Run Directly from IDE
Open the project in **IntelliJ IDEA**, **Eclipse**, or **VS Code (Java Pack)** and run `src/main/java/Main.java`.

---

## 8. Layered Project Architecture

```text
MedicineRepositorySystem/
│
├── pom.xml                               # Maven project configuration & dependencies
├── README.md                             # Comprehensive technical documentation
├── database/
│   └── medicine_repository.sql           # Schema definition + 40 seeded medicine records
│
└── src/
    └── main/
        └── java/
            ├── model/                    # Domain Entities & Business Rules
            │   ├── Medicine.java         # Medicine entity with stock status logic
            │   └── User.java             # User entity with roles & auth helpers
            │
            ├── database/                 # JDBC Persistence Foundation
            │   └── DatabaseConnection.java# Centralized connection factory & configuration
            │
            ├── dao/                      # Data Access Objects (Raw SQL + PreparedStatements)
            │   ├── MedicineDAO.java      # CRUD, searches, stock queries & counts
            │   └── UserDAO.java          # Authentication, registration & user listings
            │
            ├── service/                  # Business Logic Layer & Validation Coordination
            │   ├── MedicineService.java  # Medicine validations, metrics compilation
            │   └── UserService.java      # User validation & credential verification
            │
            ├── ui/                       # Java Swing Presentation Layer
            │   ├── LoginFrame.java       # User & Admin authentication window
            │   ├── RegisterFrame.java    # New account registration dialog
            │   ├── UserDashboard.java    # User catalog, search, filter & details view
            │   ├── AdminDashboard.java   # Executive dashboard with live stat metrics
            │   ├── MedicineDetailsFrame.java # Comprehensive specification inspector
            │   ├── MedicineManagementFrame.java # Admin inventory management table & actions
            │   ├── AddMedicineFrame.java # New medicine modal with input verification
            │   └── UpdateMedicineFrame.java # Edit medicine modal
            │
            ├── util/                     # Cross-Cutting Utilities
            │   ├── DateUtil.java         # LocalDate formatting, parsing & expiry calculation
            │   └── ValidationUtil.java   # Regular expressions & input data validators
            │
            └── Main.java                 # Bootstrap entry point & LookAndFeel initialization
```

---

## 9. Object-Oriented Principles & Design Highlights

1. **Encapsulation**: All models (`Medicine`, `User`) protect their state via private fields, providing controlled accessors and business methods like `getStockStatus()` and `isExpiringSoon()`.
2. **Separation of Concerns (SoC)**: Strict isolation between Presentation (`ui`), Business Rules (`service`), Data Access (`dao`), and Connection Management (`database`).
3. **Prepared Statements & SQL Injection Defense**: Every dynamic parameter is bound through JDBC `PreparedStatement` placeholders (`?`). String concatenation in SQL is strictly prohibited.
4. **Defensive Resource Management**: All JDBC connections, statements, and result sets use `try-with-resources` blocks to prevent database connection leaks.
5. **Robust Input Validation**: Strict checking for duplicate usernames/IDs, negative quantities/prices, invalid email formats, and nonsensical dates (e.g. expiry before manufacture).

---

## 10. Future Enhancement Roadmap

- **Audit Logging**: Maintain an append-only audit trail table recording who modified medicine stock or deleted a record.
- **CSV / PDF Export**: Generate inventory valuation reports and expiry logs directly to PDF or Excel.
- **Barcode / QR Scanner Support**: Interface with USB barcode scanners to look up medicine batches rapidly.
- **Supplier Relationship Management**: Track vendor contact information and automatic reorder purchase requests when stock drops below threshold.
- **Password Hashing**: Integrate BCrypt (`org.mindrot:jbcrypt`) to secure stored user passwords.
