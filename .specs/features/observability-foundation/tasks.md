# Observability Foundation — Tasks

**Spec:** `.specs/features/observability-foundation/spec.md`  
**Design:** `.specs/features/observability-foundation/design.md`  
**Testing matrix:** `.specs/codebase/TESTING.md`  
**Status:** In progress — T0 through T3 completed

## Execution Plan

All tasks are sequential. The current test and container setup is not parallel-safe.

```text
T0 → T1 → T2 → T3 → T4 → T5
                              ├→ T6 → T9 ─┐
                              ├→ T7 ───────┼→ T10 → T11 → T12 → T13 → T14
                              └→ T8 ───────┘
```

| Phase | Tasks | Outcome |
| --- | --- | --- |
| 0 — Baseline | T0 | Repeatable test and Compose verification path |
| 1 — Core signals | T1–T5 | Actuator, health, correlation, logs and task metrics |
| 2 — Async signals | T6–T9 | Kafka/AI metrics and explicit correlation propagation |
| 3 — Tracing | T10–T11 | Configurable OTLP and verified `traceparent` Kafka flow |
| 4 — Local stack | T12–T14 | Tempo, Prometheus, Grafana, dashboard and runbook |

## Task Breakdown

### T0: Normalize the verification baseline ✅

**Objective:** Understand why tests attempt Kafka/Testcontainers connections and establish commands that are reproducible on the developer machine.

**Requirement:** OBS-11  
**Depends on:** None  
**Expected files:** test configuration or test classes only if the diagnosis proves a change is necessary; `.specs/codebase/TESTING.md`  
**Concepts:** Spring test slices, KafkaAdmin lifecycle, Testcontainers prerequisites, Maven Surefire, Compose CLI detection.  
**Your implementation:** Diagnose first; make the smallest test-only/configuration-only correction needed to obtain a documented baseline. Do not change production behavior.  
**Tests:** Context/integration  
**Gate:** Full — `./mvnw test`  
**Failure scenario:** Kafka is unreachable or Docker is unavailable; report the exact failure rather than masking it.  
**Questions you should answer:** Why does disabling Kafka listeners not necessarily disable KafkaAdmin? Why is a full context test different from a web-slice test?  
**Suggested commit:** `test(observability): stabilize verification baseline`

**Done when:**

- [x] The available Compose command is identified without installing tools: neither
  `docker-compose` nor `docker compose` is available in WSL.
- [x] `./mvnw test` passes: 42 tests, 0 failures, 0 errors and 0 skipped.
- [x] No test is deleted, disabled or weakened.
- [x] Test count remains at 42 executable `@Test` methods. The prior count of 43 also
  included the non-executable `@Testcontainers` annotation.

### T1: Add observability dependencies

**Objective:** Learn how Spring Boot dependency management enables Actuator, Prometheus and OTLP without manually pinning incompatible versions.

**Requirement:** OBS-01, OBS-02, OBS-03, OBS-08  
**Depends on:** T0  
**Expected files:** `pom.xml`  
**Concepts:** starter dependency, Micrometer registry, tracing bridge, BOM/version alignment.  
**Your implementation:** Add only the four dependencies specified in the design and rely on Spring Boot-managed versions.  
**Tests:** none (build configuration)  
**Gate:** Build — `./mvnw test -DskipTests`  
**Failure scenario:** dependency conflict or unsupported version; inspect `mvn dependency:tree` before changing versions.  
**Questions you should answer:** Why does Actuator not automatically expose Prometheus format? Why avoid a second OpenTelemetry auto-configuration model?  
**Suggested commit:** `build(observability): add actuator metrics and tracing dependencies`

**Done when:**

- [x] Only Actuator, Prometheus registry, Micrometer OTel bridge and OTLP exporter are added.
- [x] No explicit dependency version is introduced unless Maven proves it is required and you can explain why.
- [x] Maven resolves a single coherent dependency graph.
- [x] The project compiles with tests intentionally skipped for this configuration-only task.

