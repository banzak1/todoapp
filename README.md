# todoApp — Laboratório Evolutivo de Engenharia de Software e DevOps

O **todoApp** é um laboratório de aprendizado contínuo, estruturado não apenas como uma aplicação de gerenciamento de tarefas, mas como um projeto evolutivo. O objetivo principal é evoluir esta base de código progressivamente ao longo de **8 fases**, simulando o ciclo de vida real de um sistema em produção — partindo da fundação até a alta disponibilidade, observabilidade e escalabilidade horizontal.

Este repositório serve como portfólio técnico e ambiente de experimentação prática de arquitetura de software, DevOps e engenharia de confiabilidade (SRE).

---

## 🚀 O Roadmap das 8 Fases

O projeto evolui adicionando novas camadas de complexidade técnica no mesmo repositório:

1. **Fase 1 — Fundação** (✅ Concluída): API REST funcional desenvolvida em Java 21 e Spring Boot 3.x, persistência relacional com PostgreSQL (local) / H2 (dev), testes unitários, testes de integração com Testcontainers e documentação interativa com Swagger/OpenAPI.
2. **Fase 2 — Containerização** (✅ Concluída): Empacotamento da aplicação com Docker (build multi-stage leve com JRE 21 e usuário não-root) e orquestração do ambiente local (App + Banco) via Docker Compose. Introdução do **Flyway** para controle de versionamento do esquema do banco de dados.
3. **Fase 3 — Mensageria** (✅ Concluída): Introdução de processamento assíncrono orientado a eventos usando Apache Kafka (modo KRaft / Zookeeperless). Implementação de produtores de eventos de tarefas e múltiplos grupos de consumidores (Auditoria e Notificações simuladas), com resiliência baseada em **Dead Letter Topic (DLT)** com retentativas e backoff exponencial.
4. **Fase 4 — Cache & Rate Limiting** (⚠️ Próximo Passo): Otimização de performance com Redis para consultas frequentes e implementação de controle de vazão de requisições por IP ou Token.
5. **Fase 5 — Observabilidade**: Monitoramento completo com Prometheus e Grafana, tracing distribuído e logs estruturados em formato JSON.
6. **Fase 6 — Infraestrutura como Código (IaC)**: Provisionamento declarativo de recursos em nuvem usando Terraform.
7. **Fase 7 — CI/CD**: Automatização da esteira de integração e entrega contínua (CI/CD) via GitHub Actions (validação, testes, build da imagem e deploy).
8. **Fase 8 — Escalabilidade & Orquestração**: Implantação escalável da aplicação em Kubernetes (com Minikube ou K3s), definindo manifests, probes de integridade (Liveness/Readiness) e auto-scaling (HPA).

---

## 🛠️ Stack Tecnológica Atualizada (Até Fase 3)

*   **Java 21 LTS** e **Spring Boot 3.5.x** (Spring Web, Spring Data JPA, Spring Validation, Spring Kafka)
*   **PostgreSQL 16** (Banco de dados de produção / containerizado)
*   **H2 Database** (Banco de dados em memória para desenvolvimento rápido)
*   **Flyway Database Migrations** (Controle e histórico de esquema de banco de dados)
*   **Apache Kafka (Confluent cp-kafka)** (Broker de eventos rodando em modo KRaft/Zookeeperless)
*   **Lombok** (Produtividade e redução de boilerplate)
*   **Springdoc OpenAPI 2.8.x (Swagger UI)** (Documentação e teste interativo da API)
*   **JUnit 5 & Mockito** (Testes unitários e mocks de comportamento)
*   **Testcontainers PostgreSQL** (Testes de integração com banco de dados real em containers Docker efêmeros)

---

## 🏛️ Arquitetura do Projeto (Clean Architecture / Ports & Adapters)

O projeto separa as regras de negócio centrais (domínio e casos de uso) dos detalhes de tecnologia (banco de dados, frameworks e mensageria):

```text
src/main/java/com/banzak/todoapp/
│
├── domain/                      # Regras de Negócio Core (Entidades e Enums)
│   ├── Task.java
│   ├── TaskPriority.java
│   └── TaskStatus.java
│
├── application/                 # Casos de Uso e Lógica de Aplicação (Serviços e Portas)
│   ├── TaskService.java
│   ├── TaskEventPublisher.java  # PORTA: Interface para publicar eventos (desacoplada de tecnologia)
│   └── TaskNotFoundException.java
│
├── infrastructure/              # Detalhes de Tecnologia e Adaptadores (Infraestrutura)
│   ├── persistence/
│   │   └── TaskRepository.java  # Interface Spring Data JPA
│   │
│   └── messaging/               # ADAPTADORES: Mensageria (Kafka)
│       ├── KafkaTaskEventPublisher.java  # Implementação da porta TaskEventPublisher usando KafkaTemplate
│       ├── KafkaTaskEventConsumer.java   # Consumidores do Kafka (Auditoria, Notificação e DLT)
│       └── TaskEvent.java                # Record DTO para tráfego do evento em JSON
│
└── interfaces/                  # Camada de Entrada / HTTP REST
    └── rest/
        ├── TaskController.java  # Endpoints REST expostos
        ├── GlobalExceptionHandler.java
        └── dto/                 # Objetos de Transferência de Dados (Requests/Responses)
```

