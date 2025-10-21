<div align="center">

# 🏥 LifeSure Insurance Management System

A comprehensive web-based life insurance management system built with modern technologies to streamline insurance operations for administrators, agents, branch managers, and customers.

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.6-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)

</div>

---

##  About

LifeSure is a full-stack web application providing an integrated platform for managing life insurance operations including policy lifecycle management, multi-role user management, branch performance tracking, payment processing, and customer support. Built with Spring Boot and following MVC architecture with role-based access control.

---

## ✨ Features

- **Multi-role Authentication** - Admin, Branch Manager, Agent, and Customer portals with Spring Security
- **Branch Management** - CRUD operations, performance dashboards, and KPI tracking
- **Policy Management** - Create, assign, and track insurance policies with status workflows
- **Payment Processing** - Secure payment slip uploads and verification system
- **Customer Portal** - Profile management, policy portfolio, payment history, and feedback
- **Agent Dashboard** - Task assignments, customer management, and commission tracking
- **Admin Controls** - System-wide analytics, user management, and feedback monitoring

---

## 🛠️ Technology Stack

**Backend:** Java 17, Spring Boot 3.5.6, Spring MVC, Spring Data JPA, Spring Security, Hibernate, Lombok

**Frontend:** Thymeleaf, HTML5/CSS3, Bootstrap 5, JavaScript

**Database:** MariaDB/MySQL, H2 (development)

**Tools:** Maven, Maven Wrapper

---

## 🚀 Getting Started

### Prerequisites

- Java 17+
- Maven 3.9+ (Maven Wrapper included)
- MariaDB/MySQL Server

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/thenuladew/LifeSure-Insuarnce-Management-System.git
   cd LifeSure-Insuarnce-Management-System
   ```

2. **Configure database**
   
   Create database:
   ```sql
   CREATE DATABASE SecureLife;
   ```
   
   Update `src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:mariadb://localhost:3306/SecureLife
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   spring.jpa.hibernate.ddl-auto=update
   ```

3. **Build and run**
   ```bash
   ./mvnw spring-boot:run
   ```

4. **Access application**
   ```
   http://localhost:8080
   ```

### Key URLs
- Home: `http://localhost:8080/`
- Admin Login: `http://localhost:8080/admin-login`
- Branch Login: `http://localhost:8080/branch-login`
- Agent Login: `http://localhost:8080/agent-login.html`
- User Login: `http://localhost:8080/login`

---

## 🗄️ Database Schema

Main tables: `users`, `admins`, `agents`, `branches`, `branch_users`, `policies`, `payments`, `tasks`, `feedback`

SQL scripts included for table creation in the root directory.

---

## 👥 User Roles

- **Administrator** - Full system access, manage all users and operations
- **Branch Manager** - Manage branch operations and performance
- **Agent** - Register customers, create policies, track tasks
- **Customer** - View policies, make payments, submit feedback

---

## 📁 Project Structure

```
LifeSure-Insuarnce-Management-System/
├── src/main/
│   ├── java/com/example/lifesureinsuarncemanagementsystem/
│   │   ├── config/              # Security configuration
│   │   ├── controller/          # MVC Controllers
│   │   ├── dto/                 # Data Transfer Objects
│   │   ├── entity/              # JPA Entities
│   │   ├── model/               # Domain Models
│   │   ├── repository/          # Data Access Layer
│   │   └── service/             # Business Logic
│   └── resources/
│       ├── application.properties
│       ├── static/              # CSS, JS, Images
│       └── templates/           # Thymeleaf HTML
├── uploads/                     # File upload directory
├── pom.xml                      # Maven configuration
└── mvnw                         # Maven Wrapper
```

---

## 🧪 Testing

```bash
./mvnw test
```

---

##  Contact

**Repository:** [https://github.com/thenuladew/LifeSure-Insuarnce-Management-System](https://github.com/thenuladew/LifeSure-Insuarnce-Management-System)

---

<div align="center">

**Built with Spring Boot & Java**

</div>