### T2: Configure safe Actuator health and endpoint exposure

**Objective:** Distinguish liveness from readiness and expose only the approved management endpoints.

**Requirement:** OBS-01, OBS-02  
**Depends on:** T1  
**Expected files:** `application.yml`, `application-production.yml`, new health endpoint test class  
**Concepts:** Actuator exposure allowlist, health groups, readiness versus liveness, sanitization.  
**Your implementation:** Configure liveness, readiness and safe health details; in production allow only health, info and Prometheus. Include DB in readiness and exclude Kafka initially.  
**Tests:** Context/integration  
**Gate:** Full — `./mvnw test -Dtest=<HealthEndpointTest>`  
**Failure scenario:** database health is down while liveness remains up; explain why this prevents restart cascades.  
**Questions you should answer:** Why should Kafka not gate readiness initially? What information must a public health response omit?  
**Suggested commit:** `feat(observability): configure actuator health probes`

**Done when:**

- [x] `/actuator/health/liveness` and `/actuator/health/readiness` are testable.
- [x] The production exposure list contains no administrative endpoint.
- [x] Tests verify at least healthy liveness and sanitized health behavior.
- [x] Existing test assertions remain unchanged except where new Actuator behavior requires additive tests.

### T3: Implement HTTP correlation ID lifecycle

**Objective:** Learn request-scoped MDC context handling without relying on tracing magic.

**Requirement:** OBS-05, OBS-06  
**Depends on:** T1  
**Expected files:** new `CorrelationIdFilter`, new/updated REST filter test class  
**Concepts:** servlet filters, MDC, `finally`, header validation, thread-local cleanup.  
**Your implementation:** Accept a valid `X-Correlation-ID` or generate one; set MDC before the chain, return the header, and always clear MDC.  
**Tests:** Web slice  
**Gate:** Quick — `./mvnw test -Dtest=<CorrelationIdFilterTest>`  
**Failure scenario:** invalid or oversized client header, and MDC leaking into a later request.  
**Questions you should answer:** Why must MDC be removed in `finally`? Why should the ID not become a metric label?  
**Suggested commit:** `feat(observability): add HTTP correlation ID filter`

**Done when:**

- [x] Tests cover generated, preserved and invalid/replaced IDs.
- [x] Tests prove the response exposes exactly one valid correlation ID.
- [x] The filter leaves no MDC state after the request completes.
- [x] At least three focused assertions/tests are added.

### T4: Enable profile-scoped structured JSON logging

**Objective:** Understand how MDC becomes queryable JSON without hand-building log strings.

**Requirement:** OBS-05  
**Depends on:** T3  
**Expected files:** `application-production.yml`, focused logging configuration test  
**Concepts:** Logback, Spring Boot structured logging, Logstash JSON, MDC fields, secret redaction.  
**Your implementation:** Enable the approved JSON console format only for production and keep the local default readable unless a lab scenario explicitly needs JSON.  
**Tests:** Context/integration  
**Gate:** Quick — `./mvnw test -Dtest=<StructuredLoggingConfigurationTest>`  
**Failure scenario:** a log line contains a correlation ID but must not contain credentials, prompt or Kafka payload.  
**Questions you should answer:** Why is JSON an environment concern? What belongs in MDC versus a log message?  
**Suggested commit:** `feat(observability): configure production structured logs`

**Done when:**

- [ ] Production configuration selects a supported JSON format.
- [ ] A focused test or captured-log check proves `correlationId` is emitted when present.
- [ ] No secret or payload logging is introduced.
- [ ] Local default logging behavior is preserved.

### T5: Record task outcomes through an application port

**Objective:** Add business metrics without coupling `TaskService` to Micrometer.

