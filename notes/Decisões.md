# Decisões de Arquitetura e Design

Registro de decisões técnicas, bloqueios e lições aprendidas durante o desenvolvimento.

---

## 2026-06-22 — Kickoff do Projeto

### DEC-001: WSL2 Ubuntu como ambiente principal
**Decisão:** Desenvolver no WSL2 Ubuntu, não no Windows nativo.
**Motivo:** Docker, Kafka, Redis e CI/CD têm melhor suporte e performance no Linux. Tutoriais e documentação assumem Unix.
**Como aplicar:** VS Code + Remote-WSL. Código na OneDrive acessível de ambos os OS.

### DEC-002: Java 21 + Spring Boot 3 como stack base
**Decisão:** Usar Java 21 LTS e Spring Boot 3.x.
**Motivo:** Java 21 é LTS com suporte até 2029. Virtual threads, records, pattern matching. Spring Boot 3 é o padrão de mercado.
**Como aplicar:** Inicializar com Spring Initializr (Maven, Java 21, Spring Boot 3.x).

### DEC-003: 8 fases progressivas, mesmo app
**Decisão:** Evoluir o mesmo código base em vez de criar projetos separados.
**Motivo:** Simula crescimento real de sistemas. Evita o "reset" de aprendizado. Gera portfólio com história.
**Como aplicar:** Cada fase é uma branch nova ou um milestone. A Fase 1 é o MVP.

### DEC-004: Clean Architecture como padrão
**Decisão:** Usar Clean Architecture / Hexagonal (Ports & Adapters).
**Motivo:** Separa domínio de infraestrutura, facilita testabilidade e troca de tecnologias entre fases.
**Como aplicar:** Pacotes `domain`, `application`, `infrastructure`, `interfaces`.

### DEC-005: Vault Obsidian + CLAUDE.md como fonte de contexto
**Decisão:** Usar o vault Obsidian (`notes/`) como armazenamento primário de contexto do projeto, complementado por `CLAUDE.md` para diretrizes do assistente.
**Motivo:** O vault é navegável no Obsidian (graph view, backlinks), acessível de qualquer ambiente (Windows/Ubuntu via OneDrive), e o Claude Code carrega esses arquivos para continuidade entre sessões.
**Como aplicar:** Todo início de sessão, carregar `notes/Visão Geral.md`, `notes/Roadmap.md`, `notes/Decisões.md`, `notes/Diretrizes.md` e o último resumo de sessão.

---

---

## 2026-06-23 a 25 — Estratégia de Carreira

### DEC-006: Dois currículos, um perfil
**Decisão:** Manter versão Java (principal) e versão Node.js do currículo, alternando conforme a vaga.
**Motivo:** 90% das vagas pedem Java/Spring Boot; mas vagas Node.js que valorizam Claude Code/SDD são raras e valem o esforço.
**Como aplicar:** CV Java pra Minsait, Magalu, Sicredi, BEES. CV Node.js pra vagas fullstack JS.

### DEC-007: Faixa salarial Pleno CLT: R\$ 8.000 – R\$ 10.000
**Decisão:** Pretensão padrão entre R\$ 8k–10k para CLT Pleno.
**Motivo:** Médio do mercado (Payscale, Glassdoor, consultorias). Alto o suficiente pra se valorizar, baixo o suficiente pra passar do filtro.
**Como aplicar:** Flexível conforme benefícios. PJ: R\$ 10k–12k.

### DEC-008: Recusar vagas Júnior mesmo em transição
**Decisão:** Não aceitar enquadramento Júnior.
**Motivo:** 4+ anos de XP com stack enterprise. Entrar como Jr reseta a carreira e o salário. Melhor esperar 1-2 meses pelo Pleno certo.
**Como aplicar:** Se urgência financeira for crítica, aceitar Jr mas continuar procurando Pleno ativamente.

### DEC-009: BEES (AB InBev) é prioridade máxima
**Decisão:** Priorizar a vaga Intermediate Java Developer na BEES.
**Motivo:** Plataforma global (29 países), 34 candidatos apenas, stack compatível, inglês intermediário acessível.
**Como aplicar:** CV Java, destacar experiência com microservices e Kafka.

### DEC-010: LinkedIn MCP para busca de vagas
**Decisão:** Usar o servidor linkedin-scraper-mcp para buscar vagas automaticamente.
**Motivo:** Resultados mais precisos que busca manual, filtro por nível e data.
**Script:** `scripts/busca_vagas.sh` — salva resultados em `scripts/jobs_output.txt`.

---

## 2026-06-29 — Finalização da Fase 1

### DEC-011: Profile-based database switching
**Decisão:** Manter H2 como default (dev) com profile `postgres` para PostgreSQL.
**Motivo:** H2 permite desenvolvimento rápido sem Docker. Profile postgres para testes reais e produção.
**Como aplicar:** `application.yml` (H2) + `application-postgres.yml` (PostgreSQL). Ativar com `--spring.profiles.active=postgres`.

### DEC-012: Endpoint paginado separado
**Decisão:** Criar `GET /api/v1/tasks/page` para paginação, mantendo `GET /api/v1/tasks` original sem paginação.
**Motivo:** Compatibilidade retroativa. Clientes simples podem usar o endpoint original; clientes que precisam de paginação usam o novo.
**Como aplicar:** Controller tem `findAll()` (List) e `findAllPaginated()` (Page). Service gerencia ambos.

### DEC-013: OpenAPI com springdoc
**Decisão:** Usar `springdoc-openapi-starter-webmvc-ui` para documentação OpenAPI 3.0.
**Motivo:** Integração nativa com Spring Boot 3, zero configuração extra além do dependency, Swagger UI embutido.
**Como aplicar:** Endpoints anotados com `@Operation`, `@ApiResponse`, `@Schema` nos DTOs.

---

## 2026-07-01 — Finalização da Fase 2

### DEC-014: Controle de Versão do Banco com Flyway
**Decisão:** Introduzir o Flyway como ferramenta de migração de esquema de banco de dados e desativar a auto-criação do Hibernate (`ddl-auto: validate`).
**Motivo:** Evita inconsistências de esquema entre ambientes (dev/prod/testes), rastreia todas as alterações estruturais do banco de dados no controle de versão e automatiza a carga de dados iniciais.
**Como aplicar:** Scripts `.sql` ordenados em `src/main/resources/db/migration/` e dependência do Flyway no `pom.xml`.

### DEC-015: Docker Multi-stage e Segurança (Non-Root)
**Decisão:** Usar build multi-stage no Dockerfile e rodar a aplicação sob um usuário sem privilégios de administrador (`spring:spring`).
**Motivo:** Reduz o tamanho final da imagem (apenas JRE necessário no runtime) e mitiga riscos de segurança impedindo que vulnerabilidades no app deem acesso root ao host.
**Como aplicar:** Diretivas `FROM ... AS builder` e criação de grupo/usuário com `addgroup`/`adduser` no `Dockerfile`.

---

*Voltar para: [[Visão Geral do Projeto]]*
