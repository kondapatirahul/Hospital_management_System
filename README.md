# CareFlow – Hospital Management System

A comprehensive, web-based Hospital Management System designed to simplify hospital operations, improve patient management, and streamline administrative workflows through a centralized platform.

CareFlow brings multiple hospital departments together in one application, providing a structured and user-friendly experience for administrators, doctors, receptionists, patients, pharmacy staff, laboratory staff, billing teams, and nursing staff.

---

## Project Overview

CareFlow is built to digitize everyday hospital activities and reduce manual administrative work. It provides dedicated dashboards and management interfaces for different hospital departments.

The application follows a centralized architecture with a Spring Boot backend, Thymeleaf-based frontend, and MySQL database.

## Key Modules

| Module | Description |
|---|---|
| Administration | Centralized hospital administration and management |
| Reception | Patient registration and reception operations |
| Doctor | Doctor dashboard and patient-related workflows |
| Patient | Patient access and healthcare-related information |
| Pharmacy | Pharmacy and medicine management |
| Laboratory | Laboratory operations and sample tracking |
| Billing & Finance | Billing, payments, and receipt management |
| Staff & Nursing | Staff management, duty rosters, and shift handovers |

## Features

- Role-based authentication and access control
- Dedicated dashboards for different hospital roles
- Centralized hospital management
- Patient and staff management
- Laboratory sample tracking
- Pharmacy management
- Billing and payment management
- Reports and analytics
- Responsive dashboard layouts
- Organized department-wise navigation
- MySQL database integration

## Technology Stack

### Backend
- Java 17
- Spring Boot 3.3.5
- Spring Security
- Spring Data JPA
- Maven

### Frontend
- HTML5
- CSS3
- JavaScript
- Thymeleaf
- Bootstrap (where used)

### Database
- MySQL

### Development Tools
- IntelliJ IDEA / Eclipse
- Visual Studio Code
- Git
- GitHub
- Apache Maven
- MySQL Workbench

## Project Structure

```text
CareFlow/
│
├── backend/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/healthcare/hospital/
│   │   │   └── resources/
│   │   └── test/
│   └── pom.xml
│
├── frontend/
│   ├── templates/
│   └── static/
│       ├── css/
│       ├── js/
│       └── images/
│
├── .gitignore
└── README.md
```

## Prerequisites

Make sure the following tools are installed:

- JDK 17
- Apache Maven 3.9+
- MySQL Server 8.0+
- Git

Verify the installations:

```bash
java -version
mvn -version
git --version
```

## Installation and Setup

### 1. Clone the Repository

```bash
git clone YOUR_GITHUB_REPOSITORY_URL
```

Navigate into the project directory:

```bash
cd CareFlow
```

### 2. Configure MySQL

Start your MySQL server and create the database:

```sql
CREATE DATABASE hospital_db;
```

### 3. Configure Environment Variables

Set the following environment variables before running the application:

| Variable | Description | Example |
|---|---|---|
| `PORT` | Application port | `8082` |
| `DB_URL` | MySQL connection URL | `jdbc:mysql://localhost:3306/hospital_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC` |
| `DB_USERNAME` | MySQL username | `root` |
| `DB_PASSWORD` | MySQL password | Your MySQL password |

**Windows PowerShell:**

```powershell
$env:PORT = "8082"
$env:DB_USERNAME = "root"
$env:DB_PASSWORD = "your_mysql_password"
```

Never commit actual database passwords or secret credentials to GitHub.

### 4. Run the Application

From the project root directory:

```bash
mvn -f backend/pom.xml spring-boot:run
```

Alternatively, build the application:

```bash
mvn -f backend/pom.xml clean package
```

### 5. Access the Application

Open your browser:

```text
http://localhost:8082
```

The application should display the CareFlow landing page.

## Testing

Run the test suite using:

```bash
mvn -f backend/pom.xml clean test
```

To build the application:

```bash
mvn -f backend/pom.xml clean package
```

## Screenshots

Add screenshots of the application here to demonstrate its interface and functionality.

Suggested screenshots:

- Landing Page
- Login Page
- Administration Dashboard
- Reception Dashboard
- Doctor Dashboard
- Patient Dashboard
- Pharmacy Dashboard
- Laboratory Dashboard
- Billing Dashboard
- Staff Dashboard

## Security

- Authentication is handled through Spring Security.
- Access to application features is controlled according to user roles.
- Database credentials should be configured through environment variables.
- Sensitive configuration files and credentials should not be committed to the repository.

## Development Workflow

This project uses Git and GitHub for version control and collaborative development.

Development workflow:

1. Create a feature branch from `main`.
2. Implement the required changes.
3. Test the affected functionality.
4. Commit the changes with meaningful messages.
5. Push the feature branch to GitHub.
6. Open a Pull Request.
7. Review and merge approved changes into `main`.

## Future Enhancements

- Advanced hospital analytics and reporting
- Improved appointment scheduling
- Automated notifications and reminders
- Enhanced patient communication
- Additional workflow automation
- Cloud deployment and monitoring

## Contribution

Contributions and suggestions are welcome.

If you want to contribute:

1. Fork the repository.
2. Create a new feature branch.
3. Make your changes.
4. Commit and push your changes.
5. Open a Pull Request with a clear description.

## License

This project is developed for educational and software development purposes.

---

**CareFlow – Simplifying Hospital Management Through Technology.**