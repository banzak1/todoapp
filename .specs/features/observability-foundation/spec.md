# Observability Foundation — Specification

**Status:** Approved — design and task breakdown completed
**Phase:** 8
**Depends on:** API REST, PostgreSQL, Kafka, Cloud Run e CI/CD existentes
**Decision Context:** `.specs/features/observability-foundation/context.md`

## Problem Statement

O todoApp possui logs básicos e processamento HTTP/Kafka, mas não oferece uma visão
operacional consistente sobre saúde, latência, erros, banco de dados ou mensagens
assíncronas. Em uma falha, hoje é necessário inferir o problema a partir de logs
textuais e não existe correlação confiável entre uma requisição HTTP, a persistência
da tarefa e os consumidores Kafka.

Esta feature cria uma fundação de observabilidade que possa ser exercitada localmente
e preserve a possibilidade de evolução para o Cloud Run, sem acoplar o domínio a
ferramentas específicas.

## Goals

- [ ] Detectar rapidamente indisponibilidade e degradação da aplicação.
- [ ] Expor métricas técnicas e de negócio com propósito operacional claro.
- [ ] Correlacionar requisições HTTP, logs e processamento assíncrono Kafka.
- [ ] Permitir investigar pelo menos um cenário de falha usando logs, métricas e traces.
- [ ] Oferecer um ambiente local reproduzível com Prometheus, Grafana e backend de tracing definido após pesquisa.
- [ ] Manter compatibilidade com H2/PostgreSQL, Kafka local/Aiven, Cloud Run e CI/CD.

## Proposed MVP Boundary

O MVP proposto deve entregar uma fatia operacional demonstrável:

1. Health checks de liveness e readiness, com exposição mínima e sem segredos.
2. Actuator + Micrometer com endpoint Prometheus protegido conforme a decisão aprovada.
3. Métricas HTTP, JVM, pool de conexões e Kafka somente quando ajudarem a diagnosticar um problema.
4. Métricas de aplicação: tarefas criadas/atualizadas, eventos publicados/processados, mensagens em DLT e chamadas/falhas de IA.
5. Correlation ID em HTTP e logs estruturados nos ambientes apropriados.
6. Prometheus e Grafana locais com um dashboard útil para taxa, erros, latência, JVM e fluxo Kafka.

Tracing distribuído e propagação completa do trace context pelo Kafka permanecem dentro
da feature, mas serão implementados depois da decisão de compatibilidade e do desenho
aprovado. O MVP não deve fingir que há tracing quando apenas existe logging.

## Out of Scope

| Feature | Reason |
| --- | --- |
| Alterar regras de negócio ou contratos REST existentes | Observabilidade deve ser transversal e não quebrar a API |
| Redis, cache, rate limiting ou idempotência | Feature independente `redis-idempotency`, posterior |
| Kubernetes e manifests | Feature independente `kubernetes-local`, posterior |
| Alertas gerenciados ou SLOs de produção | Dependem de custos, retenção e operação cloud ainda não definidos |
| Autenticação/autorização do Actuator | Segurança de acesso pode exigir uma feature própria; nesta etapa o foco é exposição segura mínima |
| Dashboard com painéis sem pergunta operacional | Evitar observabilidade ornamental |

## User Stories

### P1: Operador verifica saúde da aplicação ⭐ MVP

**User Story:** Como operador, quero distinguir processo vivo de aplicação pronta para
receber tráfego, para que o Cloud Run e o ambiente local não direcionem requisições para
uma instância incapaz de operar.

**Acceptance Criteria:**

1. WHEN o processo está vivo THEN system SHALL responder ao indicador de liveness sem expor detalhes internos.
2. WHEN dependências obrigatórias para servir tráfego não estão prontas THEN system SHALL indicar readiness negativa sem vazar credenciais ou connection strings.
3. WHEN a aplicação está pronta THEN system SHALL expor liveness e readiness em endpoints documentados e testáveis.
4. WHEN endpoints de gerenciamento são acessados fora do escopo permitido THEN system SHALL não expor dados sensíveis nem endpoints administrativos desnecessários.

**Independent Test:** iniciar a aplicação com H2 e exercitar os endpoints de liveness/readiness em cenários saudável e não pronto.

### P1: Operador investiga comportamento HTTP e de infraestrutura ⭐ MVP

**User Story:** Como operador, quero consultar métricas técnicas e de aplicação, para
identificar aumento de latência, erros, saturação ou perda no fluxo Kafka.

