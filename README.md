# TaskFlow API

A task-management REST API built with **Spring Boot 3**, **Spring Security (JWT)** and **Spring Data JPA**.
Each user registers, logs in, and manages only their own tasks.

## Features
- Register / login with BCrypt-hashed passwords and stateless **JWT** authentication
- Task CRUD with **status** (`TODO`, `IN_PROGRESS`, `DONE`), **priority** and due date
- **Pagination, sorting and filtering** by status
- Ownership checks: users can never read or modify someone else's tasks
- Bean Validation + a global exception handler returning RFC 7807 `ProblemDetail` responses
- OpenAPI / **Swagger UI** at `/swagger-ui.html`
- Unit tests (JUnit 5 + Mockito) and integration tests (`@SpringBootTest` + MockMvc)
- Docker + docker-compose (PostgreSQL)

## Tech stack
Java 17 · Spring Boot 3.3 · Spring Security · Spring Data JPA · H2 (dev) / PostgreSQL (prod) · jjwt · Maven · Docker

## Run locally (H2 in-memory database)
```bash
mvn spring-boot:run
```
Open http://localhost:8080/swagger-ui.html

## Run with Docker + PostgreSQL
```bash
export JWT_SECRET=$(openssl rand -base64 48)
docker compose up --build
```

## Run the tests
```bash
mvn test
```

## API overview
| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| POST | `/api/auth/register` | no | Create account, returns JWT |
| POST | `/api/auth/login` | no | Log in, returns JWT |
| POST | `/api/tasks` | yes | Create a task |
| GET | `/api/tasks?status=TODO&page=0&size=10` | yes | List my tasks (paged) |
| GET | `/api/tasks/{id}` | yes | Get one task |
| PUT | `/api/tasks/{id}` | yes | Update a task |
| PATCH | `/api/tasks/{id}/status` | yes | Change status |
| DELETE | `/api/tasks/{id}` | yes | Delete a task |

### Example
```bash
TOKEN=$(curl -s -X POST localhost:8080/api/auth/register \
  -H 'Content-Type: application/json' \
  -d '{"username":"shrouk","email":"shrouk@example.com","password":"password123"}' | jq -r .token)

curl -X POST localhost:8080/api/tasks \
  -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' \
  -d '{"title":"Finish README","priority":"HIGH"}'
```

## Project structure
```
src/main/java/com/shrouk/taskflow
├── auth/        registration + login
├── config/      Spring Security configuration
├── security/    JWT service and request filter
├── task/        entity, repository, service, controller, DTOs
├── user/        user entity and repository
└── exception/   custom exceptions + global handler
```

## Ideas for next steps
Refresh tokens, roles (admin), task comments/labels, Flyway migrations, GitHub Actions CI, Testcontainers.