**Requirement:** OBS-04, OBS-11  
**Depends on:** T1  
**Expected files:** new `TaskOperationMetrics` port, `MicrometerTaskOperationMetrics`, `TaskService`, `TaskServiceTest`, adapter test  
**Concepts:** ports/adapters, Counter, success-only business events, low-cardinality tags.  
**Your implementation:** Create the port/adapter and record successful task creation and update after persistence succeeds.  
**Tests:** Unit  
**Gate:** Quick — `./mvnw test -Dtest=TaskServiceTest,MicrometerTaskOperationMetricsTest`  
**Failure scenario:** persistence fails; no success counter may increment.  
**Questions you should answer:** Why is this port justified while putting `MeterRegistry` in `TaskService` is not? Why record after save?  
**Suggested commit:** `feat(observability): record task operation metrics`

**Done when:**

- [ ] Counters are named `todoapp.tasks.created` and `todoapp.tasks.updated`.
- [ ] `TaskServiceTest` verifies each successful use case calls the port once.
- [ ] Adapter tests verify counters increment with only low-cardinality tags.
- [ ] No task ID or correlation ID is used as a metric tag.

### T6: Propagate correlation ID and publisher outcome in Kafka

**Objective:** Connect the HTTP correlation context to a Kafka producer and distinguish publish success/failure.

**Requirement:** OBS-04, OBS-07, OBS-11  
**Depends on:** T3, T5  
**Expected files:** Kafka correlation helper, `KafkaTaskEventPublisher`, publisher unit test  
**Concepts:** Kafka record headers, asynchronous send result, MDC access, publish counters.  
**Your implementation:** Add `X-Correlation-ID` to producer records and record `todoapp.kafka.events.published` outcomes without changing the event payload contract.  
**Tests:** Unit  
**Gate:** Quick — `./mvnw test -Dtest=KafkaTaskEventPublisherTest`  
**Failure scenario:** missing MDC value and failed Kafka future/send result.  
**Questions you should answer:** Why is a Kafka header better than adding correlation ID to `TaskEvent`? What does an async send success actually mean?  
**Suggested commit:** `feat(observability): propagate correlation IDs to Kafka`

**Done when:**

- [ ] Publisher tests inspect the produced record header.
- [ ] Missing MDC produces a valid fallback ID without failing task publication.
- [ ] Publish metrics distinguish success and failure with bounded tags.
- [ ] Existing event fields remain unchanged.

### T7: Restore correlation context and metrics in Kafka consumers

**Objective:** Learn consumer-side header extraction, MDC lifecycle and DLT measurement.

**Requirement:** OBS-04, OBS-05, OBS-07, OBS-11  
**Depends on:** T3, T5  
**Expected files:** Kafka consumer context helper/interceptor, `KafkaTaskEventConsumer`, consumer unit test  
**Concepts:** consumer record headers, listener interception, retry/DLT lifecycle, MDC cleanup.  
**Your implementation:** Extract the header before processing, clear MDC after processing, increment processed and DLT counters at their actual outcomes.  
**Tests:** Unit  
**Gate:** Quick — `./mvnw test -Dtest=KafkaTaskEventConsumerTest`  
**Failure scenario:** absent/invalid header and simulated `fail`/`falha` message reaching the DLT handler.  
**Questions you should answer:** Why must the DLT counter increment only in the DLT handler? Why cannot a consumer MDC value survive to the next record?  
**Suggested commit:** `feat(observability): correlate Kafka consumer processing`

**Done when:**

- [ ] Tests cover propagated, generated and cleaned consumer context.
- [ ] Processed and DLT counters use `consumer`, `event_type` and/or `outcome` only.
- [ ] The existing retry behavior remains unchanged.
- [ ] At least one test proves DLT does not increment on ordinary successful consumption.

### T8: Instrument AI suggestion outcomes

**Objective:** Measure availability of the external AI path without leaking prompts or responses.

**Requirement:** OBS-04, OBS-11  
**Depends on:** T5  
**Expected files:** selected AI infrastructure adapter, AI metric helper/adapter, AI unit test  
**Concepts:** adapter boundary, success/failure counters, exception handling, sensitive data minimization.  
**Your implementation:** Record `todoapp.ai.suggestions` with `outcome=success|failure` at the adapter boundary.  
**Tests:** Unit  
**Gate:** Quick — `./mvnw test -Dtest=<AiObservabilityTest>`  
**Failure scenario:** LLM client throws; counter increments once and logs do not include prompt content.  
**Questions you should answer:** Why is the controller not the best place to decide AI success? What makes prompt text unsafe as a metric tag?  
**Suggested commit:** `feat(observability): record AI suggestion outcomes`

