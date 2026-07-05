# Frontend Angular — todoapp-angular

## Problem Statement

O todoApp atualmente é apenas uma API REST — sem interface visual, consumível apenas via Swagger, curl ou Postman. Para tornar o projeto acessível a usuários reais e demonstrar a integração frontend-backend, é necessário um frontend web moderno.

O frontend será um projeto Angular separado (repositório `todoapp-angular`), consumindo a API do backend hospedada no Cloud Run.

## Goals

- [ ] Aplicação Angular standalone funcional consumindo a API REST do todoApp
- [ ] Listagem de tarefas com paginação e filtros (status, priority)
- [ ] CRUD completo de tarefas (criar, visualizar, editar, deletar)
- [ ] Integração com IA para sugestão de prioridades e subtarefas
- [ ] Design responsivo (mobile + desktop)
- [ ] CI/CD próprio para build, teste e deploy
- [ ] Cobertura de testes >80% com Jest

## Out of Scope

| Feature | Reason |
|---------|--------|
| Autenticação/Autorização | Será adicionada em fase futura |
| WebSocket/tempo real | Depende de SSE ou WebSocket no backend |
| Testes E2E (Cypress) | Pode ser adicionado depois |
| PWA/Offline | Futuro |
| SSR (Angular Universal) | Fora do escopo |
| Dark mode | P3 — nice to have |

---

## Style & Design System

Baseado nos padrões do projeto `psico-landing-page` com adaptação para tema tech:

### CSS Custom Properties (Global Styles)

```scss
:root {
  /* Tech Color Palette */
  --color-primary: #6366F1;        /* Indigo */
  --color-primary-hover: #4F46E5;
  --color-primary-light: #EEF2FF;
  --color-secondary: #8B5CF6;      /* Violet */
  --color-accent: #06B6D4;         /* Cyan */
  
  --color-background: #F8FAFC;     /* Slate 50 */
  --color-surface: #FFFFFF;
  --color-surface-hover: #F1F5F9;
  --color-border: #E2E8F0;
  
  --color-text-primary: #0F172A;   /* Slate 900 */
  --color-text-secondary: #64748B; /* Slate 500 */
  --color-text-muted: #94A3B8;     /* Slate 400 */
  
  /* Status Colors */
  --color-status-todo: #94A3B8;
  --color-status-progress: #3B82F6;
  --color-status-done: #22C55E;
  
  /* Priority Colors */
  --color-priority-low: #22C55E;
  --color-priority-medium: #EAB308;
  --color-priority-high: #EF4444;
  
  /* Typography */
  --font-sans: 'Inter', -apple-system, sans-serif;
  --font-mono: 'JetBrains Mono', 'Fira Code', monospace;
  
  /* Spacing */
  --spacing-xs: 0.5rem;
  --spacing-sm: 1rem;
  --spacing-md: 1.5rem;
  --spacing-lg: 2rem;
  --spacing-xl: 3rem;
  
  /* Borders */
  --radius-sm: 6px;
  --radius-md: 10px;
  --radius-lg: 16px;
  
  /* Shadows */
  --shadow-sm: 0 1px 3px rgba(15, 23, 42, 0.05);
  --shadow-md: 0 4px 12px rgba(15, 23, 42, 0.08);
  --shadow-lg: 0 12px 32px rgba(15, 23, 42, 0.12);
  
  /* Transitions */
  --transition-fast: 150ms ease;
  --transition-normal: 250ms ease;
}
```

### Layout Principles
- **Clean & minimal**: Espaço em branco generoso, bordas sutis
- **Cards**: Elementos em cards com border-radius e shadow suave
- **Hierarquia visual**: Headers serif/sans-serif, tamanhos bem definidos
- **Micro-interações**: Hover states, transições suaves em cards e botões
- **Responsivo**: Grid adaptativo, mobile-first

---

## Architecture & Project Structure

