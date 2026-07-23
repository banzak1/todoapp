# Testing Infrastructure

**Analyzed:** 2026-07-17
**Status:** Baseline validated; Docker Compose remains a prerequisite for local-stack tasks

## Frameworks and organization

- **Unit:** JUnit 5, AssertJ and Mockito (`TaskServiceTest`, `AiConfigTest`).
- **Web slice:** `@WebMvcTest` with `MockMvcTester` and `@MockitoBean`
  (`TaskControllerTest`, `AiTaskControllerTest`).
- **Persistence integration:** `@DataJpaTest` + Testcontainers PostgreSQL 16
  (`TaskRepositoryTest`).
- **Context:** `@SpringBootTest` (`TodoappApplicationTests`).
- **Declared test methods:** 42 executable `@Test` methods across six Java test
  classes. A textual search finds 43 occurrences because it also matches the
  class-level `@Testcontainers` annotation.

## Test Coverage Matrix

| Code layer | Required test type | Location pattern | Run command |
| --- | --- | --- | --- |
| Application use case / port | Unit | `src/test/java/**/application/**/*Test.java` | `./mvnw test -Dtest=TaskServiceTest` |
| REST filter/controller/advice | Web slice | `src/test/java/**/interfaces/rest/**/*Test.java` | `./mvnw test -Dtest=<WebMvcTestClass>` |
| Infrastructure adapter without container | Unit/context | `src/test/java/**/infrastructure/**/*Test.java` | `./mvnw test -Dtest=<TestClass>` |
| JPA repository | Integration | `src/test/java/**/infrastructure/persistence/**/*Test.java` | `./mvnw test -Dtest=TaskRepositoryTest` |
| Kafka publisher/consumer propagation | Integration | New `src/test/java/**/infrastructure/messaging/**/*IT.java` | `./mvnw test -Dtest=<KafkaIntegrationTest>` |
| Application/profile configuration | Context/integration | `src/test/java/**/*Test.java` | `./mvnw test -Dtest=TodoappApplicationTests` |
| Compose, Prometheus, Grafana and Tempo assets | Manual/config | `docker-compose*.yml`, `observability/**` | available Compose command + documented smoke checks |

## Parallelism Assessment

| Test type | Parallel-safe? | Isolation model | Evidence |
| --- | --- | --- | --- |
| Unit / web slice | Not configured | JUnit parallel execution is not configured; run sequentially for predictable mentoring | `TaskServiceTest`, controller tests |
| JPA Testcontainers | No | Static PostgreSQL container and `deleteAll()` before each test | `TaskRepositoryTest` |
| Context | No | Starts full Spring context and initializes Kafka admin | `TodoappApplicationTests` |
| Kafka integration | No | Will require broker lifecycle, headers and asynchronous assertions | No current Kafka integration test |
| Compose smoke checks | No | Shared local containers, networks and volumes | `docker-compose.yml` |

## Gate Check Commands

| Gate level | When to use | Command | Current state |
| --- | --- | --- | --- |
| Quick | Unit or web-slice task | `./mvnw test -Dtest=<TargetTestClass>` | Expected to be usable after each targeted task |
| Full | JPA/Kafka/context integration | `./mvnw test` | Passed on 2026-07-17: 42 tests, 0 failures, 0 errors, 0 skipped |
| Build | End of implementation phase | `./mvnw verify` | Not yet executed |
| Compose | Local-stack asset task | `docker compose -f docker-compose.yml -f docker-compose.observability.yml config` | Blocked: neither `docker-compose` nor the `docker compose` plugin is available in WSL |

## Baseline Risk

`./mvnw test` passed on 2026-07-17 with 42 tests, zero failures, errors or skips.
`TodoappApplicationTests` still logs a KafkaAdmin connection warning against
`localhost:9092` when no broker is available, but completes successfully and must not
be silenced without a separate requirement. `TaskRepositoryTest` confirms Docker/
Testcontainers work in the current environment. Docker Compose is not installed in WSL;
it is only required from T12 onward to validate the observability stack.
