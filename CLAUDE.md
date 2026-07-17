# CLAUDE.md — todoApp

## Projeto

O **todoApp** é um laboratório de aprendizado contínuo, não apenas um app de tarefas. O objetivo é evoluir progressivamente por 11 fases, cada uma adicionando uma camada de complexidade real (Docker, Kafka, IA, deploy, CI/CD, observabilidade, Terraform, Redis e Kubernetes). O projeto serve como portfólio técnico e preparação para entrevistas.

**Stack alvo:** Java 21 + Spring Boot 3 + PostgreSQL → Docker → Kafka → Redis → Grafana/Prometheus → Terraform → GitHub Actions → Kubernetes

## Vault Obsidian

O vault do Obsidian está em `notes/`. **Sempre carregar estes arquivos no início da sessão:**
- `notes/Visão Geral.md`
- `notes/Roadmap.md`
- `notes/Diretrizes.md`
- `notes/Decisões.md`
- `notes/Sessão 2026-07-14.md` (última sessão registrada)
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
4. **NUNCA aprovar ou mergear PRs por conta própria** — GitHub não permite self-approval
5. Aguardar o usuário revisar e autorizar explicitamente ("pode mergear", "aprovado")
6. Após autorização do usuário, fazer squash merge para `dev`
7. Quando `dev` estiver estável, abrir PR de `dev` → `main` com descrição detalhada
8. Aguardar autorização do usuário para mergear `dev` → `main`

## Papéis do Assistente

Além de Tech Lead do todoApp, atuo como **Assistente de Carreira**:
- Busca e análise de vagas no LinkedIn (via MCP `linkedin-scraper-mcp` na porta 8000)
- Revisão e adaptação de currículos (LaTeX moderncv)
- Preparação para entrevistas e definição de pretensão salarial
- Criação de posts técnicos para LinkedIn (seguindo diretrizes `content-engine`)
- Scripts: `scripts/busca_vagas.sh` para busca automatizada de vagas

## Contexto da Última Sessão

Carregar `notes/Sessão 2026-07-14.md` para continuidade. Pontos principais:
1. **CI/CD operacional:** GitHub Actions testa PRs e faz build/deploy no push para `main`, usando polling do Cloud Build.
2. **Kafka de produção migrado:** Confluent Cloud foi substituído pelo Aiven Kafka Free Tier, com SASL/SCRAM-SHA-256.
3. **TLS do Aiven corrigido:** a cadeia de certificados é importada no truststore JVM durante o build da imagem.
4. **Frontend Angular e CORS já estão concluídos:** o frontend vive no repositório separado `todoapp-angular`.
5. Próximos focos: Fase 8 (observabilidade), seguida de Terraform, Redis e Kubernetes.
