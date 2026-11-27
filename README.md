# Employee Management System

[![CI](https://github.com/swarajsalve042/Employee-Management-System/actions/workflows/ci.yml/badge.svg)](https://github.com/swarajsalve042/Employee-Management-System/actions/workflows/ci.yml)

A Java 17 and Spring Boot REST API for managing employee records with Spring Data JPA and an H2 in-memory database.

## Features

- Create, list and retrieve employee records.
- Update employee details and delete records by ID.
- Validate employee names (1–100 characters), email addresses and department length (up to 80 characters).
- Normalize names and departments by trimming whitespace; store email addresses in lowercase.
- Return HTTP 400 for invalid input and HTTP 404 for missing employee records.

## Requirements

- Java 17
- Maven 3.9+

## Run locally

```bash
mvn spring-boot:run
```

Create an employee:

```bash
curl -X POST http://localhost:8080/api/employees \
  -H "Content-Type: application/json" \
  -d '{"name":"Asha Patil","department":"IT","email":"asha@example.test"}'
```

Update an employee (replace `1` with its ID):

```bash
curl -X PUT http://localhost:8080/api/employees/1 \
  -H "Content-Type: application/json" \
  -d '{"name":"Asha Patil","department":"Platform","email":"asha@example.test"}'
```

## API endpoints

| Method | Endpoint | Purpose |
|---|---|---|
| GET | `/api/employees` | List employees |
| GET | `/api/employees/{id}` | Retrieve one employee |
| POST | `/api/employees` | Create an employee |
| PUT | `/api/employees/{id}` | Update an employee |
| DELETE | `/api/employees/{id}` | Delete an employee |

## Tests

Run the integration test suite:

```bash
mvn --batch-mode test
```

Tests use Spring Boot, MockMvc and H2 to cover create, list, retrieve, update and delete operations, required fields, email validation, normalization, invalid updates and missing records. GitHub Actions runs Maven tests on pushes and pull requests to `main`.

## Scope and limitations

This is an employee CRUD learning project. It does not currently implement login, authentication, role-based authorization, a frontend, audit logging or production database deployment. H2 is in-memory, so records are not retained after application restart. Do not use this demo to store real employee personal data.
