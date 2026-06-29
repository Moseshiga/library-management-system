# Library Management System

Spring Boot application for managing a library: books, readers, loans, returns, overdue loans, statistics, monitoring, and basic security.

## Tech Stack

* Java 21
* Spring Boot 4.1.0
* Spring WebMVC
* Spring Data JPA
* Spring Validation
* Spring Security
* Spring Actuator
* Spring Retry
* Micrometer / Prometheus
* Flyway
* H2 Database for development
* PostgreSQL for integration/production-like profiles
* Maven
* Docker Compose
* Makefile

## Implemented Features

### Books

* Create, read, update, and delete books
* Search books by title, author, and ISBN
* Filter books by publication year and availability
* Pagination and sorting for book lists
* ISBN validation: exactly 10 or 13 digits
* Unique ISBN validation
* Validation for total and available copies
* `totalCopies = 0` is allowed because a book may remain in the catalog even when all copies are destroyed, written off, or waiting for restocking
* Protection against deleting books with loan history

### Readers

* Create, read, update, and delete readers
* Email and phone validation
* Unique email validation
* Pagination and sorting for reader lists
* Registration date is assigned by the system on creation
* Protection against deleting readers with loan history

### Book Loans

* Issue a book to a reader with a specific due date
* Return a book
* View reader loan history
* View overdue loans
* Pagination and sorting for loan lists
* Automatic update of available book copies on borrow/return
* Protection against borrowing unavailable books
* Protection against returning the same loan twice
* Pessimistic locking for book borrowing to prevent concurrent borrowing of the last available copy

### Error Handling

The application uses centralized exception handling with meaningful HTTP statuses:

| Case                    |                 HTTP Status |
| ----------------------- | --------------------------: |
| Invalid request data    |           `400 Bad Request` |
| Unauthorized request    |          `401 Unauthorized` |
| Forbidden request       |             `403 Forbidden` |
| Resource not found      |             `404 Not Found` |
| Business conflict       |              `409 Conflict` |
| Unexpected server error | `500 Internal Server Error` |

### Security

The application uses basic authentication and role-based access control.

Default users:

| Username | Password   | Role    |
| -------- | ---------- | ------- |
| `admin`  | `admin123` | `ADMIN` |
| `user`   | `user123`  | `USER`  |

Typical access rules:

* Public:

    * `GET /api/books/**`
    * `GET /actuator/health`
    * `GET /actuator/info`
* Admin-only:

    * write operations for books
    * readers API
    * loans API
    * statistics API
    * sensitive actuator endpoints

CSRF is disabled because this project is implemented as a stateless REST API for API clients. If a browser-based frontend with cookie/session authentication is added later, CSRF protection should be reviewed.

### Monitoring and Metrics

Implemented monitoring features:

* Spring Boot Actuator
* Health endpoint
* Info endpoint
* Metrics endpoint
* Prometheus endpoint
* Custom database performance health indicator
* Business counters for borrowed and returned books

### Logging

Implemented logging features:

* SLF4J + Logback
* AOP-based service method logging
* Execution time logging for service methods
* Profile-specific logging configuration
* File logging and log rotation for non-development profiles
* JSON logging for production-like environments

### Profiles

The application supports multiple profiles:

| Profile | Database   | Purpose                      |
| ------- | ---------- | ---------------------------- |
| `dev`   | H2         | Local development            |
| `int`   | PostgreSQL | Integration-like environment |
| `prod`  | PostgreSQL | Production-like environment  |

## Project Structure

```text
library-management-system
├── Dockerfile
├── Makefile
├── docker-compose.yml
├── README.md
└── library-management
    ├── pom.xml
    ├── mvnw
    ├── mvnw.cmd
    └── src
        ├── main
        │   ├── java
        │   └── resources
        └── test
            └── java
```

Main application packages:

```text
controller      REST controllers
service         Business logic
repository      Data access layer
entity          JPA entities
dto             Request/response DTOs
specification   Dynamic JPA filtering
config          Security and application configuration
monitoring      Health indicators and metrics
aspect          Logging aspects
exception       Custom exceptions and global error handling
```

## Running Locally with H2

From the project root:

