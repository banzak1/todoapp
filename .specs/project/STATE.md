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

**2026-06-22 — Fase 1 Kickoff**
- Agent Reach instalado no WSL2 (GitHub + LinkedIn funcionais)
- Iniciando spec-driven development da Fase 1
- Próximo: criar spec, design, tasks e implementar API REST
