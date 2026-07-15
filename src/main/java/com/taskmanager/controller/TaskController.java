package com.taskmanager.controller;

import com.taskmanager.entity.Task;
import com.taskmanager.service.TaskService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.data.domain.Pageable;
import com.taskmanager.helper.PaginatedResponse;
import com.taskmanager.dto.TaskDto;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<TaskDto> createTask(@Valid @RequestBody Task task) {
            TaskDto createdTask = taskService.createTask(task);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
    }

    @GetMapping
    public ResponseEntity<PaginatedResponse<TaskDto>> getAllTasks(Pageable pageable) {
        return ResponseEntity.ok(taskService.getAllTasks(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskDto> getTaskById(@PathVariable Long id) {
        TaskDto task = taskService.getTaskById(id);
        return ResponseEntity.ok(task);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskDto> updateTask(@PathVariable Long id, @RequestBody Task updatedTask) {
        TaskDto task = taskService.updateTask(id, updatedTask);
        return ResponseEntity.ok(task);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

}
