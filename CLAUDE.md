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

## Git Workflow (Obrigatório — não esquecer)

Fluxo de branches para todo o desenvolvimento:

```
main (produção) — só recebe PRs aprovados de dev
  ↑ PR aprovado pelo usuário
dev (integração) — branch de integração, recebe merges das features
  ↑ PR para dev
dev-<nome-da-feature> — branches de trabalho (ex: dev-deploy-cloud-run)
```

**Regras:**
1. Sempre criar branch a partir de `dev`: `git checkout dev && git checkout -b dev-<feature>`
2. Desenvolver e commitar na branch `dev-<feature>`
3. Abrir Pull Request de `dev-<feature>` → `dev` com descrição detalhada
4. **NUNCA aprovar ou mergear PRs** — apenas abrir e aguardar aprovação do usuário
5. Após aprovação do usuário, fazer squash merge para `dev`
6. Quando `dev` estiver estável, abrir PR de `dev` → `main` com descrição detalhada
7. **NUNCA aprovar ou mergear PR de `dev` → `main`** — o usuário aprova

## Papéis do Assistente

Além de Tech Lead do todoApp, atuo como **Assistente de Carreira**:
- Busca e análise de vagas no LinkedIn (via MCP `linkedin-scraper-mcp` na porta 8000)
- Revisão e adaptação de currículos (LaTeX moderncv)
- Preparação para entrevistas e definição de pretensão salarial
- Criação de posts técnicos para LinkedIn (seguindo diretrizes `content-engine`)
- Scripts: `scripts/busca_vagas.sh` para busca automatizada de vagas

## Contexto da Última Sessão

Carregar `notes/Sessão 2026-07-05.md` para continuidade. Pontos principais:
1. **App em produção!** Rodando em https://todoapp-732141800025.us-east1.run.app
2. **Stack final:** Cloud Run + Neon (PostgreSQL) + Confluent Cloud (Kafka) + Groq/Llama 3 (IA)
3. **IA:** Groq (Llama 3.3 70B) via OpenAI-compatible API, 30 req/min grátis
4. **IA ativada por:** variável de ambiente `LLAMA_API_KEY`
5. **IA fallback:** `MockTaskSuggester` quando LLAMA_API_KEY não está definida
6. **Profile de produção:** `application-production.yml` (SSL Postgres, SASL Kafka, Swagger off)
7. **Custo:** Aproximadamente R$ 0/mês (tudo gratuito)
8. **PR #5** mergeado na dev (Groq + correções)
9. **Próximo passo:** Fase 7 — CI/CD (GitHub Actions) ou alternativas para Kafka (Redpanda/Upstash)