**Acceptance Criteria:**

1. WHEN o endpoint Prometheus é consultado conforme a política aprovada THEN system SHALL retornar métricas em formato compatível com Prometheus.
2. WHEN requisições HTTP são processadas THEN system SHALL registrar contagem, status e latência com baixa cardinalidade.
3. WHEN a JVM, o pool de conexões ou o cliente Kafka produz indicadores disponíveis THEN system SHALL expor somente os indicadores selecionados no catálogo de métricas.
4. WHEN uma tarefa é criada ou atualizada THEN system SHALL incrementar a métrica de negócio correspondente.
5. WHEN um evento é publicado, processado ou enviado à DLT THEN system SHALL registrar o resultado no fluxo de métricas correspondente.
6. WHEN uma sugestão de IA é solicitada ou falha THEN system SHALL registrar contagem e resultado sem incluir prompt, segredo ou dado sensível.

**Independent Test:** gerar tráfego HTTP e eventos controlados, consultar as séries no endpoint Prometheus e explicar qual diagnóstico cada métrica permite fazer.

### P1: Desenvolvedor correlaciona uma operação HTTP ⭐ MVP

**User Story:** Como desenvolvedor, quero encontrar a mesma operação em logs de entrada,
aplicação, banco e Kafka, para investigar uma falha sem depender apenas de timestamps.

**Acceptance Criteria:**

1. WHEN uma requisição HTTP chega sem correlation ID THEN system SHALL gerar um identificador seguro e responder com ele no header definido.
2. WHEN uma requisição chega com correlation ID válido THEN system SHALL preservá-lo, respeitando limites de tamanho e formato definidos no design.
3. WHEN a requisição gera logs THEN system SHALL incluir o identificador de correlação em todos os logs da operação.
4. WHEN a operação publica um evento Kafka THEN system SHALL transportar o contexto definido pela decisão de propagação, sem acoplar o domínio ao Kafka.

**Independent Test:** executar uma criação de tarefa, capturar o correlation ID na resposta e localizar o mesmo valor nos logs do produtor e do consumidor.

### P2: Desenvolvedor acompanha fluxo distribuído

**User Story:** Como desenvolvedor, quero visualizar o fluxo HTTP → aplicação → banco → Kafka → consumidor, para localizar em qual etapa uma operação falhou ou ficou lenta.

**Acceptance Criteria:**

1. WHEN o tracing estiver habilitado no ambiente escolhido THEN system SHALL criar spans para as fronteiras relevantes sem alterar regras de negócio.
2. WHEN contexto de trace atravessa publicação e consumo Kafka THEN system SHALL propagar e extrair o contexto conforme o padrão e a biblioteca aprovados.
3. WHEN uma dependência falha THEN system SHALL marcar o span ou evento de observabilidade com erro, sem incluir segredos ou payloads sensíveis.
4. WHEN o tracing estiver desabilitado ou indisponível THEN system SHALL manter a aplicação funcional e preservar logs/correlation ID mínimos.

**Independent Test:** criar uma tarefa e abrir o trace no backend local escolhido, identificando spans HTTP, persistência e Kafka ou documentando claramente qualquer fronteira não instrumentada.

### P2: Operador usa ambiente local e dashboard

**User Story:** Como estudante/operador, quero subir a aplicação e as ferramentas de
observabilidade localmente, para praticar diagnóstico sem depender de serviços cloud.

**Acceptance Criteria:**

1. WHEN o ambiente local aprovado é iniciado THEN system SHALL disponibilizar aplicação, Prometheus, Grafana e backend de tracing escolhido sem segredos versionados.
2. WHEN o dashboard é aberto THEN system SHALL mostrar perguntas operacionais, no mínimo taxa de requisições, percentual de erros, latência p95, JVM e fluxo Kafka.
3. WHEN um cenário de falha controlado é executado THEN system SHALL permitir localizar o sintoma no dashboard e a causa nos logs/traces.

**Independent Test:** seguir um runbook para gerar tráfego, provocar uma falha não destrutiva e explicar a investigação de ponta a ponta.

### P3: Equipe mantém a fundação

**User Story:** Como mantenedor, quero testes e documentação da observabilidade, para que
novas alterações não removam sinais essenciais nem tornem o diagnóstico irreprodutível.

**Acceptance Criteria:**

