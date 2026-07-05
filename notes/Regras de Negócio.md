# Regras de Negócio — TodoApp

Documentação das regras de negócio, fluxos e estrutura do sistema.

---

## 1. Domínio: Task (Tarefa)

### Entidade
```
Task {
  id: Long (gerado por sequência)
  title: String (obrigatório, até 255 caracteres)
  description: String (opcional)
  status: TaskStatus (enum)
  priority: TaskPriority (enum)
  createdAt: LocalDateTime (preenchido automaticamente)
  updatedAt: LocalDateTime (atualizado automaticamente)
}
```

### Enums
| Enum | Valores | Descrição |
|------|---------|-----------|
| `TaskStatus` | `PENDING, IN_PROGRESS, COMPLETED, CANCELLED` | Ciclo de vida |
| `TaskPriority` | `LOW, MEDIUM, HIGH` | Nível de prioridade |

### Regras de Validação
- **Title** é obrigatório e não pode ser vazio
- **Status** inicial padrão: `PENDING`
- **Priority** inicial padrão: `MEDIUM`
- **createdAt** é gerado no momento da criação (não aceito via request)
- **updatedAt** é atualizado automaticamente em toda modificação
- Status só aceita os valores do enum (validação via `@JsonProperty`)

---

## 2. Fluxo de CRUD

```
[Cliente] → POST /api/v1/tasks         → Cria tarefa (status=PENDING)
[Cliente] → GET  /api/v1/tasks          → Lista todas (sem página)
[Cliente] → GET  /api/v1/tasks/page     → Lista paginada (com filtros)
[Cliente] → GET  /api/v1/tasks/{id}     → Busca por ID
[Cliente] → PUT  /api/v1/tasks/{id}     → Atualiza tarefa
[Cliente] → DELETE /api/v1/tasks/{id}   → Remove tarefa
```

### Filtros (endpoint paginado)
- `status`: filtra por `TaskStatus`
- `priority`: filtra por `TaskPriority`
- `page`: número da página (0-indexed)
- `size`: tamanho da página

---

## 3. Fluxo de Eventos (Kafka)

### Eventos Publicados
| Operação | Evento | Tópico |
|----------|--------|--------|
| Criar tarefa | `TaskEvent(type=CREATED, payload=task)` | `todo-tasks` |
| Atualizar tarefa | `TaskEvent(type=UPDATED, payload=task)` | `todo-tasks` |
| Deletar tarefa | `TaskEvent(type=DELETED, payload=task)` | `todo-tasks` |

### Consumidores
| Grupo | Função | Comportamento |
|-------|--------|--------------|
| `todoapp-audit` | Log de auditoria | Registra todos os eventos |
| `todoapp-notifications` | Notificações simuladas | Simula envio de notificação |

### Resiliência
- **Retry:** 3 tentativas com backoff exponencial (1s, 2s, 4s)
- **DLT:** Falhas são enviadas para `todo-tasks-dlt` após exaustão das retentativas
- **Simulação de falha:** Tarefas com título contendo "fail" ou "falha" disparam exceção proposital para testar o DLT

---

## 4. Módulo de IA (LangChain4j)

### Endpoint
```
POST /api/v1/tasks/ai/suggest
Body: { "title": "string (obrigatório)", "description": "string (opcional)" }
Response: { "suggestedPriority": "HIGH|MEDIUM|LOW", "refinedDescription": "string", "suggestedSubtasks": ["string"] }
```

### Comportamento
- **Com GEMINI_API_KEY:** Usa LangChain4j + Google Gemini 2.0 Flash para gerar sugestões reais (gratuito)
- **Sem GEMINI_API_KEY:** Retorna sugestões mockadas (prioridade HIGH, subtarefas genéricas)
- A troca entre real e mock é automática via `@ConditionalOnProperty`
- API Key gratuita em: https://aistudio.google.com/apikey

---

## 5. Estrutura do Banco (PostgreSQL)

```sql
CREATE TABLE tasks (
    id BIGINT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    priority VARCHAR(10) NOT NULL DEFAULT 'MEDIUM',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE SEQUENCE tasks_seq START WITH 1 INCREMENT BY 1;
```

### Migrations (Flyway)
| Versão | Descrição |
|--------|-----------|
| `V1__create_tasks_table.sql` | Cria tabela e sequência |
| `V2__insert_initial_tasks.sql` | Carga inicial de dados (roadmap do projeto) |

---

*Voltar para: [[Visão Geral do Projeto]]*
*Ver também: [[Roadmap]], [[Decisões]]*
