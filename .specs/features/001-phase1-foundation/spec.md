# Phase 1 — Foundation: Task CRUD API

## Problem Statement

The todoApp needs a solid foundation: a RESTful API for managing tasks. This is the core domain entity that all future phases will build upon (Kafka events, Redis caching, metrics, containerization). Without a working, tested CRUD API, nothing else can proceed.

## Goals

- [ ] Complete Task CRUD with validated inputs and proper error handling
- [ ] All endpoints tested at unit and integration level (>80% coverage)
- [ ] Clean Architecture with clear separation of concerns
- [ ] Swagger/OpenAPI documentation accessible and correct
- [ ] PostgreSQL as the production database, H2 for dev convenience, Testcontainers for integration tests

## Out of Scope

| Feature | Reason |
|---------|--------|
| User authentication | Will be added in a future phase |
| Frontend UI | API only, consumed via HTTP clients |
| Docker deployment | Phase 2 |
| Kafka events | Phase 3 |
| Redis caching | Phase 4 |
| Pagination | Simplify v1 — can be added later |
| Sorting/Filtering | Simplify v1 — can be added later |

---

## User Stories

### P1: Create a Task ⭐ MVP

**User Story**: As an API consumer, I want to create a new task so that I can track work items.

**Why P1**: Core data entry point. Without creation, nothing exists.

**Acceptance Criteria**:

1. WHEN I POST to `/api/v1/tasks` with valid JSON body `{title, description, priority, status}` THEN system SHALL create a task and return 201 with the created task including id and timestamps
2. WHEN I POST with missing or blank `title` THEN system SHALL return 400 with validation error message
3. WHEN I POST with invalid `priority` or `status` value THEN system SHALL return 400 with validation error message
4. WHEN task is created THEN system SHALL set `createdAt` and `updatedAt` automatically

**Independent Test**: Can demo by POSTing a task via curl/Swagger and verifying 201 response with persisted data.

---

### P1: Read a Task ⭐ MVP

**User Story**: As an API consumer, I want to retrieve a task by ID so that I can view its details.

**Why P1**: Read is fundamental to any data API.

**Acceptance Criteria**:

1. WHEN I GET `/api/v1/tasks/{id}` with a valid existing ID THEN system SHALL return 200 with the task
2. WHEN I GET `/api/v1/tasks/{id}` with a non-existent ID THEN system SHALL return 404 with standardized error body

**Independent Test**: Create a task, then GET it by ID.

---

### P1: List All Tasks ⭐ MVP

**User Story**: As an API consumer, I want to list all tasks so that I can see everything at once.

**Why P1**: Essential for any task management.

**Acceptance Criteria**:

1. WHEN I GET `/api/v1/tasks` THEN system SHALL return 200 with an array of all tasks
2. WHEN there are no tasks THEN system SHALL return 200 with an empty array

**Independent Test**: Create multiple tasks, then GET `/api/v1/tasks` and verify all appear.

---

### P1: Update a Task ⭐ MVP

**User Story**: As an API consumer, I want to update an existing task so that I can keep information current.

**Why P1**: Tasks change — status, description, priority all evolve.

**Acceptance Criteria**:

1. WHEN I PUT to `/api/v1/tasks/{id}` with a complete valid body THEN system SHALL update the task and return 200 with the updated entity
2. WHEN I PUT to a non-existent ID THEN system SHALL return 404
3. WHEN I PUT with invalid data THEN system SHALL return 400
4. WHEN task is updated THEN `updatedAt` SHALL be refreshed

**Independent Test**: Create a task, PUT new values, GET it and verify changes.

---

### P1: Delete a Task ⭐ MVP

**User Story**: As an API consumer, I want to delete a task so that I can remove completed or irrelevant items.

**Why P1**: CRUD requires Delete.

**Acceptance Criteria**:

1. WHEN I DELETE `/api/v1/tasks/{id}` with an existing ID THEN system SHALL return 204 with no body
2. WHEN I DELETE a non-existent ID THEN system SHALL return 404
3. WHEN I GET a deleted task THEN system SHALL return 404

**Independent Test**: Create a task, DELETE it, GET it — should 404.

---

### P2: API Documentation

**User Story**: As a developer, I want Swagger/OpenAPI documentation so that I can explore and test the API.

**Why P2**: Documentation is essential but the API works without it. Can be added right after CRUD.

**Acceptance Criteria**:

1. WHEN I GET `/swagger-ui/index.html` THEN system SHALL display interactive Swagger UI
2. WHEN I GET `/v3/api-docs` THEN system SHALL return OpenAPI 3.0 JSON spec
3. All 5 endpoints SHALL be documented with request/response schemas

**Independent Test**: Start the app, open Swagger UI in browser, execute all endpoints.

---

## Edge Cases

- WHEN title is exactly 1 character THEN system SHALL accept it (minimum length: 1)
- WHEN title exceeds 255 characters THEN system SHALL return 400
- WHEN description is null/empty THEN system SHALL accept it (description is optional)
- WHEN request body contains unknown JSON fields THEN system SHALL ignore them (Spring default)
- WHEN multiple concurrent updates to the same task THEN last write wins (no optimistic locking in v1)
- WHEN database is unavailable THEN system SHALL return 503 with a meaningful error

---

## Requirement Traceability

| Requirement ID | Story | Phase | Status |
|----------------|-------|-------|--------|
| TASK-01 | P1: Create Task | Specify | Pending |
| TASK-02 | P1: Read Task | Specify | Pending |
| TASK-03 | P1: List Tasks | Specify | Pending |
| TASK-04 | P1: Update Task | Specify | Pending |
| TASK-05 | P1: Delete Task | Specify | Pending |
| TASK-06 | P2: API Documentation | Specify | Pending |
| TASK-07 | P1: Validation & Error Handling | Specify | Pending |
| TASK-08 | P1: Database Integration | Specify | Pending |

**Coverage:** 8 total, 8 mapped to tasks, 0 unmapped

---

## Success Criteria

- [ ] All 5 CRUD endpoints respond correctly per acceptance criteria
- [ ] Test coverage >80% (unit + integration)
- [ ] Build passes: `mvn clean verify` with zero failures
- [ ] Clean Architecture layers are correctly separated (no infrastructure in domain)
- [ ] Swagger UI is accessible and documents all endpoints
