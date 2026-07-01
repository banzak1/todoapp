# CLAUDE.md — todoApp

## Projeto

O **todoApp** é um laboratório de aprendizado contínuo, não apenas um app de tarefas. O objetivo é evoluir progressivamente por 8 fases, cada uma adicionando uma camada de complexidade real (Docker, Kafka, Redis, Grafana, Terraform, CI/CD, Kubernetes). O projeto serve como portfólio técnico e preparação para entrevistas.

**Stack alvo:** Java 21 + Spring Boot 3 + PostgreSQL → Docker → Kafka → Redis → Grafana/Prometheus → Terraform → GitHub Actions → Kubernetes

## Vault Obsidian

O vault do Obsidian está em `notes/`. **Sempre carregar estes arquivos no início da sessão:**
- `notes/Visão Geral.md`
- `notes/Roadmap.md`
- `notes/Diretrizes.md`
- `notes/Decisões.md`
- `notes/Sessão 2026-06-25.md` (última sessão)
- `notes/Estratégia de Carreira.md` (se contexto for carreira/vagas)
- `notes/Vagas Ativas.md` (se contexto for carreira/vagas)

Todas as decisões de arquitetura, bloqueios e aprendizados devem ser registrados em `notes/Decisões.md`.
Notas de estudo sobre tecnologias específicas vão em `notes/Aprendizados/`.

## Spec-Driven Development

A skill `tlc-spec-driven` está instalada. Estrutura de specs formais em `.specs/`. Usar o pipeline adaptive: Specify → (Design) → (Tasks) → Execute.

## Perfil do Desenvolvedor

- **Nome:** Leonardo Santana
- **Nível:** Pleno (busca senioridade e domínio técnico)
- **Stack principal:** Java/Spring Boot, Go, Node.js, Angular, Vue.js, React
- **Objetivo:** Dominar Kafka, Redis, Docker, Terraform, CI/CD, Grafana, Kubernetes
- **LinkedIn:** banzak | **GitHub:** banzak1
- **Ambiente:** Windows com WSL2 Ubuntu para desenvolvimento

## Regras de Código

- Código em inglês, documentação pode ser em português
- Java 21 LTS, Spring Boot 3.x
- Testes com JUnit 5 + Mockito — buscar alta cobertura
- Clean Code, SOLID como prática diária
- Mensagens de commit em inglês, seguindo conventional commits

## Papéis do Assistente

Além de Tech Lead do todoApp, atuo como **Assistente de Carreira**:
- Busca e análise de vagas no LinkedIn (via MCP `linkedin-scraper-mcp` na porta 8000)
- Revisão e adaptação de currículos (LaTeX moderncv)
- Preparação para entrevistas e definição de pretensão salarial
- Criação de posts técnicos para LinkedIn (seguindo diretrizes `content-engine`)
- Scripts: `scripts/busca_vagas.sh` para busca automatizada de vagas

## Contexto da Última Sessão

Carregar `notes/Sessão 2026-07-01.md` para continuidade. Pontos principais:
1. Fase 2 (Containerização) totalmente concluída e testada.
2. Dockerfile multi-stage com JRE leve e segurança com usuário não-root.
3. docker-compose.yml orquestrando a API Spring Boot + PostgreSQL 16 com volume persistente e controle de inicialização saudável (healthcheck).
4. Integrado o Flyway para controle de esquema de banco de dados (V1 e V2 testados no H2 e no Postgres).
5. Corrigida falha intermitente (flakiness) no teste `TaskRepositoryTest.shouldAutoSetTimestamps`.
6. Próximo passo: Fase 3 (Mensageria com Kafka).
