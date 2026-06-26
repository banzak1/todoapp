# STATE — todoApp

## Decisions

### DEC-001: WSL2 Ubuntu como ambiente principal (2026-06-22)
**Decisão:** Desenvolver no WSL2 Ubuntu, não no Windows nativo.
**Motivo:** Docker, Kafka, Redis e CI/CD têm melhor suporte e performance no Linux.

### DEC-002: Java 21 + Spring Boot 3 como stack base (2026-06-22)
**Decisão:** Usar Java 21 LTS e Spring Boot 3.x.
**Motivo:** Java 21 é LTS com suporte até 2029. Virtual threads, records, pattern matching.

### DEC-003: 8 fases progressivas, mesmo app (2026-06-22)
**Decisão:** Evoluir o mesmo código base em vez de criar projetos separados.
**Motivo:** Simula crescimento real de sistemas.

### DEC-004: Clean Architecture como padrão (2026-06-22)
**Decisão:** Usar Clean Architecture / Hexagonal (Ports & Adapters).
**Motivo:** Separa domínio de infraestrutura, facilita testabilidade.

### DEC-005: Vault Obsidian + CLAUDE.md como fonte de contexto (2026-06-22)
**Decisão:** Usar vault Obsidian (`notes/`) como armazenamento primário de contexto.

## Blockers

Nenhum ativo.

## Lessons Learned

Em construção.

## Preferences

- Código em inglês, documentação em português
- Conventional commits
- Testes com padrão `should[Expected]_when[Condition]`

## Active Session

**2026-06-26 — CRUD Task implementado**
- Oh My Zsh instalado no WSL2
- Spec-driven development aplicado: spec → implementação
- Feature spec criada: `.specs/features/task-crud/spec.md`
- CRUD completo implementado e compilando:
  - TaskService, TaskRepository, TaskController
  - DTOs (CreateTaskRequest, UpdateTaskRequest, TaskResponse)
  - GlobalExceptionHandler com ProblemDetail
  - Timestamps automáticos (createdAt/updatedAt)
- Próximo: testes unitários (JUnit 5 + Mockito) e testes de integração (Testcontainers)

**2026-06-26 — Testes unitários e de controller**
- TaskServiceTest: 13 testes unitários com Mockito (100% coverage do service)
- TaskControllerTest: 10 testes de integração com MockMvc
- 24 testes totais, 0 falhas
- À fazer: testes de integração com Testcontainers (+ PostgreSQL)
