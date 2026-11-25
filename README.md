# Employee Management System

[![CI](https://github.com/swarajsalve042/Employee-Management-System/actions/workflows/ci.yml/badge.svg)](https://github.com/swarajsalve042/Employee-Management-System/actions/workflows/ci.yml)

Java 17 and Spring Boot REST API for employee records.

## Features
- Create, list and retrieve employees.
- Update employee details.
- Delete employees by ID.
- Spring Data JPA and H2 in-memory database.
- REST endpoints under `/api/employees`.

## Requirements
Java 17 and Maven 3.9+.

## Run
`mvn spring-boot:run`

## Test
`mvn test`

Example create request:
`curl -X POST http://localhost:8080/api/employees -H "Content-Type: application/json" -d '{"name":"Asha Patil","department":"IT","email":"asha@example.test"}'`

## Scope
A CRUD learning project. Authentication, role-based access, advanced validation, and production database deployment are not included.