**Done when:**

- [ ] Success and failure are independently tested.
- [ ] No prompt, refined description or subtasks appear in metric tags.
- [ ] Existing mock fallback behavior remains covered.
- [ ] The counter increments exactly once per completed adapter attempt.

### T9: Enable native Kafka observations

**Objective:** Reuse Spring Kafka's timers/observations instead of duplicating latency instrumentation.

**Requirement:** OBS-03, OBS-07  
**Depends on:** T6, T7  
**Expected files:** Kafka configuration class and focused configuration test  
**Concepts:** `KafkaTemplate` observation, listener container observation, low-cardinality messaging conventions.  
**Your implementation:** Enable observations for the template and listener container; do not add record IDs or correlation IDs as tags.  
**Tests:** Context/integration  
**Gate:** Full — `./mvnw test -Dtest=<KafkaObservationConfigurationTest>`  
**Failure scenario:** Kafka cluster metadata lookup is unavailable; configure/test only the behavior that does not hide the failure.  
**Questions you should answer:** Why does enabling observation replace some Micrometer timers? Which tags are safe for Kafka topics and groups?  
**Suggested commit:** `feat(observability): enable Kafka observations`

**Done when:**

- [ ] Configuration enables observation for producer and listeners.
- [ ] A focused test verifies the configured components use the observation registry.
- [ ] Standard `spring.kafka.template` and `spring.kafka.listener` signals are documented.
- [ ] No manual duplicate latency timer is added.

### T10: Configure OpenTelemetry tracing as a feature flag

**Objective:** Prepare HTTP/JDBC/Kafka tracing while retaining a safe disabled path.

**Requirement:** OBS-08  
**Depends on:** T1, T4, T9  
**Expected files:** application/profile configuration, tracing configuration test  
**Concepts:** Micrometer Tracing bridge, OTLP exporter, W3C propagation, sampling, externalized configuration.  
**Your implementation:** Add profile/environment properties that enable tracing and point OTLP to Tempo locally; production must default to disabled export.  
**Tests:** Context/integration  
**Gate:** Full — `./mvnw test -Dtest=<TracingConfigurationTest>`  
**Failure scenario:** endpoint missing or Tempo down; application starts and serves requests with correlation IDs intact.  
**Questions you should answer:** Why is OTLP endpoint external configuration? What is the difference between turning tracing off and losing correlation ID?  
**Suggested commit:** `feat(observability): configure opt-in OTLP tracing`

**Done when:**

- [ ] Tracing/export can be disabled without removing Actuator or correlation ID.
- [ ] Local profile values target a local OTLP receiver only when explicitly enabled.
- [ ] Production does not embed a Tempo URL or secret.
- [ ] Tests cover enabled and disabled configuration paths.

### T11: Verify `traceparent` across Kafka producer and consumer

**Objective:** Prove distributed tracing rather than assuming it works from configuration alone.

**Requirement:** OBS-07, OBS-08, OBS-11  
**Depends on:** T6, T7, T10  
**Expected files:** Kafka tracing integration test and only the smallest required test configuration  
**Concepts:** W3C Trace Context, parent/child spans, asynchronous assertions, test exporter.  
**Your implementation:** Create an integration test that creates a producer span, publishes a record and proves the consumer span belongs to the same trace.  
**Tests:** Integration  
**Gate:** Full — `./mvnw test -Dtest=<KafkaTracePropagationIT>`  
**Failure scenario:** a missing `traceparent` header; consumer must create a new root trace without breaking processing.  
**Questions you should answer:** What makes two spans part of the same trace? Why should this test not depend on Tempo UI?  
**Suggested commit:** `test(observability): verify Kafka trace propagation`

