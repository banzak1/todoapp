# Diretrizes de Código e Convenções

## Idioma
- **Código:** Inglês (variáveis, funções, classes, comentários, commits)
- **Documentação:** Português (vault Obsidian, specs de feature)
- **Mensagens de commit:** Inglês, conventional commits (`feat:`, `fix:`, `test:`, `refactor:`, `docs:`)

## Stack e Versões
- **Java:** 21 LTS
- **Spring Boot:** 3.x
- **Build:** Maven ou Gradle (preferência Maven para familiaridade)
- **Banco:** PostgreSQL 16
- **Testes:** JUnit 5 + Mockito + Testcontainers

## Arquitetura
- **Padrão:** Clean Architecture / Hexagonal (Ports & Adapters)
- **Camadas:** Controller → Service → Repository (Domain no centro)
- **DTOs:** Separar request/response dos models de domínio
- **Mapeamento:** MapStruct ou manual em métodos factory
- **Exceções:** Global exception handler com `@ControllerAdvice`

## Qualidade
- **SOLID** como prática diária
- **Clean Code:** métodos pequenos, nomes expressivos, uma responsabilidade por classe
- **Imutabilidade:** priorizar `final`, records (Java 21), builders
- **Null safety:** `Optional` para retornos que podem ser vazios, `Objects.requireNonNull` para parâmetros
- **Cobertura de testes:** buscar >80% (não 100% artificial, mas testar comportamento real)

## Testes
- **Unitários:** JUnit 5 + Mockito (cobrir lógica de service)
- **Integração:** Spring Boot Test + Testcontainers (PostgreSQL real)
- **Contrato:** Testes de API com MockMvc
- **Nomenclatura:** `should[ExpectedBehavior]_when[Condition]`
- **Padrão:** Arrange → Act → Assert

## Git
- **Branch principal:** `main` (produção)
- **Branch de integração:** `dev`
- **Branches de trabalho:** `dev-<nome-da-feature>` (criar sempre a partir de `dev`)
- **Fluxo:**
  1. `git checkout dev && git checkout -b dev-<feature>`
  2. Desenvolver e commitar na branch
  3. Abrir PR de `dev-<feature>` → `dev` (usar GitHub MCP)
  4. Merge (squash) para `dev` após aprovação
  5. Quando estável, PR de `dev` → `main` para aprovação final
- **Conventional commits:**
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

---

*Voltar para: [[Visão Geral do Projeto]]*
