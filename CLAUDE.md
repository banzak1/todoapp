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
- `notes/Sessão 2026-06-22.md` (última sessão)

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

## Contexto da Última Sessão

Carregar `notes/Sessão 2026-06-22.md` para continuidade. Pontos principais:
1. Currículo analisado — gaps identificados (métricas, Docker, observabilidade, CI/CD)
2. WSL2 Ubuntu recomendado como ambiente principal
3. Estrutura do projeto definida com 8 fases progressivas
4. Content-engine skill instalada para LinkedIn
5. Próximo passo: inicializar a Fase 1 (API REST em Java/Spring Boot)
