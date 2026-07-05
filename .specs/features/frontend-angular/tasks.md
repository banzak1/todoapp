# Frontend Angular — Tasks

**Spec**: `.specs/features/frontend-angular/spec.md`
**Design**: `.specs/features/frontend-angular/design.md`
**Status**: In Progress

---

## Execution Plan

### Phase 1: Foundation (Sequential)

```
T1 (Create Repo) → T2 (Init Angular) → T3 (CI/CD Repo)
```

### Phase 2: Data Layer + Layout (Parallel OK)

After foundation:
```
T2 ──┬── T4 (Models + Services)
     └── T5 (Header + Shared Components + Routes)
```

### Phase 3: Pages (Sequential — each builds on shared components)

```
T4 + T5 → T6 (TaskList) → T7 (TaskForm + Detail) → T8 (IA Integration)
```

### Phase 4: Polish (Parallel OK)

```
T8 ──┬── T9 (Responsividade + Refinamentos)
     └── T10 (CORS Backend)
```

---

## Task Breakdown

### T1: Create GitHub repository `todoapp-angular`

**What**: Criar repositório GitHub para o frontend Angular
**Where**: GitHub — banzak1/todoapp-angular
**Depends on**: None
**Tools**: MCP GitHub

**Done when**:
- [ ] Repositório criado com README.md
- [ ] Branch `dev` criada
- [ ] Proteção de branch configurada

---

### T2: Initialize Angular project (standalone, SCSS, Jest)

**What**: Inicializar projeto Angular 17+ com Angular CLI
**Where**: `todoapp-angular/`
**Depends on**: T1
**Reuses**: Patterns from psico-landing-page (standalone, Jest, ESLint, commitlint, husky)

**Done when**:
- [ ] `ng new` com standalone components, SCSS
- [ ] ESLint + Angular ESLint configurados
- [ ] Jest (via @angular-builders/jest) + jest-preset-angular configurado
- [ ] Commitlint + Husky configurados
- [ ] Path aliases configurados (`@/` → `src/app/`)
- [ ] `styles.scss` com design system (CSS custom properties — paleta indigo tech)
- [ ] `environment.ts` com API_URL
- [ ] `app.config.ts` com HttpClient, Router
- [ ] Build passa: `npm run build`
- [ ] Lint passa: `npm run lint`
- [ ] Testes rodam: `npm run test`

---

### T3: CI/CD workflow for todoapp-angular

**What**: Criar GitHub Actions workflow para lint, test, build
**Where**: `.github/workflows/ci-cd.yml`
**Depends on**: T2
**Reuses**: Pattern from todoapp `.github/workflows/ci-cd.yml`

**Done when**:
- [ ] Workflow de PR: `npm ci → npm run lint → npm run test → npm run build`
- [ ] Workflow de push para main: mesmo + deploy
- [ ] PR de teste passa no CI

---

### T4: Models + TaskService + AiSuggestService + ErrorInterceptor

**What**: Camada de dados completa
**Where**: `src/app/models/`, `src/app/core/services/`, `src/app/core/interceptors/`
**Depends on**: T2
**Reuses**: Patterns from psico-landing-page (services, inject pattern)

**Done when**:
- [ ] `task.model.ts` — Task, CreateTaskRequest, UpdateTaskRequest, TaskListParams
- [ ] `ai-suggestion.model.ts` — AiSuggestRequest, AiSuggestion
- [ ] `page.model.ts` — Page\<T\> interface
- [ ] `task.service.ts` — CRUD + pagination + filters
- [ ] `ai-suggest.service.ts` — Sugestão de IA
- [ ] `error.interceptor.ts` — Tratamento global de erros
- [ ] Testes unitários passando

---

### T5: Header + Shared Components + Routes + AppComponent

**What**: Layout base e componentes compartilhados
**Where**: `src/app/shared/components/`, `src/app/app.routes.ts`, `src/app/app.component.ts`
**Depends on**: T2
**Reuses**: Patterns from psico header, dashboard

**Done when**:
- [ ] HeaderComponent (logo + navegação)
- [ ] FooterComponent
- [ ] LoadingComponent (spinner)
- [ ] EmptyStateComponent (message + icon)
- [ ] ConfirmDialogComponent (modal de confirmação)
- [ ] ToastComponent (serviço + componente)
- [ ] NotFoundComponent (404)
- [ ] AppComponent com Header + RouterOutlet + Footer
- [ ] Rotas configuradas com lazy loading
- [ ] Testes unitários passando

---

### T6: TaskListPage with pagination + filters

**What**: Página principal de listagem
**Where**: `src/app/features/tasks/pages/task-list/`, `features/tasks/components/`
**Depends on**: T4, T5

**Done when**:
- [ ] TaskCardComponent (card com status, prioridade, ações)
- [ ] TaskFiltersComponent (status + priority)
- [ ] PaginationComponent (navegação páginas)
- [ ] TaskListPage com Signals state
- [ ] Loading state (skeleton)
- [ ] Empty state ("Nenhuma tarefa encontrada")
- [ ] Error state ("Tentar novamente")
- [ ] Testes unitários passando

---

### T7: TaskFormPage + TaskDetailPage (CRUD)

**What**: Páginas de criação, edição, visualização e deleção
**Where**: `src/app/features/tasks/pages/task-form/`, `task-detail/`
**Depends on**: T4, T5, T6

**Done when**:
- [ ] TaskFormPage com ReactiveForms (create + edit modes)
- [ ] Validação: título obrigatório, tamanho máximo
- [ ] TaskDetailPage com todas as informações
- [ ] Deleção com ConfirmDialog
- [ ] Toast de sucesso após operações
- [ ] Tratamento 404
- [ ] Testes unitários passando

---

### T8: AI Suggestion Integration

**What**: Botão "Sugerir com IA" no formulário
**Where**: `src/app/features/tasks/components/ai-suggest/`
**Depends on**: T7

**Done when**:
- [ ] Botão "✨ Sugerir com IA" no formulário
- [ ] Loading state no botão
- [ ] Preenchimento automático de prioridade e descrição
- [ ] Lista de subtarefas sugeridas
- [ ] Tratamento de erro (IA indisponível)
- [ ] Testes unitários passando

---

### T9: Responsividade + Refinamentos

**What**: Ajustes finais de layout e responsividade
**Where**: Múltiplos componentes (SCSS media queries)
**Depends on**: T7, T8

**Done when**:
- [ ] Desktop (≥1024px): grid multi-coluna
- [ ] Tablet (768-1023px): layout adaptativo
- [ ] Mobile (<768px): coluna única
- [ ] Transições e animações suaves
- [ ] Micro-interações (hover, focus)

---

### T10: CORS Backend

**What**: Configurar CORS no Spring Boot para aceitar requisições do frontend
**Where**: Backend — `src/main/java/com/banzak/todoapp/infrastructure/config/CorsConfig.java`
**Depends on**: T2 (saber URL do frontend)

**Done when**:
- [ ] CORS configurado para dev (localhost:4200) e produção
- [ ] Preflight OPTIONS retorna 200
- [ ] Testes de integração validam CORS