1. WHEN filtros, interceptors, propagadores ou métricas customizadas forem adicionados THEN system SHALL possuir testes próprios adequados ao padrão do projeto.
2. WHEN um novo desenvolvedor seguir a documentação THEN system SHALL conseguir iniciar o ambiente, gerar tráfego e reproduzir ao menos um cenário de falha.
3. WHEN a configuração mudar THEN system SHALL documentar impacto local, produção/Cloud Run, custo e dados que não podem ser coletados.

**Independent Test:** executar os gates de teste definidos em `.specs/codebase/TESTING.md` ou, enquanto ele não existir, registrar os gates aprovados na fase Tasks.

## Edge Cases

- WHEN o cliente envia um correlation ID vazio, excessivamente longo ou inválido THEN system SHALL gerar/substituir por um ID seguro e registrar a decisão.
- WHEN o endpoint de métricas é consultado sem autorização adequada THEN system SHALL aplicar a política aprovada sem expor informações operacionais indevidas.
- WHEN Prometheus, Grafana ou o backend de tracing está indisponível THEN system SHALL continuar servindo a API dentro dos limites definidos, sem bloquear o caminho principal.
- WHEN uma dependência Kafka ou PostgreSQL está indisponível THEN readiness, logs e métricas SHALL distinguir falha de startup, falha transitória e falha de processamento.
- WHEN labels recebem valores de alta cardinalidade, como IDs de tarefa ou correlation IDs THEN system SHALL rejeitá-los do catálogo de métricas.
- WHEN um prompt de IA, segredo, payload sensível ou connection string aparece em uma exceção THEN system SHALL evitar sua emissão em logs, métricas e traces.

## Decisions Captured During Specify

As decisões detalhadas e seu racional estão em `context.md`. Resumo:

| Area | Decision |
| --- | --- |
| Alcance | Stack completa primeiro local; aplicação preparada por configuração externa para produção. |
| Backend de tracing | Grafana Tempo será comparado formalmente com Jaeger no design; Tempo monolítico é a recomendação preliminar. |
| Logs | JSON será habilitado apenas onde o cenário validado justificar, com foco inicial em produção. |
| Actuator em produção | Exposição restrita a health, info e Prometheus; mecanismo de proteção será desenhado antes da implementação. |
| Métricas | Catálogo proposto foi aprovado. |
| Correlação e tracing | Primeiro `X-Correlation-ID` explícito com MDC e headers Kafka; depois W3C Trace Context/OpenTelemetry com `traceparent` entre producer e consumer. |
| Ambiente local | Após aprovar spec e design, Prometheus, Grafana e backend de tracing poderão ser adicionados ao Compose local. |
| Produção | Há uma confirmação pendente: uma instrução pede exportação de tracing já em produção, mas a orientação final limita a produção a logs estruturados, correlation ID, Actuator restrito e instrumentação desativável. |

## Requirement Traceability

| Requirement ID | Story | Phase | Status |
| --- | --- | --- | --- |
| OBS-01 | P1: Saúde da aplicação | Design | In Design |
| OBS-02 | P1: Saúde da aplicação | Design | In Design |
| OBS-03 | P1: Métricas técnicas | Design | In Design |
| OBS-04 | P1: Métricas de negócio | Design | In Design |
| OBS-05 | P1: Logs estruturados | Design | In Design |
| OBS-06 | P1: Correlation ID HTTP | Design | In Design |
| OBS-07 | P1: Propagação Kafka | Design | In Design |
| OBS-08 | P2: Tracing distribuído | Design | In Design |
| OBS-09 | P2: Ambiente local | Design | In Design |
| OBS-10 | P2: Dashboard | Design | In Design |
| OBS-11 | P3: Testes próprios | Design | In Design |
| OBS-12 | P3: Documentação/runbook | Design | In Design |

**Coverage:** 12 total, 0 mapped to tasks, 12 unmapped — aguardando design/tasks.

## Success Criteria

- [ ] Operador consegue verificar liveness/readiness sem exposição de dados sensíveis.
- [ ] Endpoint Prometheus e catálogo de métricas respondem conforme a política aprovada.
- [ ] Um fluxo HTTP → banco → Kafka pode ser correlacionado por logs e, após a decisão, traces.
- [ ] Dashboard responde a perguntas operacionais concretas, sem painéis ornamentais.
- [ ] Um cenário de falha documentado pode ser reproduzido e diagnosticado localmente.
- [ ] Testes e documentação são executáveis sem dependência de serviços cloud reais.
