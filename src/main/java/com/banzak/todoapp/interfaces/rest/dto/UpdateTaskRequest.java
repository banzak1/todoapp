package com.banzak.todoapp.interfaces.rest.dto;

import jakarta.validation.constraints.Size;

public record UpdateTaskRequest(
        @Size(min = 1, max = 255, message = "Title must be between 1 and 255 characters")
        String title,

        String description,

        String priority,

        String status
) {
}
