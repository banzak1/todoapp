# todoApp

**Vision:** Laboratório de aprendizado contínuo — um app de tarefas que evolui em complexidade ao longo de 8 fases, simulando o crescimento real de um sistema em produção.
**For:** Leonardo Santana (dev pleno) — portfólio técnico e preparação para entrevistas.
**Solves:** Domínio prático de tecnologias enterprise (Kafka, Redis, Docker, Grafana, Terraform, CI/CD, Kubernetes) em um único projeto com história evolutiva.

## Goals

- Completar as 8 fases do roadmap com código funcional e testado
- Alta cobertura de testes (>80%) seguindo Clean Code e SOLID
- Cada fase adiciona complexidade ao mesmo app, sem começar do zero

## Tech Stack

**Core:**
- Framework: Spring Boot 3.x
- Language: Java 21 LTS
- Database: PostgreSQL 16
- Build: Maven

**Key dependencies:** Spring Data JPA, Spring Web, Spring Validation, Hibernate, JUnit 5, Mockito, Testcontainers, Springdoc OpenAPI, MapStruct

## Scope

**v1 (Fase 1) includes:**
- API REST de tarefas (CRUD completo)
- Entidade Task com validação
- Conexão PostgreSQL com JPA/Hibernate
- Testes unitários com JUnit 5 + Mockito
- Testes de integração com Testcontainers
- Documentação da API com Swagger/OpenAPI
- Clean Architecture (Controller → Service → Repository)

**Explicitly out of scope:**
- Autenticação/Autorização (futuro)
- Frontend (API only)
- Docker (Fase 2)
- Kafka (Fase 3)
- Redis (Fase 4)
- Métricas/Monitoramento (Fase 5)

## Constraints

- Timeline: Sem prazo fixo, foco em aprendizado de qualidade
- Technical: Java 21 LTS, Spring Boot 3.x, Maven, PostgreSQL 16
- Environment: WSL2 Ubuntu para desenvolvimento
- Resources: Ambiente local, sem custo de cloud na Fase 1