```
todoapp-angular/
├── src/
│   ├── app/
│   │   ├── app.component.ts           # Standalone root component
│   │   ├── app.config.ts              # App providers (HTTP, router, etc.)
│   │   ├── app.routes.ts              # Root routes
│   │   ├── core/
│   │   │   ├── services/
│   │   │   │   ├── task.service.ts
│   │   │   │   └── ai-suggest.service.ts
│   │   │   └── interceptors/
│   │   │       └── error.interceptor.ts
│   │   ├── features/
│   │   │   ├── tasks/
│   │   │   │   ├── tasks.routes.ts
│   │   │   │   ├── pages/
│   │   │   │   │   ├── task-list/
│   │   │   │   │   ├── task-detail/
│   │   │   │   │   └── task-form/
│   │   │   │   └── components/
│   │   │   │       ├── task-card/
│   │   │   │       ├── task-filters/
│   │   │   │       └── pagination/
│   │   │   └── not-found/
│   │   ├── shared/
│   │   │   ├── components/
│   │   │   │   ├── header/
│   │   │   │   ├── loading/
│   │   │   │   ├── empty-state/
│   │   │   │   └── confirm-dialog/
│   │   │   └── pipes/
│   │   │       └── priority-color.pipe.ts
│   │   └── models/
│   │       ├── task.model.ts
│   │       └── ai-suggestion.model.ts
│   ├── environments/
│   │   ├── environment.ts
│   │   └── environment.prod.ts
│   ├── styles.scss
│   ├── index.html
│   └── main.ts
├── .github/workflows/
│   └── ci-cd.yml
├── angular.json
├── package.json
└── README.md
```

### Key Patterns (from psico-landing-page)

- **Standalone Components**: No NgModules, `@Component({ standalone: true, imports: [...] })`
- **Signals**: `signal()`, `computed()`, `effect()` para estado reativo
- **`inject()`**: Injeção de dependência funcional (não constructor-based)
- **`takeUntilDestroyed`**: Gerenciamento de subscriptions
- **Feature structure**: `features/<name>/pages/` + `features/<name>/components/`
- **Lazy loading**: Rotas carregadas via `loadChildren` com `/* webpackChunkName */`
- **SCSS**: Estilos component-scoped com variáveis globais CSS
- **Path aliases**: `@/` → `src/app/` (ou similar)

---

## User Stories

### P1: Listar Tarefas Paginadas ⭐ MVP

**User Story**: Como usuário, quero ver a lista de tarefas paginada com filtros.

**Why P1**: Tela principal do app.

**Acceptance Criteria**:

1. WHEN usuário acessa `/` THEN sistema SHALL exibir lista paginada de tarefas (20 por página)
2. WHEN usuário clica em "Próxima"/"Anterior" THEN sistema SHALL navegar entre páginas
3. WHEN usuário seleciona filtro de status THEN sistema SHALL filtrar a lista via API
4. WHEN usuário seleciona filtro de prioridade THEN sistema SHALL filtrar a lista via API
5. WHEN não há tarefas THEN sistema SHALL exibir empty state "Nenhuma tarefa encontrada"
6. WHEN API retorna erro THEN sistema SHALL exibir mensagem de erro com try again
7. WHEN carregando THEN sistema SHALL exibir skeleton loader

### P1: Criar Tarefa ⭐ MVP

**User Story**: Como usuário, quero criar uma nova tarefa.

**Why P1**: Operação fundamental.

**Acceptance Criteria**:

1. WHEN usuário clica "Nova Tarefa" THEN sistema SHALL exibir formulário (modal ou página)
2. WHEN usuário preenche título (obrigatório) e opcionais e salva THEN sistema SHALL criar e redirecionar
3. WHEN título está vazio THEN sistema SHALL exibir validação inline "Título é obrigatório"
4. WHEN criação bem-sucedida THEN sistema SHALL exibir toast de sucesso
5. WHEN API retorna erro THEN sistema SHALL exibir toast de erro

### P1: Visualizar Tarefa ⭐ MVP

**User Story**: Como usuário, quero ver detalhes completos de uma tarefa.

**Acceptance Criteria**:

1. WHEN usuário clica em uma tarefa na lista THEN sistema SHALL exibir página de detalhes
2. WHEN tarefa não existe THEN sistema SHALL exibir "Tarefa não encontrada" (404)
3. WHEN carregando THEN sistema SHALL exibir loading spinner

### P1: Editar Tarefa ⭐ MVP

**User Story**: Como usuário, quero editar uma tarefa existente.

**Acceptance Criteria**:

1. WHEN usuário clica "Editar" THEN sistema SHALL exibir formulário pré-preenchido
2. WHEN usuário altera campos e salva THEN sistema SHALL atualizar e redirecionar
3. WHEN API retorna 404 THEN sistema SHALL exibir "Tarefa não encontrada"