---

## 🔌 Endpoints da API (REST v1)

Todos os endpoints utilizam o prefixo `/api/v1/tasks`:

| Método | Endpoint | Descrição | Parâmetros de Busca / Filtros |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/v1/tasks` | Cria uma nova tarefa | JSON no corpo da requisição |
| `GET` | `/api/v1/tasks` | Retorna todas as tarefas (sem paginação) | Nenhum |
| `GET` | `/api/v1/tasks/page` | Retorna tarefas com paginação e ordenação | `page`, `size`, `sort`, `status`, `priority` |
| `GET` | `/api/v1/tasks/{id}` | Busca uma tarefa específica por ID | ID na URL |
| `PUT` | `/api/v1/tasks/{id}` | Atualiza os dados de uma tarefa | ID na URL + JSON no corpo |
| `DELETE` | `/api/v1/tasks/{id}` | Remove uma tarefa do banco de dados | ID na URL |

---

## 🏃 Como Executar Localmente

### Pré-requisitos
*   Java Development Kit (JDK) 21 instalado localmente (para rodar fora de container).
*   Docker e Docker Compose instalados e configurados.

---

### Modo 1: Orquestração Completa via Docker Compose (Postgres + Kafka + App)
Esta é a forma recomendada, pois ativa todas as integrações reais (Fase 2 e Fase 3).

1.  **Limpar volumes e containers antigos (opcional, para garantir um início limpo):**
    ```bash
    docker compose down -v
    ```
2.  **Compilar o código fonte e iniciar todos os serviços:**
    ```bash
    docker compose up --build
    ```
3.  **Acessar a aplicação:**
    *   **API / Swagger UI:** `http://localhost:8080/swagger-ui.html`
    *   **Banco de Dados PostgreSQL:** Porta `5432` no host local.
    *   **Porta do Broker Kafka (Host Externo):** Porta `29092` no host local (permitindo conexão de ferramentas visuais como Offset Explorer).

---

### Modo 2: Executando apenas os Brokers locais (Banco + Kafka) e o App no Host
Útil para depuração rápida do código Java diretamente na sua IDE.

1.  **Subir apenas o Postgres e o Kafka no Docker:**
    ```bash
    docker compose up -d db kafka
    ```
2.  **Iniciar o Spring Boot local apontando para o profile postgres:**
    ```bash
    ./mvnw spring-boot:run -Dspring-boot.run.profiles=postgres
    ```

---

### Modo 3: Modo de Desenvolvimento Rápido (H2)
Este modo inicia a aplicação usando banco em memória H2. **Nota:** O Kafka Listener auto-startup é desativado por padrão neste profile para evitar erros de conexão na falta do broker.

```bash
./mvnw spring-boot:run
```
*   **Console H2:** `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:todoapp`, Username: `sa`)

---

## 🧪 Testando a Resiliência com DLT (Dead Letter Topic)

Os consumidores Kafka do projeto estão configurados com uma política de retentativas. Se um erro ocorrer ao ler uma mensagem:
1. O Spring Kafka tenta re-processar a mensagem **3 vezes**, aplicando um **Backoff Exponencial** (com atraso progressivo dobrado a cada tentativa).
2. Se a falha persistir após as 3 tentativas, a mensagem é encaminhada para o tópico de descarte `todo-tasks-dlt`, onde o manipulador `@DltHandler` registra a falha.

### Como simular e validar o fluxo de DLT:
1. Com a aplicação rodando no Docker Compose, faça um envio `POST` para criar uma tarefa.
2. No **título** da tarefa, inclua a palavra **`fail`** ou **`falha`** (ex: `{"title": "Testar DLT fail", "priority": "HIGH"}`).
3. Acompanhe os logs no seu console do Docker. Você verá o consumidor de auditoria tentando processar a mensagem, falhando, aplicando o backoff e, após a 3ª tentativa, encaminhando-a para o DLT com sucesso.

---

## 🧪 Rodando os Testes Automatizados

A suíte de testes conta com testes unitários, testes de integração reais (usando Docker/Testcontainers para PostgreSQL real) e testes de contrato HTTP (MockMvc).

Execute todos os testes com o comando:
```bash
./mvnw clean test
```

---

## 🧑‍💻 Desenvolvedor
*   **Leonardo Santana** (Pleno)
*   LinkedIn: [linkedin.com/in/banzak](https://linkedin.com/in/banzak)
*   GitHub: [github.com/banzak1](https://github.com/banzak1)
