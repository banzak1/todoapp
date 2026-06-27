package com.banzak.todoapp.application;

import com.banzak.todoapp.domain.Task;
import com.banzak.todoapp.domain.TaskPriority;
import com.banzak.todoapp.domain.TaskStatus;
import com.banzak.todoapp.infrastructure.persistence.TaskRepository;
import com.banzak.todoapp.interfaces.rest.dto.CreateTaskRequest;
import com.banzak.todoapp.interfaces.rest.dto.TaskResponse;
import com.banzak.todoapp.interfaces.rest.dto.UpdateTaskRequest;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public List<TaskResponse> findAll() {
        return taskRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public TaskResponse findById(Long id) {
        return taskRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new TaskNotFoundException(id));
    }

    public TaskResponse create(CreateTaskRequest request) {
        var task = Task.builder()
                .title(request.title().trim())
                .description(Optional.ofNullable(request.description()).map(String::trim).orElse(null))
                .priority(parsePriority(request.priority()))
                .status(TaskStatus.TODO)
                .build();

        var saved = taskRepository.save(task);
        return toResponse(saved);
    }

    public TaskResponse update(Long id, UpdateTaskRequest request) {
        var task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));

        Optional.ofNullable(request.title())
                .map(String::trim)
                .ifPresent(task::setTitle);

        Optional.ofNullable(request.description())
                .map(String::trim)
                .ifPresent(task::setDescription);

        Optional.ofNullable(request.priority())
                .map(this::parsePriority)
                .ifPresent(task::setPriority);

        Optional.ofNullable(request.status())
                .map(this::parseStatus)
                .ifPresent(task::setStatus);

        var saved = taskRepository.save(task);
        return toResponse(saved);
    }

    public void delete(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new TaskNotFoundException(id);
        }
        taskRepository.deleteById(id);
    }

    private TaskPriority parsePriority(String value) {
        if (value == null) return TaskPriority.MEDIUM;
        try {
            return TaskPriority.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return TaskPriority.MEDIUM;
        }
    }

    private TaskStatus parseStatus(String value) {
        if (value == null) return TaskStatus.TODO;
        try {
            return TaskStatus.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return TaskStatus.TODO;
        }
    }

    private TaskResponse toResponse(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getPriority() != null ? task.getPriority().name() : null,
                task.getStatus() != null ? task.getStatus().name() : null,
                task.getCreatedAt(),
                task.getUpdatedAt()
        );
    }
}
