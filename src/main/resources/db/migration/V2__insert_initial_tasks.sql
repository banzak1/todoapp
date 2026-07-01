INSERT INTO tasks (id, title, description, status, priority, created_at, updated_at)
VALUES (nextval('tasks_seq'), 'Fase 1: Fundação', 'Concluir a base da API REST com testes e H2/PostgreSQL', 'DONE', 'HIGH', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO tasks (id, title, description, status, priority, created_at, updated_at)
VALUES (nextval('tasks_seq'), 'Fase 2: Containerização', 'Configurar Docker e Docker Compose com Flyway para controle de banco', 'IN_PROGRESS', 'HIGH', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO tasks (id, title, description, status, priority, created_at, updated_at)
VALUES (nextval('tasks_seq'), 'Fase 3: Mensageria', 'Introduzir o Apache Kafka para processamento orientado a eventos', 'TODO', 'MEDIUM', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
