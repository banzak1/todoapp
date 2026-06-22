# Decisões de Arquitetura e Design

Registro de decisões técnicas, bloqueios e lições aprendidas durante o desenvolvimento.

---

## 2026-06-22 — Kickoff do Projeto

### DEC-001: WSL2 Ubuntu como ambiente principal
**Decisão:** Desenvolver no WSL2 Ubuntu, não no Windows nativo.
**Motivo:** Docker, Kafka, Redis e CI/CD têm melhor suporte e performance no Linux. Tutoriais e documentação assumem Unix.
**Como aplicar:** VS Code + Remote-WSL. Código na OneDrive acessível de ambos os OS.

### DEC-002: Java 21 + Spring Boot 3 como stack base
**Decisão:** Usar Java 21 LTS e Spring Boot 3.x.
**Motivo:** Java 21 é LTS com suporte até 2029. Virtual threads, records, pattern matching. Spring Boot 3 é o padrão de mercado.
**Como aplicar:** Inicializar com Spring Initializr (Maven, Java 21, Spring Boot 3.x).

### DEC-003: 8 fases progressivas, mesmo app
**Decisão:** Evoluir o mesmo código base em vez de criar projetos separados.
**Motivo:** Simula crescimento real de sistemas. Evita o "reset" de aprendizado. Gera portfólio com história.
**Como aplicar:** Cada fase é uma branch nova ou um milestone. A Fase 1 é o MVP.

### DEC-004: Clean Architecture como padrão
**Decisão:** Usar Clean Architecture / Hexagonal (Ports & Adapters).
**Motivo:** Separa domínio de infraestrutura, facilita testabilidade e troca de tecnologias entre fases.
**Como aplicar:** Pacotes `domain`, `application`, `infrastructure`, `interfaces`.

### DEC-005: Vault Obsidian + CLAUDE.md como fonte de contexto
**Decisão:** Usar o vault Obsidian (`notes/`) como armazenamento primário de contexto do projeto, complementado por `CLAUDE.md` para diretrizes do assistente.
**Motivo:** O vault é navegável no Obsidian (graph view, backlinks), acessível de qualquer ambiente (Windows/Ubuntu via OneDrive), e o Claude Code carrega esses arquivos para continuidade entre sessões.
**Como aplicar:** Todo início de sessão, carregar `notes/Visão Geral.md`, `notes/Roadmap.md`, `notes/Decisões.md`, `notes/Diretrizes.md` e o último resumo de sessão.

---

*Voltar para: [[Visão Geral do Projeto]]*
