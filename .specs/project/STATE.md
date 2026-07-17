# STATE — todoApp

## Decisions

### DEC-001: WSL2 Ubuntu como ambiente principal (2026-06-22)
**Decisão:** Desenvolver no WSL2 Ubuntu, não no Windows nativo.
**Motivo:** Docker, Kafka, Redis e CI/CD têm melhor suporte e performance no Linux.

### DEC-002: Java 21 + Spring Boot 3 como stack base (2026-06-22)
**Decisão:** Usar Java 21 LTS e Spring Boot 3.x.
**Motivo:** Java 21 é LTS com suporte até 2029.

### DEC-003: 11 fases progressivas, mesmo app (2026-07-04)
**Decisão:** Evoluir o mesmo código base com 11 fases, adicionando IA, deploy e CI/CD antes de observabilidade.
**Motivo:** Alinhado com aprendizado progressivo e urgência de deploy.

### DEC-004: Clean Architecture como padrão (2026-06-22)
**Decisão:** Usar Clean Architecture / Hexagonal (Ports & Adapters).
**Motivo:** Separa domínio de infraestrutura, facilita testabilidade.

### DEC-005: Vault Obsidian + CLAUDE.md como fonte de contexto (2026-06-22)
**Decisão:** Usar vault Obsidian (`notes/`) como armazenamento primário de contexto do projeto.

### DEC-011: Profile-based database switching (2026-06-29)
**Decisão:** Manter H2 como default (dev) com profile `postgres` para PostgreSQL.

### DEC-014: Controle de Versão do Banco com Flyway (2026-07-01)
**Decisão:** Flyway para migrações de esquema, ddl-auto: validate.

### DEC-015: Docker Multi-stage e Segurança Non-Root (2026-07-01)
**Decisão:** Build multi-stage, usuário spring:spring no runtime.

### DEC-016: Kafka em Modo KRaft (2026-07-01)
**Decisão:** Kafka sem Zookeeper, modo KRaft puro.

### DEC-017: Resiliência com DLT (2026-07-01)
**Decisão:** Dead Letter Topic com @RetryableTopic para mensagens com falha.

### DEC-019: Cloud Run como alvo de deploy (2026-07-04)
**Decisão original:** Google Cloud Run + Neon (PostgreSQL free) + Confluent Cloud (Kafka free).
**Estado atual:** Kafka de produção usa Aiven Kafka Free Tier; a migração está registrada nas decisões de 2026-07-14.

### DEC-020: CI/CD antes de Observabilidade (2026-07-04)
**Decisão:** GitHub Actions primeiro, depois Grafana/Prometheus.

### DEC-021: Fluxo de Branches dev → main (2026-07-04)
**Decisão:** dev-{feature} → PR → dev → PR → main.

### DEC-022: LangChain4j com fallback condicional (2026-07-04)
**Decisão:** IA ativa apenas com OPENAI_API_KEY, fallback Mock automático.

### DEC-023: CI/CD com GitHub Actions (2026-07-05)
**Decisão:** Test no PR, build + deploy no push para main.

### DEC-024: Issues no GitHub para rastrear fases (2026-07-05)
**Decisão:** 8 issues criadas para fases restantes.

### DEC-025: Frontend Angular em repositório separado (2026-07-05)
**Decisão:** Angular standalone components, Signals, repositório todoapp-angular.
**Motivo:** Projetos desacoplados, CI/CD independente, cada um com seu ciclo de vida.

## Blockers

Nenhum ativo.

## Lessons Learned

- Clean Architecture + Spring Boot funciona bem: domínio anêmico evitado com services ricos
- Testcontainers > H2 para testes de integração — mesma sintaxe SQL que produção
- Kafka KRaft reduz complexidade significativamente vs Zookeeper
- LangChain4j + Groq/Llama funciona bem como alternativa gratuita à OpenAI
- Cloud Run + Neon + Aiven Kafka = stack de produção baseada em free tiers
- GitHub Actions + Cloud Build = pipeline confiável

## Preferences

- Código em inglês, documentação em português
- Conventional commits
- Testes com padrão `should[Expected]_when[Condition]`
- Angular standalone components (sem NgModules), Signals, Angular Material

## Active Session

**2026-07-14 — Correção de deploy e migração Kafka Aiven**
- CI/CD com GitHub Actions operacional, incluindo polling do Cloud Build ✅
- Kafka de produção migrado da Confluent Cloud para Aiven Kafka Free Tier ✅
- Cadeia de certificados TLS do Aiven importada no truststore JVM ✅
- Frontend Angular no repositório separado `todoapp-angular` e CORS concluídos ✅
- Próximo passo: iniciar a Fase 8 (observabilidade)

## Deferred Ideas

- Autenticação JWT (após frontend funcional)
- Testes E2E com Cypress
- PWA/offline support
- WebSocket para atualizações em tempo real
