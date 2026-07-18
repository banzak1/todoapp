# Observability Foundation — Context

**Gathered:** 2026-07-17
**Spec:** `.specs/features/observability-foundation/spec.md`
**Status:** Approved for design

## Feature Boundary

Construir a fundação de observabilidade do todoApp com health checks, métricas úteis,
logs estruturados, correlation ID explícito, tracing distribuído e um ambiente local
de investigação. A execução deve começar localmente; o código deve aceitar configuração
externa e feature flags para uma evolução segura em produção.

## Implementation Decisions

### Alcance local e produção

- A stack completa será implementada e validada primeiro no ambiente local.
- Após aprovação da spec e do design, o Compose local poderá incluir Prometheus, Grafana
  e um backend de tracing.
- O deploy atual do Cloud Run não será alterado nesta feature sem uma decisão específica.
- A aplicação deverá ser preparada para produção por configuração externa e instrumentação
  desativável, sem exigir que Prometheus, Grafana ou o backend de traces existam em produção.

### Backend de tracing

- A preferência do usuário é pelo ecossistema Grafana.
- Antes da implementação, o design deve comparar Jaeger e Grafana Tempo quanto a consumo
  de recursos, portas expostas, volumes, arquivos de configuração e fluxo de uso.
- Recomendação preliminar a validar no design: Tempo em modo monolítico para desenvolvimento,
  com armazenamento local e Grafana como UI de consulta.

### Logs estruturados

- JSON não precisa estar habilitado simultaneamente em local e produção.
- A configuração será guiada pelo cenário que será validado; o foco inicial é permitir
  logs estruturados úteis em produção sem registrar segredos, payloads ou prompts de IA.

### Actuator e exposição em produção

- Em produção, os endpoints permitidos serão health, info e Prometheus.
- O design deverá definir quais endpoints são públicos, quais dependem de proteção e como
  manter readiness/liveness seguros, sem expor dados operacionais além do necessário.

### Catálogo de métricas

- Aprovadas: HTTP, JVM, pool de conexões, Kafka, tarefas criadas/atualizadas, eventos
  publicados/processados, DLT e chamadas/falhas da integração de IA.
- O design deverá justificar cada métrica, nome, tipo, labels de baixa cardinalidade e
  pergunta operacional que ela responde.

### Correlação e contexto distribuído

- Primeira etapa: implementar `X-Correlation-ID` de forma explícita para aprender geração,
  validação, MDC, resposta HTTP e propagação por headers Kafka.
- Segunda etapa, ainda nesta feature: adicionar W3C Trace Context com OpenTelemetry e validar
  a propagação de `traceparent` entre producer e consumer.
- Correlation ID e trace/span IDs coexistirão: o primeiro para rastreabilidade funcional e
  o segundo para observabilidade distribuída.

### Redis e frontend

- A integração do frontend com `Idempotency-Key` não pertence a esta feature.
- Ela será tratada depois que API, retries e comportamento de idempotência forem validados.

## Production Export Decision

- A exportação OTLP será configurável por variáveis de ambiente e feature flag.
- Ela permanecerá desabilitada por padrão em produção nesta fase.
- Prometheus, Grafana e Tempo permanecerão locais no M1; ativar um backend de traces em
  cloud exige uma decisão posterior sobre custo, retenção, acesso e operação.

## Specific References

- O objetivo é aprender a implementação e o diagnóstico no ambiente local antes de ampliar
  a operação em cloud.
- O usuário prefere o ecossistema Grafana para investigar métricas e traces.
- Mudanças locais de Compose só entram após aprovação de spec e design.

## Deferred Ideas

- Integração do frontend com `Idempotency-Key` — pertence à feature posterior de Redis e idempotência.
- Ativação de backend de tracing, retenção e consultas de traces em produção — requer decisão de operação e custo separada.
