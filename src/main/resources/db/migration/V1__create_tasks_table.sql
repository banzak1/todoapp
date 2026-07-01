CREATE SEQUENCE tasks_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE tasks (
    id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    priority VARCHAR(255),
    status VARCHAR(255),
    created_at TIMESTAMP(6),
    updated_at TIMESTAMP(6),
    CONSTRAINT tasks_pkey PRIMARY KEY (id),
    CONSTRAINT tasks_priority_check CHECK (priority IN ('LOW', 'MEDIUM', 'HIGH')),
    CONSTRAINT tasks_status_check CHECK (status IN ('TODO', 'IN_PROGRESS', 'DONE'))
);
