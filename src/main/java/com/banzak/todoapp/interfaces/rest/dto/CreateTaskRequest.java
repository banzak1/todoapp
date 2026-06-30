package com.banzak.todoapp.interfaces.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Request body for creating a new task")
public record CreateTaskRequest(
        @NotBlank(message = "Title is required")
        @Size(min = 1, max = 255, message = "Title must be between 1 and 255 characters")
        @Schema(description = "Task title", example = "Implement login page", minLength = 1, maxLength = 255)
        String title,

        @Schema(description = "Task description", example = "Create the login page with email and password fields")
        String description,

        @Schema(description = "Task priority", example = "HIGH", allowableValues = {"LOW", "MEDIUM", "HIGH"})
        String priority
) {
}
