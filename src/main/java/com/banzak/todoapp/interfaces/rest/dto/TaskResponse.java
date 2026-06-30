package com.banzak.todoapp.interfaces.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Task response body")
public record TaskResponse(
        @Schema(description = "Task unique identifier", example = "1")
        Long id,

        @Schema(description = "Task title", example = "Implement login page")
        String title,

        @Schema(description = "Task description", example = "Create the login page with email and password fields")
        String description,

        @Schema(description = "Task priority", example = "HIGH", allowableValues = {"LOW", "MEDIUM", "HIGH"})
        String priority,

        @Schema(description = "Task status", example = "TODO", allowableValues = {"TODO", "IN_PROGRESS", "DONE"})
        String status,

        @Schema(description = "Task creation timestamp")
        LocalDateTime createdAt,

        @Schema(description = "Task last update timestamp")
        LocalDateTime updatedAt
) {
}
