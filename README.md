# todoApp — Laboratório Evolutivo de Engenharia de Software e DevOps

O **todoApp** é um laboratório de aprendizado contínuo, estruturado não apenas como uma aplicação de gerenciamento de tarefas, mas como um projeto evolutivo. O objetivo principal é evoluir esta base de código progressivamente ao longo de **8 fases**, simulando o ciclo de vida real de um sistema em produção — partindo da fundação até a alta disponibilidade, observabilidade e escalabilidade horizontal.

Este repositório serve como portfólio técnico e ambiente de experimentação prática de arquitetura de software, DevOps e engenharia de confiabilidade (SRE).

---

## 🚀 O Roadmap das 8 Fases

O projeto foi planejado para evoluir adicionando novas camadas de complexidade técnica no mesmo repositório:

1. **Fase 1 — Fundação** (✅ Concluída): API REST funcional desenvolvida em Java 21 e Spring Boot 3.x, persistência relacional com PostgreSQL, testes unitários, testes de integração com Testcontainers e documentação interativa com Swagger/OpenAPI.
2. **Fase 2 — Containerização** (⚠️ Próximo Passo): Empacotamento da aplicação com Docker (build multi-stage) e orquestração do ambiente local de desenvolvimento (App + Banco) via Docker Compose.
3. **Fase 3 — Mensageria**: Introdução de processamento assíncrono orientado a eventos usando Apache Kafka para auditoria e simulação de notificações.
4. **Fase 4 — Cache & Rate Limiting**: Otimização de performance com Redis para consultas frequentes e implementação de controle de vazão de requisições.
5. **Fase 5 — Observabilidade**: Monitoramento completo com Prometheus e Grafana, tracing distribuído e logs estruturados em formato JSON.
6. **Fase 6 — Infraestrutura como Código (IaC)**: Provisionamento declarativo de recursos em nuvem usando Terraform.
7. **Fase 7 — CI/CD**: Automatização da esteira de integração e entrega contínua (CI/CD) via GitHub Actions (validação, testes, build da imagem e deploy).
8. **Fase 8 — Escalabilidade & Orquestração**: Implantação escalável da aplicação em Kubernetes (com Minikube ou K3s), definindo manifests, probes de integridade (Liveness/Readiness) e auto-scaling (HPA).

---

## 🛠️ Stack Tecnológica (Fase 1)

*   **Java 21 LTS**
*   **Spring Boot 3.5.x** (Spring Web, Spring Data JPA, Spring Validation)
*   **PostgreSQL** (Banco de dados de produção)
*   **H2 Database** (Banco de dados em memória para desenvolvimento rápido)
*   **Lombok** (Produtividade e redução de boilerplate)
*   **Springdoc OpenAPI 2.8.x (Swagger UI)** (Documentação e teste interativo da API)
*   **JUnit 5 & Mockito** (Testes unitários e mocks de comportamento)
*   **Testcontainers PostgreSQL** (Testes de integração com banco de dados real em containers Docker efêmeros)

---

## 🏛️ Arquitetura do Projeto

O projeto segue princípios de **Clean Architecture** e **DDD (Domain-Driven Design)** adaptados para a simplicidade do domínio de tarefas:

```text
src/main/java/com/banzak/todoapp/
│
├── domain/                      # Regras de Negócio Core (Entidades e Enums)
│   ├── Task.java
│   ├── TaskPriority.java
│   └── TaskStatus.java
│
├── application/                 # Casos de Uso e Lógica de Aplicação (Serviços)
│   ├── TaskService.java
│   └── TaskNotFoundException.java
│
├── infrastructure/              # Detalhes de Tecnologia e Persistência
│   └── persistence/
│       └── TaskRepository.java  # Interface Spring Data JPA
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
*   Java Development Kit (JDK) 21 instalado.
*   Docker (necessário para rodar os testes de integração com Testcontainers).

### Executando em Modo de Desenvolvimento (H2)
Por padrão, a aplicação inicia utilizando o banco de dados em memória **H2**.
```bash
./mvnw spring-boot:run
```
*   **API URL:** `http://localhost:8080`
*   **Console H2:** `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:todoapp`, Username: `sa`, Password: vazio)
*   **Documentação Swagger:** `http://localhost:8080/swagger-ui.html`

### Executando com PostgreSQL Local
Para rodar apontando para um banco PostgreSQL externo/local configurado na porta `5432`:
```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=postgres
```

---

## 🧪 Rodando os Testes

A base de testes conta com testes unitários, testes de integração reais (usando Docker/Testcontainers) e testes de contrato HTTP (MockMvc).

Execute todos os testes com o comando:
```bash
./mvnw clean test
```

---

## 🧑‍💻 Desenvolvedor
*   **Leonardo Santana** (Pleno)
*   LinkedIn: [linkedin.com/in/banzak](https://linkedin.com/in/banzak)
*   GitHub: [github.com/banzak1](https://github.com/banzak1)
