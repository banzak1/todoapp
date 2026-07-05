# Diretrizes de Código e Convenções

## Idioma
- **Código:** Inglês (variáveis, funções, classes, comentários, commits)
- **Documentação:** Português (vault Obsidian, specs de feature)
- **Mensagens de commit:** Inglês, conventional commits (`feat:`, `fix:`, `test:`, `refactor:`, `docs:`)

## Stack e Versões
- **Java:** 21 LTS
- **Spring Boot:** 3.x
- **Build:** Maven (backend) / Angular CLI (frontend)
- **Banco:** PostgreSQL 16
- **Testes (Backend):** JUnit 5 + Mockito + Testcontainers
- **Testes (Frontend):** Vitest (Angular 21 nativo)
- **Frontend:** Angular 21+ standalone components, SCSS, Signals

## Arquitetura
- **Backend:** Clean Architecture / Hexagonal (Ports & Adapters)
- **Frontend:** Feature-based structure (`features/<nome>/pages/` + `features/<nome>/components/`)
- **Frontend Components:** CADA componente DEVE ter 3 arquivos separados:
  - `nome.component.ts` — lógica e decorator do componente
  - `nome.component.html` — template HTML (usar `templateUrl` no decorator)
  - `nome.component.scss` — estilos (usar `styleUrl` no decorator)
  - **NUNCA** usar `template:` ou `styles:` inline — sempre arquivos externos
- **DTOs:** Separar request/response dos models de domínio
- **Mapeamento:** MapStruct ou manual em métodos factory
- **Exceções:** Global exception handler com `@ControllerAdvice` (backend) / ErrorInterceptor (frontend)

## Qualidade
- **SOLID** como prática diária
- **Clean Code:** métodos pequenos, nomes expressivos, uma responsabilidade por classe
- **Imutabilidade:** priorizar `final`, records (Java 21), builders
- **Null safety:** `Optional` para retornos que podem ser vazios, `Objects.requireNonNull` para parâmetros
- **Cobertura de testes:** buscar >80%

## Testes
- **Backend:** JUnit 5 + Mockito + Testcontainers
- **Frontend:** Vitest (Angular 21 nativo)
- **Nomenclatura:** `should[ExpectedBehavior]_when[Condition]`
- **Padrão:** Arrange → Act → Assert

## Git Workflow
- **Branch principal:** `main` (produção)
- **Branch de integração:** `dev`
- **Branches de trabalho:** `dev-<nome-da-feature>` (criar sempre a partir de `dev`)
- **Fluxo:**
  1. `git checkout dev && git checkout -b dev-<feature>`
  2. Desenvolver e commitar na branch
  3. Abrir PR de `dev-<feature>` → `dev`
  4. Merge (squash) para `dev` após aprovação
  5. Quando estável, PR de `dev` → `main`

## Issues e PRs (Obrigatório)

### Issues
- Toda nova funcionalidade DEVE ter uma issue correspondente no GitHub
- Issues usam labels: `enhancement`, `bug`, `docs`, `frontend`, `backend`
- Issues são usadas para tracking de progresso dentro do GitHub Projects
- Cada issue deve conter checklist de tarefas e critérios de aceite
- Issues DEVEM ser atualizadas conforme o progresso da implementação

### Pull Requests
- **Toda branch de feature DEVE referenciar sua issue** no nome: `dev-<feature>-<issue-number>`
- **Descrição do PR DEVE incluir:** "Closes #N" para linkar automaticamente com a issue
- PRs DEVEM referenciar a issue no corpo: `Related to #N` ou `Closes #N`
- PRs pequenos e focados (máximo 10-15 arquivos) — se for maior, quebrar em múltiplos PRs
- Checklist no PR: "√ Implementado", "√ Testado", "√ Documentado"

### GitHub Projects
- Usar GitHub Projects (v2) para organizar o roadmap visualmente
- Colunas sugeridas: Backlog → To Do → In Progress → Review → Done
- Cada issue é um card no Project
- Atualizar status do card conforme avança

## Conventional Commits
- `feat(nome): descrição` — nova feature
- `fix(nome): descrição` — correção
- `test(nome): descrição` — testes
- `refactor(nome): descrição` — refatoração
- `docs(nome): descrição` — documentação
- `chore(nome): descrição` — build, CI, config

## Endpoints REST
- **Plural nouns:** `/api/v1/tasks`, `/api/v1/tasks/{id}`
- **HTTP verbs semânticos:** GET (read), POST (create), PUT (replace), PATCH (partial), DELETE (remove)
- **Paginação:** `Pageable` do Spring Data com parâmetros `page` e `size`
- **Respostas de erro padronizadas:** `{ "status": 404, "error": "Not Found", "message": "Task not found", "timestamp": "..." }`

## Diretrizes de Feature (Spec-Driven)
- Toda nova feature deve seguir o fluxo: **Specify → (Design) → (Tasks) → Execute**
- Specs formais em `.specs/features/<feature>/`
- Antes de implementar, definir acceptance criteria com WHEN/THEN/SHALL
- Documentar edge cases e decisões técnicas no spec
- Usar sub-agents (Agent tool) para implementação de tarefas em paralelo quando aplicável
