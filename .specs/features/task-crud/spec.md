# Task CRUD — Specification

## Problem Statement

A Fase 1 do todoApp precisa de uma API REST funcional para gerenciamento de tarefas. A entidade `Task` já foi parcialmente implementada (domínio), mas faltam as camadas de repositório, serviço, controller, DTOs e tratamento de exceções para expor um CRUD completo via REST.

## Goals

- [ ] Expor endpoints REST para CRUD de tarefas em `/api/v1/tasks`
- [ ] Garantir validação de dados na entrada (título obrigatório, tamanho máximo)
- [ ] Gerenciar timestamps (createdAt/updatedAt) automaticamente
- [ ] Seguir Clean Architecture (Controller → Service → Repository)
- [ ] Tratar erros com respostas padronizadas

## Out of Scope

| Feature | Reason |
| ------- | ------ |
| Autenticação/Autorização | Fase futura |
| Paginação, filtros, busca | Fase futura (apenas listagem total) |
| Frontend | API only |
| Testes | Serão specs separadas |

---

## User Stories

### P1: Criar Tarefa ⭐ MVP

**User Story**: Como usuário da API, quero criar uma nova tarefa para gerenciar meus afazeres.

**Why P1**: Operação fundamental do CRUD.

**Acceptance Criteria**:

1. WHEN usuário envia POST `/api/v1/tasks` com title, description e priority válidos THEN system SHALL retornar 201 Created com os dados da tarefa criada
2. WHEN usuário envia POST sem title THEN system SHALL retornar 400 Bad Request com mensagem de erro
3. WHEN usuário envia POST com title > 255 caracteres THEN system SHALL retornar 400 Bad Request
4. WHEN tarefa é criada THEN system SHALL definir status como `TODO` e priority como `MEDIUM` (default)

---

### P1: Listar Tarefas ⭐ MVP

**User Story**: Como usuário da API, quero listar todas as tarefas para visualizar o que tenho pendente.

**Why P1**: Leitura dos dados é essencial.

**Acceptance Criteria**:

1. WHEN usuário envia GET `/api/v1/tasks` THEN system SHALL retornar 200 OK com array de tarefas
2. WHEN não há tarefas THEN system SHALL retornar 200 OK com array vazio

---

### P1: Buscar Tarefa por ID ⭐ MVP

**User Story**: Como usuário da API, quero buscar uma tarefa específica pelo ID.

**Why P1**: Operação de leitura individual.

**Acceptance Criteria**:

1. WHEN usuário envia GET `/api/v1/tasks/{id}` com ID existente THEN system SHALL retornar 200 OK com dados da tarefa
2. WHEN usuário envia GET `/api/v1/tasks/{id}` com ID inexistente THEN system SHALL retornar 404 Not Found

---

### P1: Atualizar Tarefa ⭐ MVP

**User Story**: Como usuário da API, quero atualizar campos de uma tarefa existente.

**Why P1**: Permitir edição de tarefas.

**Acceptance Criteria**:

1. WHEN usuário envia PUT `/api/v1/tasks/{id}` com campos válidos THEN system SHALL retornar 200 OK com tarefa atualizada
2. WHEN usuário envia PUT com ID inexistente THEN system SHALL retornar 404 Not Found
3. WHEN usuário envia PUT com título vazio THEN system SHALL retornar 400 Bad Request
4. WHEN tarefa é atualizada THEN system SHALL atualizar updatedAt

---

### P1: Deletar Tarefa ⭐ MVP

**User Story**: Como usuário da API, quero remover uma tarefa que não é mais relevante.

**Why P1**: Operação de deleção completa o CRUD.

**Acceptance Criteria**:

1. WHEN usuário envia DELETE `/api/v1/tasks/{id}` com ID existente THEN system SHALL retornar 204 No Content
2. WHEN usuário envia DELETE `/api/v1/tasks/{id}` com ID inexistente THEN system SHALL retornar 404 Not Found

---

## Edge Cases

- WHEN usuário envia priority inválida THEN system SHALL usar `MEDIUM` como fallback (validação branda)
- WHEN usuário envia status inválido THEN system SHALL usar `TODO` como fallback
- WHEN usuário envia campos extras no body THEN system SHALL ignorá-los silenciosamente
- WHEN usuário envia title com espaços THEN system SHALL fazer trim antes de salvar

---

## Requirement Traceability

| Requirement ID | Story | Phase | Status |
| -------------- | ----- | ----- | ------ |
| TASK-01 | P1: Criar Tarefa | Execute | Verified |
| TASK-02 | P1: Criar Tarefa | Execute | Verified |
| TASK-03 | P1: Criar Tarefa | Execute | Verified |
| TASK-04 | P1: Criar Tarefa | Execute | Verified |
| TASK-05 | P1: Listar Tarefas | Execute | Verified |
| TASK-06 | P1: Listar Tarefas | Execute | Verified |
| TASK-07 | P1: Buscar Tarefa por ID | Execute | Verified |
| TASK-08 | P1: Buscar Tarefa por ID | Execute | Verified |
| TASK-09 | P1: Atualizar Tarefa | Execute | Verified |
| TASK-10 | P1: Atualizar Tarefa | Execute | Verified |
| TASK-11 | P1: Atualizar Tarefa | Execute | Verified |
| TASK-12 | P1: Atualizar Tarefa | Execute | Verified |
| TASK-13 | P1: Deletar Tarefa | Execute | Verified |
| TASK-14 | P1: Deletar Tarefa | Execute | Verified |
| TASK-15 | Edge Cases | Execute | Verified |

**Coverage:** 15 total, 15 verified ✅

---

## Success Criteria

- [x] Todos os endpoints CRUD respondem conforme acceptance criteria
- [x] Respostas de erro padronizadas com status, mensagem e timestamp
- [x] Build do Maven compila sem erros
- [x] 24 testes passando (13 unitários + 10 controller + 1 context)
