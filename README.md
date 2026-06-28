# Library Management System API 📚

An enterprise-grade RESTful API for library management, designed with a focus on concurrency control, resilience, and observability. Built with **Java 21** and **Spring Boot 4.1.0**.

## 🚀 Key Architectural Features

* **Concurrency & Integrity:** Implemented **Pessimistic Write Locking** (`@Lock(LockModeType.PESSIMISTIC_WRITE)`) to prevent race conditions during concurrent book loans (e.g., checking out the last available copy).
* **Advanced Querying:** Utilized **JPA Specifications** for dynamic, multi-criteria filtering (by ISBN, author, year, availability) without hardcoding SQL permutations.
* **Resilience:** Integrated **Spring Retry** to handle transient database connection failures and transaction timeouts (`CannotCreateTransactionException`) seamlessly.
* **Security:** Secured endpoints via **Spring Security** using stateless HTTP Basic Auth and Role-Based Access Control (RBAC).
* **Observability & Audit:** * AOP-based execution time logging for service layer performance tracking.
* Application metrics exported via **Prometheus** (tracked custom business metrics using Micrometer `Gauge` and `Counter`).
* Custom Spring Boot Actuator `HealthIndicator` for database query performance monitoring.


* **Data Migration & Profiles:** Managed schema evolution and test data injection via **Flyway**, with strict separation between `dev` (H2) and `prod` (PostgreSQL) environments.

## 🛠️ Technology Stack

* **Backend:** Java 21, Spring Boot 4.1.0 (Web, Data JPA, Security, Validation, AOP)
* **Database:** PostgreSQL 15 (Prod), H2 Database (Dev)
* **Migrations:** Flyway
* **Monitoring:** Spring Boot Actuator, Micrometer Registry Prometheus
* **Infrastructure:** Docker, Docker Compose, Makefile
* **Testing:** JUnit 5, Mockito, Spring Boot Test, Spring Security Test

## 🚦 Quick Start (Dockerized)

The project includes a `Makefile` and `docker-compose.yml` for zero-configuration startup.

### Prerequisites

* Docker & Docker Compose
* Make (optional, but recommended)

### Run the Application

To build the application and start the PostgreSQL database cluster:

```bash
make up
```

*(This command builds the multi-stage Docker image and spins up the environment on port 8080).*

To view real-time application logs:

```bash
make logs
```

To stop and remove containers:

```bash
make down
```

## 💻 Local Development

If you prefer to run the application locally without Docker (using the in-memory H2 database with pre-populated Flyway test data):

```bash
mvn clean install
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

## 🔐 Authentication & Roles

The API uses HTTP Basic Authentication. Two default users are provisioned in memory:

| Role | Username | Password | Permissions |
| --- | --- | --- | --- |
| **ADMIN** (Librarian) | `admin` | `admin123` | Full CRUD access, Loans management, Actuator metrics |
| **USER** (Reader) | `user` | `user123` | Read-only access to catalog (`GET /api/books/**`) |

## 📡 Core API Endpoints

### Books

* `GET /api/books` - Get paginated list of books.
* `GET /api/books/search?isbn={isbn}&year={year}` - Dynamic search.
* `POST /api/books` - Add a new book (Admin).

### Readers

* `GET /api/readers` - Get paginated list of readers (Admin).
* `POST /api/readers` - Register a new reader (Admin).

### Loans

* `POST /api/loans` - Issue a book with a specific due date (Admin).
* `GET /api/loans/reader/{id}` - Get loan history for a specific reader (Admin).

### Observability

* `GET /api/statistics` - Fast business dashboard metrics (Admin).
* `GET /actuator/prometheus` - Prometheus metrics export (Admin).

## 🧪 Testing

The project is covered by both Unit and Integration tests (using `MockMvc` and `@SpringBootTest`).

```bash
make test
```