**Done when:**

- [ ] Test captures spans with an in-memory/test exporter rather than an external backend.
- [ ] Producer and consumer share a trace ID when propagation is present.
- [ ] Missing propagation is explicitly asserted as a new trace.
- [ ] The test has deterministic completion/timeout handling.

### T12: Add the local observability Compose stack

**Objective:** Learn service networking, ports, volumes and configuration ownership for Prometheus, Grafana and Tempo.

**Requirement:** OBS-09  
**Depends on:** T2, T10  
**Expected files:** `docker-compose.observability.yml`, `observability/prometheus/prometheus.yml`, `observability/tempo/tempo.yml`  
**Concepts:** additive Compose files, Docker networks, named volumes, OTLP receivers, Prometheus scrape targets.  
**Your implementation:** Add Tempo monolithic, Prometheus and Grafana as local-only services; keep the production Dockerfile and workflow unchanged.  
**Tests:** Manual/config  
**Gate:** Compose — available Compose command with both files and `config`  
**Failure scenario:** Tempo/Promeheus cannot reach app or host ports collide; diagnose network/port mapping before changing application code.  
**Questions you should answer:** Why use an additive Compose file? Which services need a host port and which need only the Compose network?  
**Suggested commit:** `chore(observability): add local telemetry stack`

**Done when:**

- [ ] Tempo has a persistent local trace-data volume and no production credentials.
- [ ] Prometheus scrapes only the approved application metrics endpoint.
- [ ] Grafana, Prometheus and Tempo are local-only services.
- [ ] You obtain explicit approval immediately before any command that starts containers.

### T13: Provision Grafana datasources and an operational dashboard

**Objective:** Turn telemetry into a diagnosis workflow rather than an uncurated metric list.

**Requirement:** OBS-10  
**Depends on:** T12  
**Expected files:** Grafana datasource provisioning, dashboard provisioning and one dashboard JSON  
**Concepts:** Grafana provisioning, PromQL, trace links, RED metrics, p95 latency.  
**Your implementation:** Provision Prometheus and Tempo datasources and create one dashboard addressing the five questions in the design.  
**Tests:** Manual/config  
**Gate:** Compose — open Grafana and verify provisioned assets after explicit container-start approval  
**Failure scenario:** datasource reports no data; distinguish bad URL, scrape failure and no generated traffic.  
**Questions you should answer:** Why is p95 more useful than average here? How do published versus processed event counters reveal backlog/loss?  
**Suggested commit:** `feat(observability): add operational Grafana dashboard`

**Done when:**

- [ ] Dashboard has HTTP rate/error/p95, JVM/pool, Kafka, DLT and AI panels.
- [ ] Every panel answers a documented operational question.
- [ ] Datasources are provisioned without credentials in version control.
- [ ] Dashboard loads from files on a clean local stack.

### T14: Document the investigation runbook and validate the feature

**Objective:** Practice explaining and reproducing an incident investigation end-to-end.

**Requirement:** OBS-10, OBS-11, OBS-12  
**Depends on:** T11, T13  
**Expected files:** observability runbook, feature validation report, required project documentation updates  
**Concepts:** reproducibility, controlled failure, troubleshooting timeline, acceptance validation.  
**Your implementation:** Document normal traffic, correlation lookup, DLT simulation, dependency failure observations, local cleanup and the commands you actually used.  
**Tests:** Full/manual  
**Gate:** Build — `./mvnw verify`, followed by approved local-stack smoke checks  
**Failure scenario:** `fail`/`falha` reaches DLT; trace/log/metric evidence must tell the same story.  
**Questions you should answer:** Walk through the same incident using logs, metrics and traces. What signal detects it first, and what signal proves the cause?  
**Suggested commit:** `docs(observability): add local troubleshooting runbook`

**Done when:**

- [ ] Every OBS requirement has a validation result in the feature report.
- [ ] Runbook can be followed without cloud services or unversioned secrets.
- [ ] Existing and new tests pass with no unexplained skips.
- [ ] You can give a 60–90 second interview explanation of the implementation.

