package com.taskmanager.service;

import com.taskmanager.entity.Task;

import com.taskmanager.repository.TaskRepository;
import com.taskmanager.mapper.TaskMapper;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.taskmanager.helper.PaginatedResponse;
import com.taskmanager.dto.TaskDto;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;

    public TaskDto createTask(Task task) {
        var taskSaved = taskRepository.save(task);

        TaskDto taskDto = taskMapper.toTaskDto(taskSaved);

        return taskDto;
    }

    public PaginatedResponse<TaskDto> getAllTasks(Pageable pageable) {
        Page<Task> page = taskRepository.findAll(pageable);

        Page<TaskDto> taskDtoPage = page.map(task -> {

            TaskDto taskDto = taskMapper.toTaskDto(task);

            return taskDto;
        });

        return new PaginatedResponse<>(taskDtoPage);
    }

    public TaskDto getTaskById(Long id) {
        var taskFound = taskRepository.findById(id).orElseThrow(() -> new RuntimeException("Task not found with id: " + id));

        TaskDto taskDto = taskMapper.toTaskDto(taskFound);

        return taskDto;
    }

    public TaskDto updateTask(Long id, Task updatedTask) {

        var existingTask = taskRepository.findById(id).orElseThrow(() -> new RuntimeException("Task not found with id: " + id));

        existingTask.setTitle(updatedTask.getTitle());
        existingTask.setDescription(updatedTask.getDescription());
        existingTask.setCompleted(updatedTask.getCompleted());

        var newTask = taskRepository.save(existingTask);
        TaskDto taskDto = taskMapper.toTaskDto(newTask);

        return taskDto;
    }

    public void deleteTask(Long id) {
         var existingTask = taskRepository.findById(id).orElseThrow(() -> new RuntimeException("Task not found with id: " + id));
        taskRepository.delete(existingTask);
    }
    
}
