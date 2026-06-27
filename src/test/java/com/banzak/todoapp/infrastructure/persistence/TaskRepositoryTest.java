package com.banzak.todoapp.infrastructure.persistence;

import com.banzak.todoapp.domain.Task;
import com.banzak.todoapp.domain.TaskPriority;
import com.banzak.todoapp.domain.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayName("TaskRepository (PostgreSQL)")
class TaskRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private TaskRepository taskRepository;

    private Task sampleTask() {
        return Task.builder()
                .title("Integration Test Task")
                .description("Testing with real PostgreSQL")
                .priority(TaskPriority.HIGH)
                .status(TaskStatus.TODO)
                .build();
    }

    @BeforeEach
    void setUp() {
        taskRepository.deleteAll();
    }

    @Test
    @DisplayName("should save and find task by ID")
    void shouldSaveAndFindById() {
        var saved = taskRepository.save(sampleTask());

        var found = taskRepository.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getTitle()).isEqualTo("Integration Test Task");
        assertThat(found.get().getPriority()).isEqualTo(TaskPriority.HIGH);
        assertThat(found.get().getStatus()).isEqualTo(TaskStatus.TODO);
        assertThat(found.get().getCreatedAt()).isNotNull();
        assertThat(found.get().getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("should return empty when task not found")
    void shouldReturnEmpty_whenNotFound() {
        var result = taskRepository.findById(999L);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("should find all tasks")
    void shouldFindAll() {
        taskRepository.save(sampleTask());
        taskRepository.save(Task.builder()
                .title("Second Task")
                .description("Another task")
                .priority(TaskPriority.LOW)
                .status(TaskStatus.IN_PROGRESS)
                .build());

        var tasks = taskRepository.findAll();

        assertThat(tasks).hasSize(2);
    }

    @Test
    @DisplayName("should update task")
    void shouldUpdate() {
        var saved = taskRepository.save(sampleTask());
        saved.setTitle("Updated Title");
        saved.setPriority(TaskPriority.LOW);
        saved.setStatus(TaskStatus.DONE);

        var updated = taskRepository.save(saved);

        assertThat(updated.getTitle()).isEqualTo("Updated Title");
        assertThat(updated.getPriority()).isEqualTo(TaskPriority.LOW);
        assertThat(updated.getStatus()).isEqualTo(TaskStatus.DONE);
    }

    @Test
    @DisplayName("should delete task")
    void shouldDelete() {
        var saved = taskRepository.save(sampleTask());

        taskRepository.deleteById(saved.getId());

        assertThat(taskRepository.findById(saved.getId())).isEmpty();
    }

    @Test
    @DisplayName("should auto-set timestamps on create and update")
    void shouldAutoSetTimestamps() {
        var saved = taskRepository.save(sampleTask());
        var createdAt = saved.getCreatedAt();
        var updatedAt = saved.getUpdatedAt();

        assertThat(createdAt).isNotNull();
        assertThat(updatedAt).isNotNull();
        assertThat(updatedAt).isEqualTo(createdAt);

        saved.setTitle("Updated");
        var reSaved = taskRepository.save(saved);

        assertThat(reSaved.getUpdatedAt()).isNotNull();
        assertThat(reSaved.getUpdatedAt()).isAfterOrEqualTo(createdAt);
    }

    @Test
    @DisplayName("should handle empty database")
    void shouldHandleEmptyDatabase() {
        var tasks = taskRepository.findAll();

        assertThat(tasks).isEmpty();
    }
}
