package com.taskmanager.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class TaskDto {
    @Schema(description = "Task id.", example = "1")
    private Long id;
    @Column(length = 100, nullable = false)
    @NotBlank(message = "Title is required")
    @Schema(description = "Task title.", example = "Fazer compras")
    private String title;
    @Schema(description = "Optional task description.", example = "Leite, pão, ovos")
    private String description;
    @Schema(description = "Whether the task is completed.", example = "false")
    private boolean completed;
}