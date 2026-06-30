# Roadmap — 8 Fases de Aprendizado

Cada fase adiciona complexidade ao **mesmo app**, sem começar do zero. Isso simula o crescimento real de sistemas.

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

## Fase 2 — Containerização
**Objetivo:** App rodando em containers, ambiente reproduzível

- [ ] Dockerfile multi-stage (build + runtime)
- [ ] docker-compose: app + PostgreSQL
- [ ] Health checks, volumes persistentes
- [ ] Networks e variáveis de ambiente
- [ ] Scripts de inicialização do banco

**Tecnologias:** Docker, Docker Compose

---

## Fase 3 — Mensageria
**Objetivo:** Processamento assíncrono com eventos

- [ ] Broker Kafka no docker-compose
- [ ] Producer: eventos de tarefa criada/atualizada/deletada
- [ ] Consumer: log de auditoria
- [ ] Consumer: notificações (simulação)
- [ ] Dead letter topic para falhas

**Tecnologias:** Apache Kafka, Spring Kafka

---

## Fase 4 — Cache
**Objetivo:** Performance com caching e rate limiting

- [ ] Redis no docker-compose
- [ ] Cache de consultas frequentes
- [ ] Rate limiting por IP/token
- [ ] Invalidação de cache em updates

**Tecnologias:** Redis, Spring Cache

---

## Fase 5 — Observabilidade
**Objetivo:** Visibilidade do que acontece no sistema

- [ ] Métricas com Micrometer + Prometheus
- [ ] Dashboards no Grafana
- [ ] Logs estruturados (JSON)
- [ ] Tracing distribuído
- [ ] Alertas básicos

**Tecnologias:** Grafana, Prometheus, Micrometer, SLF4J

---

## Fase 6 — Infra como Código
**Objetivo:** Provisionar infraestrutura de forma declarativa

- [ ] Script Terraform para provisionar VM/cloud
- [ ] Deploy do app na infra provisionada
- [ ] Gerenciar secrets de forma segura

**Tecnologias:** Terraform, Cloud (AWS/GCP/Azure free tier)

---

## Fase 7 — CI/CD
**Objetivo:** Pipeline automatizado de build, teste e deploy

- [ ] GitHub Actions: build + testes
- [ ] Lint e análise de código
- [ ] Build da imagem Docker
- [ ] Deploy automático

**Tecnologias:** GitHub Actions

---

## Fase 8 — Escalabilidade
**Objetivo:** App preparado para escala horizontal

- [ ] Kubernetes manifests (deployment, service, configmap, secret)
- [ ] Health checks (liveness, readiness)
- [ ] Horizontal Pod Autoscaler
- [ ] Rodar localmente com minikube/k3s

**Tecnologias:** Kubernetes, minikube/k3s

---

*Voltar para: [[Visão Geral do Projeto]]*
