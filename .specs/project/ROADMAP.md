# Roadmap — 11 Fases

**Ciclo técnico atual:** M1 — Observabilidade e confiabilidade

| # | Fase | Status |
|---|------|--------|
| 1 | **Fundação** — Spring Boot + PostgreSQL + CRUD | ✅ Concluído |
| 2 | **Containerização** — Docker + Docker Compose | ✅ Concluído |
| 3 | **Mensageria** — Apache Kafka (KRaft) | ✅ Concluído |
| 4 | **Módulo de IA** — LangChain4j + Groq/Llama | ✅ Concluído |
| 5 | **Documentação do Vault** — Obsidian notes | 🔶 Em andamento |
| 6 | **Deploy em Produção** — Cloud Run + Neon + Aiven Kafka | ✅ Concluído |
| 7 | **CI/CD** — GitHub Actions | ✅ Concluído |
| — | **Frontend Angular** (repositório `todoapp-angular`) | ✅ Concluído |
| 8 | **Observabilidade e confiabilidade** — health, métricas, logs, tracing e dashboard | 🔶 Em design |
| 9 | **Infra como Código** — Terraform | ⏳ Pendente |
| 10 | **Redis e idempotência** — Redis configurável + criação idempotente de tarefas | ⏳ Pendente |
| 11 | **Kubernetes local** — manifests, probes, HPA e troubleshooting | ⏳ Pendente |

## Novo ciclo de evolução técnica

Cada marco é uma feature independente em `.specs/features/` e só começa após a
validação do marco anterior:

### M1 — Observability Foundation 🔶 Em design

- Feature: `.specs/features/observability-foundation/`
- Objetivo: health checks, Actuator/Micrometer, métricas com propósito, logs estruturados,
  correlation ID, contexto Kafka, tracing local, Prometheus, Grafana e runbook de diagnóstico.
- Gate: fluxo HTTP → PostgreSQL → Kafka investigável localmente sem depender de cloud real.

### M2 — Redis e idempotência ⏳ Bloqueado por M1

- Feature: `.specs/features/redis-idempotency/`
- Objetivo: Redis local configurável e idempotência concorrente para criação de tarefas,
  incluindo TTL, fingerprint, estados e não duplicação de evento Kafka.
- Gate: retries, conflitos, concorrência, indisponibilidade e expiração reproduzíveis em testes.

### M3 — Kubernetes local ⏳ Bloqueado por M2

- Feature: `.specs/features/kubernetes-local/`
- Objetivo: executar o app em cluster local com configuração externa, probes, rollout,
  HPA e troubleshooting documentado.
- Gate: deployment reproduzível e sinais de observabilidade preservados no cluster.

## Próximos Marcos

1. Aprovar a especificação de `.specs/features/observability-foundation/spec.md`
2. Fechar decisões cinzentas e criar `design.md`
3. Criar `tasks.md` granular e executar M1 tarefa a tarefa
4. Criar e validar M2 — Redis e idempotência
5. Criar e validar M3 — Kubernetes local

## Ideias futuras

- Terraform/IaC permanece como Fase 9 independente, após o ciclo M1–M3 ser planejado.
- Alertas gerenciados e SLOs de produção serão refinados depois da fundação de observabilidade.