## Task Granularity Check

| Task | Scope | Status |
| --- | --- | --- |
| T0 | Test baseline only | ✅ Granular |
| T1 | Dependency manifest only | ✅ Granular |
| T2 | Actuator configuration + co-located health test | ✅ Cohesive |
| T3 | One HTTP filter + tests | ✅ Cohesive |
| T4 | One profile logging configuration + test | ✅ Cohesive |
| T5 | One metrics boundary + its service/adapter tests | ✅ Cohesive |
| T6 | Producer propagation/outcome boundary | ✅ Cohesive |
| T7 | Consumer propagation/DLT boundary | ✅ Cohesive |
| T8 | AI outcome boundary | ✅ Cohesive |
| T9 | Kafka observation configuration | ✅ Granular |
| T10 | Tracing feature configuration | ✅ Granular |
| T11 | One trace propagation integration proof | ✅ Granular |
| T12 | One local-stack deliverable | ✅ Cohesive |
| T13 | One dashboard/provisioning deliverable | ✅ Cohesive |
| T14 | Runbook and feature validation | ✅ Cohesive |

## Diagram–Definition Cross-Check

| Task | Depends on (task body) | Diagram shows | Status |
| --- | --- | --- | --- |
| T0 | None | Start | ✅ Match |
| T1 | T0 | T0 → T1 | ✅ Match |
| T2 | T1 | T1 → T2 | ✅ Match |
| T3 | T1 | T1 → T3 | ✅ Match |
| T4 | T3 | T3 → T4 | ✅ Match |
| T5 | T1 | T1 → T5 | ✅ Match |
| T6 | T3, T5 | T3/T5 → T6 | ✅ Match |
| T7 | T3, T5 | T3/T5 → T7 | ✅ Match |
| T8 | T5 | T5 → T8 | ✅ Match |
| T9 | T6, T7 | T6/T7 → T9 | ✅ Match |
| T10 | T1, T4, T9 | T1/T4/T9 → T10 | ✅ Match |
| T11 | T6, T7, T10 | T6/T7/T10 → T11 | ✅ Match |
| T12 | T2, T10 | T2/T10 → T12 | ✅ Match |
| T13 | T12 | T12 → T13 | ✅ Match |
| T14 | T11, T13 | T11/T13 → T14 | ✅ Match |

## Test Co-location Validation

| Task | Code layer | Matrix requires | Task says | Status |
| --- | --- | --- | --- | --- |
| T0 | Context/integration baseline | Context/integration | Context/integration | ✅ OK |
| T1 | Build manifest | none | none | ✅ OK |
| T2 | Profile/Actuator config | Context/integration | Context/integration | ✅ OK |
| T3 | REST filter | Web slice | Web slice | ✅ OK |
| T4 | Production logging config | Context/integration | Context/integration | ✅ OK |
| T5 | Application port/adapter | Unit | Unit | ✅ OK |
| T6 | Kafka publisher | Unit | Unit | ✅ OK |
| T7 | Kafka consumer | Unit | Unit | ✅ OK |
| T8 | AI infrastructure adapter | Unit | Unit | ✅ OK |
| T9 | Kafka configuration | Context/integration | Context/integration | ✅ OK |
| T10 | Tracing configuration | Context/integration | Context/integration | ✅ OK |
| T11 | Kafka propagation | Integration | Integration | ✅ OK |
| T12 | Compose assets | Manual/config | Manual/config | ✅ OK |
| T13 | Grafana assets | Manual/config | Manual/config | ✅ OK |
| T14 | Feature validation | Full/manual | Full/manual | ✅ OK |

## Tools Before Execute

The user remains the only implementation and command owner. Before starting a task,
confirm the exact tools and commands you want to use. Expected tools are: local editor,
Maven Wrapper, Git read-only commands, Docker/Compose only after explicit approval, and
no cloud command or dependency installation without explicit approval.
