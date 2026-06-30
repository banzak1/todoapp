package com.banzak.todoapp.application;

import com.banzak.todoapp.domain.Task;
import com.banzak.todoapp.domain.TaskPriority;
import com.banzak.todoapp.domain.TaskStatus;
import com.banzak.todoapp.infrastructure.persistence.TaskRepository;
import com.banzak.todoapp.interfaces.rest.dto.CreateTaskRequest;
import com.banzak.todoapp.interfaces.rest.dto.TaskResponse;
import com.banzak.todoapp.interfaces.rest.dto.UpdateTaskRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TaskService")
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    private Task createSampleTask(Long id) {
        return Task.builder()
                .id(id)
                .title("Test Task")
                .description("Test Description")
                .priority(TaskPriority.MEDIUM)
                .status(TaskStatus.TODO)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Nested
    @DisplayName("findAll")
    class FindAll {

        @Test
        @DisplayName("should return all tasks")
        void shouldReturnAllTasks() {
            var tasks = List.of(
                    createSampleTask(1L),
                    createSampleTask(2L)
            );
            when(taskRepository.findAll()).thenReturn(tasks);

            var result = taskService.findAll();

            assertThat(result).hasSize(2);
            verify(taskRepository).findAll();
        }

        @Test
        @DisplayName("should return empty list when no tasks exist")
        void shouldReturnEmptyList_whenNoTasks() {
            when(taskRepository.findAll()).thenReturn(List.of());

            var result = taskService.findAll();

            assertThat(result).isEmpty();
            verify(taskRepository).findAll();
        }
    }

    @Nested
    @DisplayName("findAll (paginated)")
    class FindAllPaginated {

        @Test
        @DisplayName("should return paginated tasks without filters")
        void shouldReturnPage_whenNoFilters() {
            var pageable = PageRequest.of(0, 10);
            var tasks = List.of(createSampleTask(1L), createSampleTask(2L));
            var page = new PageImpl<>(tasks, pageable, 2);
            when(taskRepository.findAll(pageable)).thenReturn(page);

            var result = taskService.findAll(pageable, null, null);

            assertThat(result).hasSize(2);
            assertThat(result.getTotalElements()).isEqualTo(2);
            verify(taskRepository).findAll(pageable);
        }

        @Test
        @DisplayName("should filter by status")
        void shouldFilterByStatus() {
            var pageable = PageRequest.of(0, 10);
            var tasks = List.of(createSampleTask(1L));
            var page = new PageImpl<>(tasks, pageable, 1);
            when(taskRepository.findByStatus(TaskStatus.TODO, pageable)).thenReturn(page);

            var result = taskService.findAll(pageable, "TODO", null);

            assertThat(result).hasSize(1);
            verify(taskRepository).findByStatus(TaskStatus.TODO, pageable);
        }

        @Test
        @DisplayName("should filter by priority")
        void shouldFilterByPriority() {
            var pageable = PageRequest.of(0, 10);
            var tasks = List.of(createSampleTask(1L));
            var page = new PageImpl<>(tasks, pageable, 1);
            when(taskRepository.findByPriority(TaskPriority.HIGH, pageable)).thenReturn(page);

            var result = taskService.findAll(pageable, null, "HIGH");

            assertThat(result).hasSize(1);
            verify(taskRepository).findByPriority(TaskPriority.HIGH, pageable);
        }

        @Test
        @DisplayName("should filter by status and priority")
        void shouldFilterByStatusAndPriority() {
            var pageable = PageRequest.of(0, 10);
            var tasks = List.of(createSampleTask(1L));
            var page = new PageImpl<>(tasks, pageable, 1);
            when(taskRepository.findByStatusAndPriority(TaskStatus.TODO, TaskPriority.HIGH, pageable))
                    .thenReturn(page);

            var result = taskService.findAll(pageable, "TODO", "HIGH");

            assertThat(result).hasSize(1);
            verify(taskRepository).findByStatusAndPriority(TaskStatus.TODO, TaskPriority.HIGH, pageable);
        }

        @Test
        @DisplayName("should ignore invalid filter values")
        void shouldIgnoreInvalidFilters() {
            var pageable = PageRequest.of(0, 10);
            var tasks = List.of(createSampleTask(1L));
            var page = new PageImpl<>(tasks, pageable, 1);
            when(taskRepository.findAll(pageable)).thenReturn(page);

            var result = taskService.findAll(pageable, "INVALID_STATUS", "INVALID_PRIORITY");

            assertThat(result).hasSize(1);
            verify(taskRepository).findAll(pageable);
        }
    }

    @Nested
    @DisplayName("findById")
    class FindById {

        @Test
        @DisplayName("should return task when it exists")
        void shouldReturnTask_whenExists() {
            var task = createSampleTask(1L);
            when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

            var result = taskService.findById(1L);

            assertThat(result.id()).isEqualTo(1L);
            assertThat(result.title()).isEqualTo("Test Task");
            verify(taskRepository).findById(1L);
        }

        @Test
        @DisplayName("should throw TaskNotFoundException when task does not exist")
        void shouldThrowException_whenNotFound() {
            when(taskRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> taskService.findById(99L))
                    .isInstanceOf(TaskNotFoundException.class)
                    .hasMessageContaining("99");
            verify(taskRepository).findById(99L);
        }
    }

    @Nested
    @DisplayName("create")
    class Create {

        @Test
        @DisplayName("should create task with default status TODO and provided data")
        void shouldCreateTask_whenValidRequest() {
            var request = new CreateTaskRequest("New Task", "Description", "HIGH");
            var savedTask = Task.builder()
                    .id(1L)
                    .title("New Task")
                    .description("Description")
                    .priority(TaskPriority.HIGH)
                    .status(TaskStatus.TODO)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
            when(taskRepository.save(any(Task.class))).thenReturn(savedTask);

            var result = taskService.create(request);

            assertThat(result.id()).isEqualTo(1L);
            assertThat(result.title()).isEqualTo("New Task");
            assertThat(result.status()).isEqualTo("TODO");
            assertThat(result.priority()).isEqualTo("HIGH");

            var captor = ArgumentCaptor.forClass(Task.class);
            verify(taskRepository).save(captor.capture());
            var captured = captor.getValue();
            assertThat(captured.getStatus()).isEqualTo(TaskStatus.TODO);
            assertThat(captured.getTitle()).isEqualTo("New Task");
        }

        @Test
        @DisplayName("should trim title and description")
        void shouldTrimInput() {
            var request = new CreateTaskRequest("  Task with spaces  ", "  Desc  ", null);
            var savedTask = Task.builder()
                    .id(1L)
                    .title("Task with spaces")
                    .description("Desc")
                    .priority(TaskPriority.MEDIUM)
                    .status(TaskStatus.TODO)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
            when(taskRepository.save(any(Task.class))).thenReturn(savedTask);

            var result = taskService.create(request);

            assertThat(result.title()).isEqualTo("Task with spaces");
            assertThat(result.description()).isEqualTo("Desc");
        }

        @Test
        @DisplayName("should default to MEDIUM priority when priority is null")
        void shouldDefaultPriority_whenNull() {
            var request = new CreateTaskRequest("Task", null, null);
            var savedTask = Task.builder()
                    .id(1L)
                    .title("Task")
                    .priority(TaskPriority.MEDIUM)
                    .status(TaskStatus.TODO)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
            when(taskRepository.save(any(Task.class))).thenReturn(savedTask);

            var result = taskService.create(request);

            assertThat(result.priority()).isEqualTo("MEDIUM");
        }

        @Test
        @DisplayName("should default to MEDIUM priority when priority is invalid")
        void shouldDefaultPriority_whenInvalid() {
            var request = new CreateTaskRequest("Task", null, "INVALID_PRIORITY");
            var savedTask = Task.builder()
                    .id(1L)
                    .title("Task")
                    .priority(TaskPriority.MEDIUM)
                    .status(TaskStatus.TODO)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
            when(taskRepository.save(any(Task.class))).thenReturn(savedTask);

            var result = taskService.create(request);

            assertThat(result.priority()).isEqualTo("MEDIUM");
        }
    }

    @Nested
    @DisplayName("update")
    class Update {

        @Test
        @DisplayName("should update all fields when provided")
        void shouldUpdateAllFields() {
            var existing = createSampleTask(1L);
            var request = new UpdateTaskRequest("Updated Title", "Updated Desc", "LOW", "DONE");

            when(taskRepository.findById(1L)).thenReturn(Optional.of(existing));
            when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

            var result = taskService.update(1L, request);

            assertThat(result.title()).isEqualTo("Updated Title");
            assertThat(result.description()).isEqualTo("Updated Desc");
            assertThat(result.priority()).isEqualTo("LOW");
            assertThat(result.status()).isEqualTo("DONE");
        }

        @Test
        @DisplayName("should keep existing values when fields are null")
        void shouldKeepExistingValues_whenFieldsNull() {
            var existing = createSampleTask(1L);
            var request = new UpdateTaskRequest(null, null, null, null);

            when(taskRepository.findById(1L)).thenReturn(Optional.of(existing));
            when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

            var result = taskService.update(1L, request);

            assertThat(result.title()).isEqualTo("Test Task");
            assertThat(result.priority()).isEqualTo("MEDIUM");
            assertThat(result.status()).isEqualTo("TODO");
        }

        @Test
        @DisplayName("should throw TaskNotFoundException when task does not exist")
        void shouldThrowException_whenNotFound() {
            var request = new UpdateTaskRequest("Title", null, null, null);

            when(taskRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> taskService.update(99L, request))
                    .isInstanceOf(TaskNotFoundException.class)
                    .hasMessageContaining("99");
            verify(taskRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("delete")
    class Delete {

        @Test
        @DisplayName("should delete task when it exists")
        void shouldDeleteTask_whenExists() {
            when(taskRepository.existsById(1L)).thenReturn(true);

            taskService.delete(1L);

            verify(taskRepository).deleteById(1L);
        }

        @Test
        @DisplayName("should throw TaskNotFoundException when task does not exist")
        void shouldThrowException_whenNotFound() {
            when(taskRepository.existsById(99L)).thenReturn(false);

            assertThatThrownBy(() -> taskService.delete(99L))
                    .isInstanceOf(TaskNotFoundException.class)
                    .hasMessageContaining("99");
            verify(taskRepository, never()).deleteById(any());
        }
    }
}
