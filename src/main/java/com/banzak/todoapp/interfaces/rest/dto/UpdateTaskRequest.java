package com.banzak.todoapp.interfaces.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

@Schema(description = "Request body for updating an existing task (all fields optional)")
public record UpdateTaskRequest(
        @Size(min = 1, max = 255, message = "Title must be between 1 and 255 characters")
        @Schema(description = "Task title", example = "Implement login page", maxLength = 255)
        String title,

        @Schema(description = "Task description", example = "Create the login page with email and password fields")
        String description,

        @Schema(description = "Task priority", example = "HIGH", allowableValues = {"LOW", "MEDIUM", "HIGH"})
        String priority,

        @Schema(description = "Task status", example = "IN_PROGRESS", allowableValues = {"TODO", "IN_PROGRESS", "DONE"})
        String status
) {
}
