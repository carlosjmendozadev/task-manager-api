package com.taskmanager.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TaskDto {
    private Long id;
    @Column(length = 100, nullable = false)
    @NotBlank(message = "Title is required")
    private String title;
    private String description;
    private boolean completed;
}