### P1: Deletar Tarefa ⭐ MVP

**User Story**: Como usuário, quero remover tarefas.

**Acceptance Criteria**:

1. WHEN usuário clica "Deletar" THEN sistema SHALL exibir modal de confirmação
2. WHEN usuário confirma THEN sistema SHALL deletar e remover da lista com toast
3. WHEN usuário cancela THEN sistema SHALL não deletar

### P2: Sugestão de IA ⭐

**User Story**: Como usuário, quero que a IA sugira prioridade e subtarefas.

**Why P2**: Diferencial do app, demonstra integração com IA.

**Acceptance Criteria**:

1. WHEN usuário clica "Sugerir com IA" no formulário THEN sistema SHALL chamar `POST /api/v1/tasks/ai/suggest`
2. WHEN IA retorna sugestão THEN sistema SHALL preencher prioridade, descrição e exibir subtarefas
3. WHEN IA está indisponível THEN sistema SHALL exibir "IA temporariamente indisponível"
4. WHEN carregando THEN sistema SHALL exibir loading no botão

### P2: Layout Responsivo

**User Story**: Como usuário, quero que o app funcione bem em qualquer dispositivo.

**Acceptance Criteria**:

1. WHEN ≥1024px THEN sistema SHALL exibir layout desktop com sidebar/multi-coluna
2. WHEN <768px THEN sistema SHALL exibir layout mobile de coluna única
3. WHEN tablet THEN sistema SHALL exibir layout adaptativo

### P2: CI/CD Frontend

**User Story**: Como desenvolvedor, quero deploy automático do frontend.

**Acceptance Criteria**:

1. WHEN PR aberto para `dev` THEN GitHub Actions SHALL rodar lint + testes
2. WHEN push para `main` THEN GitHub Actions SHALL build + deploy (Vercel/Cloud Run)
3. WHEN deploy falha THEN sistema SHALL notificar falha

---

## Edge Cases

- WHEN API fora do ar (503/0) THEN sistema SHALL exibir "Serviço temporariamente indisponível"
- WHEN rota inexistente THEN sistema SHALL exibir página 404
- WHEN formulário fechado com dados não salvos THEN sistema SHALL alertar (P3)
- WHEN lista vazia com filtro THEN sistema SHALL exibir "Nenhuma tarefa encontrada para este filtro"
- WHEN título tem apenas espaços THEN sistema SHALL validar como vazio
- WHEN descrição longa THEN sistema SHALL truncar na lista com "Ver mais"
- WHEN resolução muda THEN sistema SHALL adaptar layout via media queries
- WHEN 2 requisições simultâneas THEN sistema SHALL lidar sem race conditions

---

## Requirement Traceability

| ID | Story | Phase | Status |
|----|-------|-------|--------|
| ANG-01 | P1: Listar Tarefas Paginadas | Specify | Pending |
| ANG-02 | P1: Filtrar por Status | Specify | Pending |
| ANG-03 | P1: Filtrar por Priority | Specify | Pending |
| ANG-04 | P1: Criar Tarefa | Specify | Pending |
| ANG-05 | P1: Visualizar Tarefa | Specify | Pending |
| ANG-06 | P1: Editar Tarefa | Specify | Pending |
| ANG-07 | P1: Deletar Tarefa (com confirmação) | Specify | Pending |
| ANG-08 | P1: Loading/Error/Empty states | Specify | Pending |
| ANG-09 | P1: Toast de notificação | Specify | Pending |
| ANG-10 | P2: Sugestão de IA | Specify | Pending |
| ANG-11 | P2: Layout Responsivo | Specify | Pending |
| ANG-12 | P2: CI/CD Frontend | Specify | Pending |
| ANG-13 | P2: Skeleton Loader | Specify | Pending |
| ANG-14 | P2: Toast notifications | Specify | Pending |

---

## Success Criteria

- [ ] Angular app funcional consumindo API do todoApp em produção
- [ ] CRUD completo operacional (criar, listar, ver, editar, deletar)
- [ ] Paginação e filtros funcionando com a API
- [ ] Integração com IA operacional
- [ ] App responsivo (mobile-first, desktop + tablet + mobile)
- [ ] Loading, empty e error states em todas as telas
- [ ] CI/CD pipeline próprio (build + test + deploy)
- [ ] Cobertura de testes >80% (Jest)
