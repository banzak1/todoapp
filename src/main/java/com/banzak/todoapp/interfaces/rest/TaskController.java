package com.banzak.todoapp.interfaces.rest;

import com.banzak.todoapp.application.TaskService;
import com.banzak.todoapp.interfaces.rest.dto.CreateTaskRequest;
import com.banzak.todoapp.interfaces.rest.dto.TaskResponse;
import com.banzak.todoapp.interfaces.rest.dto.UpdateTaskRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tasks")
@Tag(name = "Tasks", description = "Task management API")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    @Operation(summary = "Create a new task")
    @ApiResponse(responseCode = "201", description = "Task created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    public ResponseEntity<TaskResponse> create(@Valid @RequestBody CreateTaskRequest request) {
        var response = taskService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "List all tasks with optional pagination and filters")
    @ApiResponse(responseCode = "200", description = "List of tasks returned")
    public ResponseEntity<List<TaskResponse>> findAll() {
        var tasks = taskService.findAll();
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/page")
    @Operation(summary = "List tasks with pagination, sorting and optional filters")
    @ApiResponse(responseCode = "200", description = "Paginated list of tasks returned")
    public ResponseEntity<Page<TaskResponse>> findAllPaginated(
            @PageableDefault(size = 20, sort = "createdAt")
            @Parameter(description = "Pagination and sorting parameters")
            Pageable pageable,

            @RequestParam(required = false)
            @Parameter(description = "Filter by status: TODO, IN_PROGRESS, DONE")
            String status,

            @RequestParam(required = false)
            @Parameter(description = "Filter by priority: LOW, MEDIUM, HIGH")
            String priority) {
        var page = taskService.findAll(pageable, status, priority);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a task by ID")
    @ApiResponse(responseCode = "200", description = "Task found")
    @ApiResponse(responseCode = "404", description = "Task not found",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    public ResponseEntity<TaskResponse> findById(@PathVariable Long id) {
        var response = taskService.findById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing task")
    @ApiResponse(responseCode = "200", description = "Task updated successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "404", description = "Task not found",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    public ResponseEntity<TaskResponse> update(@PathVariable Long id,
                                                @Valid @RequestBody UpdateTaskRequest request) {
        var response = taskService.update(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a task")
    @ApiResponse(responseCode = "204", description = "Task deleted successfully")
    @ApiResponse(responseCode = "404", description = "Task not found",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        taskService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
