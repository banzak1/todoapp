# todoApp

**Vision:** Laboratório de aprendizado contínuo — um app de tarefas que evolui em complexidade ao longo de 11 fases, simulando o crescimento real de um sistema em produção.
**For:** Leonardo Santana (dev pleno) — portfólio técnico e preparação para entrevistas.
**Solves:** Domínio prático de tecnologias enterprise (Kafka, Redis, Docker, Grafana, Terraform, CI/CD, Kubernetes) em um único projeto com história evolutiva.

## Goals

- Completar as 11 fases do roadmap com código funcional e testado
- Alta cobertura de testes (>80%) seguindo Clean Code e SOLID
- Cada fase adiciona complexidade ao mesmo app, sem começar do zero
- Frontend Angular em repositório separado consumindo a API REST

## Tech Stack

**Backend:**
- Framework: Spring Boot 3.x
- Language: Java 21 LTS
- Database: PostgreSQL 16
- Build: Maven
- Mensageria: Apache Kafka (KRaft mode)
- IA: LangChain4j + OpenAI

**Frontend (novo repositório):**
- Framework: Angular (latest)
- Language: TypeScript
- UI: Angular Material / Tailwind CSS
- Build: Angular CLI

**Infra & DevOps:**
- Containerização: Docker + Docker Compose
- CI/CD: GitHub Actions
- Deploy: Cloud Run (GCP Free Tier)
- Cloud: Google Cloud Platform

## Roadmap — 11 Fases

| Fase | Status |
|------|--------|
| 1 — Fundação (Spring Boot + PostgreSQL) | ✅ Concluído |
| 2 — Containerização (Docker) | ✅ Concluído |
| 3 — Mensageria (Kafka) | ✅ Concluído |
| 4 — Módulo de IA (LangChain4j) | ✅ Concluído |
| 5 — Documentação do Vault | 🔶 Em andamento |
| 6 — Deploy em Produção (Cloud Run) | ✅ Concluído |
| 7 — CI/CD (GitHub Actions) | ✅ Concluído |
| 8 — Observabilidade (Prometheus/Grafana) | ⏳ Pendente |
| 9 — Infra como Código (Terraform) | ⏳ Pendente |
| 10 — Redis/Cache | ⏳ Pendente |
| 11 — Kubernetes | ⏳ Pendente |

## Scope

**Includes:**
- API REST de tarefas (CRUD completo + paginação + filtros)
- Mensageria assíncrona com Kafka (eventos de criação/atualização/deleção)
- Assistente de IA para sugestão de prioridades e subtarefas
- Docker multi-stage + docker-compose
- CI/CD com GitHub Actions (test → build → deploy)
- Deploy no Google Cloud Run
- Frontend Angular em repositório separado

**Explicitly out of scope:**
- Autenticação/Autorização (futuro)
- Testes E2E (futuro)
- Mobile app (futuro)

## Constraints

- Timeline: Sem prazo fixo, foco em aprendizado de qualidade
- Technical: Java 21 LTS, Spring Boot 3.x, Maven, PostgreSQL 16, Angular
- Environment: WSL2 Ubuntu para desenvolvimento
- Resources: Cloud gratuito (GCP Free Tier, Neon PostgreSQL, Confluent Cloud free)
- Frontend: repositório GitHub separado (todoapp-angular)
