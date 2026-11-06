# 🏛️ Prison Management System (PMS)

[![Java](https://img.shields.io/badge/Java-17%2B-orange.svg)](https://www.oracle.com/java/)
[![Database](https://img.shields.io/badge/Database-MySQL%20%7C%20Embedded%20H2-blue.svg)](https://www.mysql.com/)
[![UI](https://img.shields.io/badge/UI-Java%20Swing-green.svg)](https://docs.oracle.com/javase/tutorial/uiswing/)
[![Build](https://img.shields.io/badge/Build-Maven-red.svg)](https://maven.apache.org/)
[![License](https://img.shields.io/badge/License-MIT-purple.svg)](LICENSE)

A robust, full-featured desktop Prison and Inmate Management System built with **Java Swing**, **JDBC**, and **JFreeChart**. Features intelligent **AI-assisted cell allocation**, visual analytical dashboards, comprehensive prisoner lifecycle tracking, cell block management, and legal event logging.

---

## ✨ Features

- **📊 Visual Dashboard & Analytics**
  - Live capacity and occupancy percentage metrics.
  - Interactive **JFreeChart** graphs visualizing health distribution and legal event timelines.
  - Quick action buttons for registering prisoners, inspecting cells, and logging legal events.

- **🤖 AI-Assisted Cell Allocation**
  - Rule-based AI heuristic engine that analyzes crime type, behavioral record, and health condition.
  - Automatically matches inmates to optimal security levels (Minimum, Medium, Maximum) and prevents security downgrades.
  - Balances cell occupancy and segregates high-risk inmates.

- **👥 Prisoner Records & Lifecycle Management**
  - Add, search, view, and edit prisoner profiles.
  - Real-time remaining sentence calculation.
  - One-click inmate transfer between cells with automated occupancy tracking.
  - Safe release / discharge workflow that frees cell capacity.
  - Instant health record updates (Good, Fair, Poor, Critical).

- **🏢 Prison Cell Management**
  - Overview of all cell blocks (Block A, B, C), current occupancy, capacity, and security levels.
  - Add new cell units directly through the UI.

- **⚖️ Legal Events & Hearings Tracking**
  - Record court hearings, trials, appeals, parole reviews, and disciplinary actions linked to inmates.

- **⚡ Zero-Config Dual Database Architecture**
  - **MySQL Support**: Connects seamlessly to a production MySQL database using credentials in `config.properties`.
  - **Embedded Fallback**: If MySQL is not installed or offline, the system automatically initializes an embedded in-memory database with pre-populated sample data. **It works out of the box on any computer with zero setup.**

---

## 🛠️ Tech Stack & Requirements

- **Language:** Java 17+ (Compatible with Java 17, 21, and 24)
- **GUI Framework:** Java Swing
- **Database:** MySQL 8.0+ / 9.0+ or embedded H2 fallback
- **Data Access:** JDBC with PreparedStatements and transaction rollback support
- **Libraries:**
  - `mysql-connector-j` (v8.0.33)
  - `jfreechart` (v1.5.3)
  - `h2` (v2.2.224)

---

## 🚀 Quick Start

### Option 1: Direct Run (No Setup Required)

1. **Clone the repository:**
   ```bash
   git clone https://github.com/your-username/PrisonManagementSystem.git
   cd PrisonManagementSystem
   ```

2. **Run on Windows:**
   Double-click `run.bat` or run:
   ```cmd
   run.bat
   ```

3. **Run on Linux / macOS:**
   ```bash
   chmod +x run.sh
   ./run.sh
   ```

4. **Or launch via Java command:**
   ```bash
   java -cp "PrisonManagementSystem.jar;lib/*" com.prison.MainApp
   ```
   *(On Linux/macOS, replace `;` with `:`)*

---

### Option 2: Running with Apache Maven

If you have Maven installed:

```bash
# Compile and build standalone executable JAR
mvn clean package

# Run application directly with Maven
mvn exec:java
```

---

### Option 3: Opening in IntelliJ IDEA / Eclipse / VS Code

1. Open the project folder in your IDE.
2. Ensure the JDK is set to **Java 17** or newer.
3. If not using Maven auto-import, add the JAR files in the `lib/` directory as project library dependencies:
   - `lib/jfreechart-1.5.3.jar`
   - `lib/mysql-connector-j-8.0.33.jar`
   - `lib/h2-2.2.224.jar`
4. Run `com.prison.MainApp`.

---

## 🗄️ MySQL Database Setup (Optional)

By default, the application runs with full functionality using the embedded database if MySQL is not available. To connect to your local MySQL instance:

1. **Import the database schema:**
   Open MySQL Workbench or your terminal and run:
   ```bash
   mysql -u root -p < database/schema.sql
   ```

2. **Configure credentials:**
   Edit `src/main/resources/config.properties`:
   ```properties
   db.url=jdbc:mysql://localhost:3306/prison_management?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
   db.username=root
   db.password=YOUR_PASSWORD
   ```

3. Start the application. It will automatically detect and connect to your MySQL database.

---

## 🧪 Verification & Testing

A verification test suite is included in `src/test/java/com/prison/AppTest.java`. It tests database connectivity, cell queries, inmate registration, AI cell allocation, and transfer lifecycles:

```bash
# Run verification test
java -cp "target/classes;lib/*" com.prison.AppTest
```

---

## 📁 Project Structure

```
PrisonManagementSystem/
├── database/
│   └── schema.sql                # Full MySQL schema and seed data
├── lib/                          # Standalone dependency JARs
│   ├── h2-2.2.224.jar
│   ├── jfreechart-1.5.3.jar
│   └── mysql-connector-j-8.0.33.jar
├── src/
│   ├── main/
│   │   ├── java/com/prison/
│   │   │   ├── MainApp.java      # Application entry point
│   │   │   ├── config/           # DBConnection with fallback
│   │   │   ├── dao/              # PrisonerDAO, CellDAO, LegalEventDAO, StaffDAO
│   │   │   ├── model/            # Prisoner, Cell, LegalEvent, Staff
│   │   │   ├── service/          # Business logic & services
│   │   │   ├── ui/               # MainFrame, Panels, and Dialogs
│   │   │   └── utils/            # AIUtility & ChartUtility
│   │   └── resources/
│   │       ├── config.properties # Database configuration
│   │       └── schema.sql        # Auto-seeding script
│   └── test/
│       └── java/com/prison/
│           └── AppTest.java      # Automated verification tests
├── .gitignore                    # Clean Git configuration
├── pom.xml                       # Maven build descriptor
├── PrisonManagementSystem.jar    # Executable JAR
├── run.bat                       # Windows launcher
├── run.sh                        # Linux/macOS launcher
└── README.md                     # Documentation
```

---

## 📄 License

This project is open-source and available under the [MIT License](LICENSE).
