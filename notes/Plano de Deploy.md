# Plano de Deploy — TodoApp em Produção (Custo Zero)

## Visão Geral da Arquitetura

```
[Usuário] → Cloud Run (todoapp) → Neon (PostgreSQL)
                              ↘ Confluent Cloud (Kafka)
                              ↘ Gemini API (IA)
```

| Serviço | Produto | Free Tier | Função |
|---------|---------|-----------|--------|
| **App** | Google Cloud Run | 2M req/mês, 360k GB-seg | Container serverless |
| **Banco** | Neon (PostgreSQL) | 0.5GB storage, 100h/mês | Banco de dados |
| **Mensageria** | Confluent Cloud | 50k msgs/dia | Kafka gerenciado |
| **IA** | Google Gemini API | Grátis (60 req/min, 1500 req/dia) | Sugestões de tarefas |
| **Registry** | Artifact Registry | 0.5GB/mês free | Imagens Docker |

---

## Fase 1 — Banco de Dados (Neon)

### 1.1 Criar conta no Neon
1. Acessar [neon.tech](https://neon.tech)
2. Login com GitHub Google
3. Criar um projeto `todoapp`

### 1.2 Criar o banco
```bash
# Neon já cria um banco "neondb" automaticamente, mas vamos criar um dedicado
# Pelo console Neon:
# - Project Name: todoapp
# - Region: US East (us-east-1) — perto do Cloud Run
```

### 1.3 Obter connection string
```
postgresql://<user>:<password>@<host>.us-east-1.aws.neon.tech/neondb?sslmode=require
```

### 1.4 Rodar as migrations Flyway
```bash
# Opção 1: Deixar o Flyway rodar na inicialização do app (automático)
# Opção 2: Rodar manualmente via Maven
./mvnw flyway:migrate \
  -Dflyway.url=jdbc:postgresql://<host>.us-east-1.aws.neon.tech/neondb?sslmode=require \
  -Dflyway.user=<user> \
  -Dflyway.password=<password>
```

---

## Fase 2 — Kafka (Confluent Cloud)

### 2.1 Criar conta no Confluent Cloud
1. Acessar [confluent.cloud](https://confluent.cloud)
2. Login com Google GitHub
3. Ativar free trial (não pede cartão no trial inicial)
4. Criar cluster Basic

### 2.2 Configurar o cluster
```bash
# Cluster type: Basic (free tier)
# Cloud: Google Cloud
# Region: us-east1 (mesma região do Cloud Run)
# Cluster name: todoapp-kafka
```

### 2.3 Criar API Key
```bash
# No console: Cluster → Data Integration → API Keys → Create Key
# Salvar:
# - Key: <api-key>
# - Secret: <api-secret>
# - Bootstrap server: <broker>.us-east1.gcp.confluent.cloud:9092
```

### 2.4 Criar os tópicos
```bash
# Criar pelo console ou CLI:
# - todo-tasks (1 partition, 1 replica)
# - todo-tasks-dlt (1 partition, 1 replica)
```

---

## Fase 3 — Container Registry (Artifact Registry)

### 3.1 Ativar Google Cloud SDK
```bash
# Instalar se não tiver
# No WSL2 Ubuntu:
sudo apt-get install google-cloud-sdk

# Autenticar (abre browser)
gcloud auth login

# Configurar projeto (ou criar um novo)
gcloud projects create todoapp-<seu-id> --name="TodoApp"
gcloud config set project todoapp-<seu-id>

# Ativar billing (necessário mesmo para free tier)
# Acessar: https://console.cloud.google.com/billing
```

### 3.2 Ativar serviços necessários
```bash
gcloud services enable run.googleapis.com
gcloud services enable artifactregistry.googleapis.com
gcloud services enable cloudbuild.googleapis.com
```

### 3.3 Criar repositório de imagens
```bash
gcloud artifacts repositories create todoapp-repo \
  --repository-format=docker \
  --location=us-east1
```

---

## Fase 4 — Profile de Produção (Código)

Precisamos criar um `application-production.yml` para configurar:
- SSL do PostgreSQL (Neon exige `sslmode=require`)
- SASL do Confluent Cloud
- Kafka consumer ativado
- Swagger desligado

Este profile substitui o `application-postgres.yml` quando em produção.

---

## Fase 5 — Deploy no Cloud Run

### 5.1 Build da imagem usando Cloud Build
```bash
gcloud builds submit \
  --tag us-east1-docker.pkg.dev/todoapp-<id>/todoapp-repo/todoapp:latest
```

### 5.2 Deploy
```bash
gcloud run deploy todoapp \
  --image us-east1-docker.pkg.dev/todoapp-<id>/todoapp-repo/todoapp:latest \
  --platform managed \
  --region us-east1 \
  --allow-unauthenticated \
  --memory=512Mi \
  --cpu=1 \
  --min-instances=0 \
  --max-instances=2 \
  --concurrency=80 \
  --timeout=300 \
  --set-env-vars="SPRING_PROFILES_ACTIVE=production" \
  --set-env-vars="SPRING_DATASOURCE_URL=jdbc:postgresql://<neon-host>/neondb?sslmode=require" \
  --set-env-vars="SPRING_DATASOURCE_USERNAME=<user>" \
  --set-env-vars="SPRING_DATASOURCE_PASSWORD=<password>" \
  --set-env-vars="SPRING_KAFKA_BOOTSTRAP_SERVERS=<confluent-broker>:9092" \
  --set-env-vars="SPRING_KAFKA_PROPERTIES_SASL_JAAS_CONFIG=org.apache.kafka.common.security.plain.PlainLoginModule required username='<api-key>' password='<api-secret>';" \
  --set-env-vars="SPRING_KAFKA_PROPERTIES_SASL_MECHANISM=PLAIN" \
  --set-env-vars="SPRING_KAFKA_PROPERTIES_SECURITY_PROTOCOL=SASL_SSL" \
  --set-secrets="GEMINI_API_KEY=gemini-key:latest"
```

### 5.3 Configurar Secret do Gemini
```bash
# Obter key em: https://aistudio.google.com/apikey
# Criar secret no Secret Manager
echo -n "AIzaSy..." | gcloud secrets create gemini-key --data-file=-

# Dar acesso ao Cloud Run
gcloud secrets add-iam-policy-binding gemini-key \
  --member=serviceAccount:<projeto-number>-compute@developer.gserviceaccount.com \
  --role=roles/secretmanager.secretAccessor
```

---

## Fase 6 — Verificação

### 6.1 Testar endpoints
```bash
# URL do Cloud Run (ex: https://todoapp-xxxxx-ue.a.run.app)
curl https://todoapp-xxxxx-ue.a.run.app/api/v1/tasks
curl https://todoapp-xxxxx-ue.a.run.app/api/v1/tasks/ai/suggest \
  -X POST \
  -H "Content-Type: application/json" \
  -d '{"title":"Estudar Kubernetes"}'
```

### 6.2 Verificar logs
```bash
gcloud logging read "resource.type=cloud_run_revision AND resource.labels.service_name=todoapp" --limit 20
```

### 6.3 Verificar métricas
```bash
# Acessar Console GCP > Cloud Run > todoapp > Metrics
# Ou via gcloud:
gcloud run services list --platform managed
```

---

## Mapa de Variáveis de Ambiente

| Variável | Local (H2) | Local (Docker) | Produção (Cloud Run) |
|----------|-----------|----------------|---------------------|
| `SPRING_PROFILES_ACTIVE` | — | `postgres` | `production` |
| `SPRING_DATASOURCE_URL` | H2 mem | `jdbc:postgresql://db:5432/todoapp` | Neon (SSL) |
| `SPRING_DATASOURCE_USERNAME` | `sa` | `postgres` | Neon user |
| `SPRING_DATASOURCE_PASSWORD` | — | `postgres` | Neon password |
| `SPRING_KAFKA_BOOTSTRAP_SERVERS` | — | `kafka:9092` | Confluent broker |
| `SPRING_KAFKA_LISTENER_AUTO_STARTUP` | `false` | `true` | `true` |
| `GEMINI_API_KEY` | — | — | Secret Manager |
| Kafka SASL | — | — | Confluent API Key |

---

## Custos Mensais Estimados (Free Tier)

| Serviço | Custo | Limite Free Tier |
|---------|-------|------------------|
| Cloud Run | R$ 0 | 2M req, 360k GB-seg, 180k vCPU-seg |
| Neon | R$ 0 | 0.5GB, 100h computo/mês |
| Confluent Cloud | R$ 0 | 50k msgs/dia |
| Artifact Registry | R$ 0 | 0.5GB/mês |
| Gemini API | R$ 0 | 60 req/min, 1500 req/dia |
| Secret Manager | R$ 0 | 6 secrets/mês |

**Total: R$ 0/mês** (tudo gratuito!) 🎉

---

## Fase 7 — CI/CD (GitHub Actions)

### Workflow
O arquivo `.github/workflows/ci-cd.yml` já está criado com:

| Evento | Ação |
|--------|------|
| `pull_request` para `dev` ou `main` | ✅ Roda testes (`mvnw test`) |
| `push` para `main` | ✅ Roda testes + build Docker + deploy no Cloud Run |

### Pré-requisito: Criar Service Account no GCP

O deploy automático precisa de uma Service Account do GCP para autenticar:

```bash
# 1. Criar a service account
gcloud iam service-accounts create github-actions \
  --display-name="GitHub Actions" \
  --project=todoapp-banzak

# 2. Atribuir permissões
gcloud projects add-iam-policy-binding todoapp-banzak \
  --member="serviceAccount:github-actions@todoapp-banzak.iam.gserviceaccount.com" \
  --role="roles/run.admin"

gcloud projects add-iam-policy-binding todoapp-banzak \
  --member="serviceAccount:github-actions@todoapp-banzak.iam.gserviceaccount.com" \
  --role="roles/artifactregistry.admin"

gcloud projects add-iam-policy-binding todoapp-banzak \
  --member="serviceAccount:github-actions@todoapp-banzak.iam.gserviceaccount.com" \
  --role="roles/cloudbuild.builds.editor"

gcloud projects add-iam-policy-binding todoapp-banzak \
  --member="serviceAccount:github-actions@todoapp-banzak.iam.gserviceaccount.com" \
  --role="roles/storage.objectAdmin"

# 3. Criar e baixar a chave JSON
gcloud iam service-accounts keys create gcp-sa-key.json \
  --iam-account=github-actions@todoapp-banzak.iam.gserviceaccount.com
```

### Configurar Secret no GitHub
1. Acessar **Settings > Secrets and variables > Actions**
2. Criar secret: `GCP_SA_KEY`
3. Colar o conteúdo do arquivo `gcp-sa-key.json` (baixado acima)

⚠️ **Nunca commitar o `gcp-sa-key.json` no repositório!**

### Ambiente de Produção
As variáveis de ambiente sensíveis (Neon, Confluent, LLAMA_API_KEY) precisam ser configuradas manualmente no Cloud Run via Console ou comando `gcloud run deploy --set-env-vars`, pois não devem ficar no repositório.

---

*Voltar para: [[Visão Geral do Projeto]]*
*Ver também: [[Roadmap]], [[Regras de Negócio]]*
