# Roadmap — 11 Fases de Aprendizado (Revisado em 2026-07-04)

Cada fase adiciona complexidade ao **mesmo app**, sem começar do zero.
Redis postergado, IA adicionado, Deploy como fase principal, CI/CD antes de Observabilidade.

---

## Fase 1 — Fundação ✅ (Concluída em 2026-06-29)
**Objetivo:** API REST funcional com testes e banco de dados

- [x] Inicializar projeto Spring Boot 3 + Java 21
- [x] Entidade Task: CRUD completo
- [x] Conexão PostgreSQL com JPA/Hibernate (profile postgres)
- [x] Testes unitários com JUnit 5 + Mockito (16 testes)
- [x] Testes de integração com Testcontainers (7 testes)
- [x] Testes de contrato com MockMvc (15 testes)
- [x] Documentação da API (Swagger/OpenAPI)
- [x] Paginação e filtros (status, priority)

**Tecnologias:** Java 21, Spring Boot 3, Spring Data JPA, PostgreSQL, JUnit 5, Mockito, Testcontainers, SpringDoc OpenAPI

---

## Fase 2 — Containerização ✅ (Concluída em 2026-07-01)
**Objetivo:** App rodando em containers, ambiente reproduzível

- [x] Dockerfile multi-stage (build + runtime)
- [x] docker-compose: app + PostgreSQL
- [x] Health checks, volumes persistentes
- [x] Networks e variáveis de ambiente
- [x] Scripts de inicialização do banco (gerenciados via Flyway)

**Tecnologias:** Docker, Docker Compose, Flyway

---

## Fase 3 — Mensageria ✅ (Concluída em 2026-07-01)
**Objetivo:** Processamento assíncrono com eventos

- [x] Broker Kafka no docker-compose (em modo KRaft)
- [x] Producer: eventos de tarefa criada/atualizada/deletada
- [x] Consumer: log de auditoria
- [x] Consumer: notificações (simulação)
- [x] Dead letter topic para falhas (implementado com @RetryableTopic e @DltHandler)

**Tecnologias:** Apache Kafka (KRaft), Spring Kafka

---

## Fase 4 — Módulo de IA ✅ (Concluída em 2026-07-04)
**Objetivo:** Assistente de IA para sugerir prioridades e subtarefas

- [x] Porta AiTaskSuggester (interface na camada application)
- [x] Implementação LangChain4j com fallback Mock
- [x] Configuração condicional (ativa com API key, fallback automático)
- [x] Endpoint REST: POST /api/v1/tasks/ai/suggest
- [x] Testes unitários e de contrato

**Tecnologias:** LangChain4j 1.7.1, Groq/Llama via API compatível com OpenAI, Spring Boot

---

## Fase 5 — Documentação do Vault (Em andamento)
**Objetivo:** Preservar conhecimento do projeto com regras de negócio, fluxos e arquitetura

- [x] Regras de negócio documentadas
- [ ] Fluxos de eventos (Kafka) mapeados
- [ ] Estrutura do banco de dados documentada
- [ ] Decisões de design registradas com contexto
- [ ] Guia de desenvolvimento local
- [ ] Guia de deploy e operação

**Artefatos:** Vault Obsidian (`notes/`)

---

## Fase 6 — Deploy em Produção ✅ (Concluída em 2026-07-14)
**Objetivo:** Aplicação rodando em produção com custo zero

- [x] Setup Cloud Run (GCP Free Tier)
- [x] Banco PostgreSQL gerenciado (Neon Free Tier)
- [x] Kafka gerenciado (Aiven Kafka Free Tier — migrado da Confluent Cloud)
- [x] Variáveis de ambiente e secrets (OPENAI_API_KEY, etc.)
- [x] Health checks e readiness no Cloud Run
- [x] CI/CD funcional com polling loop para deploy automático

**Tecnologias:** Google Cloud Run, Neon (PostgreSQL), Aiven Kafka (Free Tier)

---

## Fase 7 — CI/CD ✅ (Concluída em 2026-07-14)
**Objetivo:** Pipeline automatizado de build, teste e deploy

- [x] GitHub Actions: build + testes
- [x] Build da imagem Docker
- [x] Deploy automático no Cloud Run
- [x] Correção: polling loop com `gcloud builds describe` (evita erro de log streaming)
- [x] Rollback strategy (Cloud Run mantém revisões anteriores)

**Tecnologias:** GitHub Actions, Docker, Cloud Run

---

## Fase 8 — Observabilidade
**Objetivo:** Visibilidade do que acontece no sistema

- [ ] Métricas com Micrometer + Prometheus
- [ ] Dashboards no Grafana
- [ ] Logs estruturados (JSON)
- [ ] Tracing distribuído
- [ ] Alertas básicos

**Tecnologias:** Grafana, Prometheus, Micrometer, SLF4J

---

## Fase 9 — Infra como Código
**Objetivo:** Provisionar infraestrutura de forma declarativa

- [ ] Script Terraform para provisionar Cloud Run
- [ ] Gerenciar secrets de forma segura
- [ ] Infra versionada junto com o código

**Tecnologias:** Terraform, Google Cloud

---

## Fase 10 — Redis / Cache (Postergado)
**Objetivo:** Performance com caching (prioridade baixa)

- [ ] Redis no docker-compose
- [ ] Cache de consultas frequentes
- [ ] Rate limiting por IP/token
- [ ] Invalidação de cache em updates

**Tecnologias:** Redis, Spring Cache

---

## Fase 11 — Kubernetes (Horizonte Final)
**Objetivo:** App preparado para escala horizontal

- [ ] Kubernetes manifests (deployment, service, configmap, secret)
- [ ] Health checks (liveness, readiness)
- [ ] Horizontal Pod Autoscaler
- [ ] Rodar localmente com minikube/k3s

**Tecnologias:** Kubernetes, minikube/k3s

---

*Voltar para: [[Visão Geral do Projeto]]*
*Ver também: [[Decisões]], [[Diretrizes]]*
