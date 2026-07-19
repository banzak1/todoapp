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

### DEC-016: Kafka em Modo KRaft (Zookeeperless)
**Decisão:** Utilizar o Apache Kafka em modo KRaft (Kafka Raft Metadata Mode) para mensageria assíncrona, dispensando o container Zookeeper.
**Motivo:** Reduz a complexidade da infraestrutura (um container a menos para gerenciar), diminui o uso de memória no ambiente de desenvolvimento local e acelera o tempo de boot dos serviços de mensageria.
**Como aplicar:** Configuração do container `confluentinc/cp-kafka:7.6.0` com variáveis de ambiente do KRaft (process roles, node ID, controller quorum e cluster ID) no `docker-compose.yml`.

### DEC-017: Resiliência de Mensageria com DLT (Dead Letter Topic)
**Decisão:** Utilizar a estratégia de Dead Letter Topic (DLT) em conjunto com retentativas (retries) e backoff exponencial no processamento dos eventos.
**Motivo:** Evita o bloqueio da partição do Kafka (Poison Pill) em caso de exceções no processamento das mensagens, garantindo tolerância a falhas sem perda de dados para auditoria ou intervenções manuais futuras.
**Como aplicar:** Uso das anotações `@RetryableTopic` (attempts = 3, backoff exponencial) e `@DltHandler` no `KafkaTaskEventConsumer.java`.

---

## 2026-07-04 — Reestruturação do Roadmap e Módulo de IA

### DEC-018: Roadmap revisado — Redis postergado, IA e Deploy como prioridades
**Decisão:** Reorganizar as 8 fases originais para 11 fases, com as seguintes mudanças:
1. **Módulo de IA** (Fase 4) — adicionado como fase própria com LangChain4j
2. **Redis/Cache** postergado para Fase 10 (otimização, não essencial agora)
3. **Deploy em Produção** (Fase 6) — nova fase usando Cloud Run + free tier
4. **Documentação do Vault** (Fase 5) — nova fase para preservar conhecimento
5. **CI/CD** (Fase 7) agora vem antes de **Observabilidade** (Fase 8)

**Motivo:** IA já estava implementada e precisava ser finalizada. Deploy em produção é mais urgente que cache. Primeiro automatiza (CI/CD), depois monitora (Observabilidade).

### DEC-019: Cloud Run como alvo de deploy (GCP Free Tier)
**Decisão:** Usar Google Cloud Run + Neon (PostgreSQL free) + Confluent Cloud (Kafka free) + OpenAI API.
**Motivo:** Cloud Run serverless com free tier generoso (2M req/mês). Dockerfile já existe. Stack completa por R$ 0.

### DEC-020: CI/CD antes de Observabilidade
**Decisão:** GitHub Actions (CI/CD) antes de Grafana/Prometheus (Observabilidade).
**Motivo:** Primeiro deploy manual, depois automatiza, depois monitora.

### DEC-021: Fluxo de Branches dev → main
**Decisão:** Adotar fluxo com branch `dev` de integração. Branches de trabalho: `dev-<feature>`.
**Fluxo:** `dev-<feature>` → PR → `dev` (squash merge) → quando estável, PR → `main`.
**Motivo:** Proteger a `main` para produção. Permitir revisão antes do merge final.

### DEC-022: LangChain4j com fallback condicional
**Decisão:** Usar `@ConditionalOnProperty` para ativar o LangChain4j apenas quando `OPENAI_API_KEY` estiver definida. Sem a chave, `MockTaskSuggester` é usado automaticamente.
**Motivo:** Desenvolvimento local sem API key não deve quebrar. Produção com a chave ativa a IA real.

---

## 2026-07-14 — Deploy Fix e Migração Kafka Aiven

### DEC-029: Migração Confluent Cloud → Aiven Kafka Free Tier
**Decisão:** Migrar o Kafka gerenciado da Confluent Cloud para o Aiven Kafka Free Tier.
**Motivo:** Créditos iniciais de $400 da Confluent já consumiram $174. Aiven Free Tier é permanentemente gratuito ($0/mês, sem cartão de crédito), com 5 topics e Schema Registry incluso.
**Como aplicar:** Novo profile `application-production.yml` com SASL/SCRAM-SHA-256 (Aiven) ao invés de SASL/PLAIN (Confluent). Env vars atualizadas no Cloud Run.

### DEC-030: CI/CD com polling loop ao invés de log streaming
**Decisão:** Usar `gcloud builds submit --async` + polling loop com `gcloud builds describe` para monitorar builds no Cloud Build.
**Motivo:** `gcloud builds submit` tenta streamar logs, mas o GitHub Actions runner não tem permissão para ler o bucket de logs. `--async` separa o submit do log streaming. `gcloud builds wait` não existe no Cloud SDK 575.
**Como aplicar:** Polling loop no workflow: `while true; do STATUS=$(gcloud builds describe "$BUILD_ID" --format='value(status)'); case $STATUS in SUCCESS) break;; FAILURE|...) exit 1;; *) sleep 15;; esac; done`

### DEC-031: Extração da cadeia de certificados TLS no Dockerfile
**Decisão:** Extrair a cadeia completa de certificados TLS via `openssl s_client -showcerts` no Dockerfile e importar cada certificado no JVM `cacerts` com `keytool`.
**Motivo:** Aiven Kafka Free Tier usa um Project CA próprio que não está no truststore padrão do Java (eclipse-temurin). O `openssl x509` extrai apenas o primeiro certificado (servidor), não o CA.
**Como aplicar:** `sed -n '/-----BEGIN CERTIFICATE-----/,/-----END CERTIFICATE-----/p'` + `awk` para dividir cada PEM + loop `for` com `keytool -importcert` no Dockerfile.

---

## 2026-07-19 — Fundamento de Observabilidade: Health Checks

### DEC-032: Liveness e readiness com dependências distintas
**Decisão:** Expor probes do Actuator com liveness baseado apenas no estado interno da aplicação e readiness baseado no estado interno mais o banco de dados. Kafka fica fora da readiness inicialmente.
**Motivo:** Uma falha externa não deve provocar reinicializações em cascata. Já a indisponibilidade do banco impede o CRUD e deve retirar a instância do tráfego. A publicação Kafka exige uma decisão futura sobre consistência entre persistência e evento antes de se tornar um gate de readiness.
**Como aplicar:** `management.endpoint.health.group.liveness` inclui `livenessState`; readiness inclui `readinessState,db`; respostas de health não expõem detalhes de componentes.

### DEC-033: Tópicos Kafka são responsabilidade da infraestrutura
**Decisão:** Desabilitar a criação administrativa automática de tópicos pelo `KafkaAdmin` (`spring.kafka.admin.auto-create=false`).
**Motivo:** Evita dependência de broker no startup local e impede que a aplicação altere a infraestrutura de mensageria implicitamente em produção.
**Como aplicar:** Docker/local broker pode criar tópicos conforme sua política; Aiven e produção devem ter o tópico `todo-tasks` provisionado previamente.

---

*Voltar para: [[Visão Geral do Projeto]]*