```bash
cd library-management
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

On Windows PowerShell:

```powershell
cd library-management
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=dev"
```

Alternative PowerShell option:

```powershell
cd library-management
$env:SPRING_PROFILES_ACTIVE="dev"
.\mvnw.cmd spring-boot:run
```

Application URL:

```text
http://localhost:8080
```

Health check:

```bash
curl http://localhost:8080/actuator/health
```

## Running Tests

From the project root:

```bash
cd library-management
./mvnw test
```

On Windows PowerShell:

```powershell
cd library-management
.\mvnw.cmd test
```

## Building the Application

From the project root:

```bash
cd library-management
./mvnw clean package
```

On Windows PowerShell:

```powershell
cd library-management
.\mvnw.cmd clean package
```

## Running with Docker Compose

Docker Compose is prepared for running the application with PostgreSQL.

From the project root:

```bash
docker compose up -d --build
```

View logs:

```bash
docker compose logs -f library-app
```

Stop containers:

```bash
docker compose down
```

Remove containers and volumes:

```bash
docker compose down -v
```

Application URL:

```text
http://localhost:8080
```

## Makefile Commands

The project includes a Makefile for quick local operations.

```bash
make help
```

Available commands:

```bash
make build
make test
make run-dev
make up
make down
make logs
make clean
make restart
```

On Windows, `make` may not be installed by default. In that case, use the Maven and Docker commands directly.

## API Endpoints

### Books API

| Method   | Endpoint            | Description                   | Access |
| -------- | ------------------- | ----------------------------- | ------ |
| `GET`    | `/api/books`        | Get all books with pagination | Public |
| `GET`    | `/api/books/{id}`   | Get book by ID                | Public |
| `POST`   | `/api/books`        | Create book                   | Admin  |
| `PUT`    | `/api/books/{id}`   | Update book                   | Admin  |
| `DELETE` | `/api/books/{id}`   | Delete book                   | Admin  |
| `GET`    | `/api/books/search` | Search/filter books           | Public |

Example search:

```http
GET /api/books/search?title=Dune&author=Herbert&isbn=1234567890&year=1965&available=true&page=0&size=10&sort=title,asc
```

### Readers API

| Method   | Endpoint            | Description                     | Access |
| -------- | ------------------- | ------------------------------- | ------ |
| `GET`    | `/api/readers`      | Get all readers with pagination | Admin  |
| `GET`    | `/api/readers/{id}` | Get reader by ID                | Admin  |
| `POST`   | `/api/readers`      | Create reader                   | Admin  |
| `PUT`    | `/api/readers/{id}` | Update reader                   | Admin  |
| `DELETE` | `/api/readers/{id}` | Delete reader                   | Admin  |

### Loans API

| Method | Endpoint                       | Description                   | Access |
| ------ | ------------------------------ | ----------------------------- | ------ |
| `GET`  | `/api/loans`                   | Get all loans with pagination | Admin  |
| `POST` | `/api/loans`                   | Issue a book                  | Admin  |
| `PUT`  | `/api/loans/{id}/return`       | Return a book                 | Admin  |
| `GET`  | `/api/loans/reader/{readerId}` | Get reader loan history       | Admin  |
| `GET`  | `/api/loans/overdue`           | Get overdue loans             | Admin  |

Example borrow request:

```json
{
  "bookId": 1,
  "readerId": 1,
  "dueDate": "2026-07-15"
}
```

### Statistics API

| Method | Endpoint          | Description            | Access |
| ------ | ----------------- | ---------------------- | ------ |
| `GET`  | `/api/statistics` | Get library statistics | Admin  |

### Actuator API

| Method | Endpoint               | Description         |
| ------ | ---------------------- | ------------------- |
| `GET`  | `/actuator/health`     | Health status       |
| `GET`  | `/actuator/info`       | Application info    |
| `GET`  | `/actuator/metrics`    | Application metrics |
| `GET`  | `/actuator/prometheus` | Prometheus metrics  |
| `GET`  | `/actuator/loggers`    | Logger levels       |

Sensitive actuator endpoints require admin authentication.

## Example Requests

Create a book:

```bash
curl -u admin:admin123 \
  -X POST http://localhost:8080/api/books \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Dune",
    "author": "Frank Herbert",
    "isbn": "1234567890",
    "publicationYear": 1965,
    "totalCopies": 5,
    "availableCopies": 5
  }'
```

Create a reader:

```bash
curl -u admin:admin123 \
  -X POST http://localhost:8080/api/readers \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Paul",
    "lastName": "Atreides",
    "email": "paul@example.com",
    "phone": "+1234567890"
  }'
```

Borrow a book:

```bash
curl -u admin:admin123 \
  -X POST http://localhost:8080/api/loans \
  -H "Content-Type: application/json" \
  -d '{
    "bookId": 1,
    "readerId": 1,
    "dueDate": "2026-07-15"
  }'
```

Return a book:

```bash
curl -u admin:admin123 \
  -X PUT http://localhost:8080/api/loans/1/return
```

Get statistics:

```bash
curl -u admin:admin123 http://localhost:8080/api/statistics
```

## Tests

The project includes basic tests for:

* application context loading
* book service logic
* reader service logic
* book loan service logic
* book controller integration/security behavior
* book loan controller integration behavior

Run all tests:

```bash
cd library-management
./mvnw test
```

On Windows:

```powershell
cd library-management
.\mvnw.cmd test
```

## Known Limitations

This is an educational project, so several production-level features are simplified:

* Authentication uses in-memory users instead of a database-backed user model
* Passwords are hardcoded for demonstration purposes
* There is no frontend
* There is no full audit subsystem with separate audit storage
* Docker requires Docker Desktop or Docker Engine to be installed locally
* Makefile usage on Windows requires `make` to be installed separately
* CSRF is disabled because the application is treated as a stateless REST API
