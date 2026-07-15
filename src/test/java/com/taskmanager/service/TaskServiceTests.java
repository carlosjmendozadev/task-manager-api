package com.taskmanager.service;

import com.taskmanager.entity.Task;
import com.taskmanager.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import com.taskmanager.dto.TaskDto;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class TaskServiceTests {

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskRepository taskRepository;

    @Test
    void createTask_savesAndReturnsTask() {
        Task task = new Task();
        task.setTitle("Test task");
        task.setDescription("Integration test task");
        task.setCompleted(false);

        TaskDto created = taskService.createTask(task);

        assertNotNull(created.getId());
        assertTrue(taskRepository.findById(created.getId()).isPresent());
    }

    @Test
    void updateTask_modifiesExistingTask() {
        Task task = new Task();
        task.setTitle("Initial title");
        task.setDescription("Initial description");
        task.setCompleted(false);
        TaskDto created = taskService.createTask(task);

        Task update = new Task();
        update.setTitle("Updated title");
        update.setDescription("Updated description");
        update.setCompleted(true);

        TaskDto updated = taskService.updateTask(created.getId(), update);

        assertEquals("Updated title", updated.getTitle());
        assertEquals("Updated description", updated.getDescription());
    }

    @Test
    void deleteTask_removesTask() {
        Task task = new Task();
        task.setTitle("To delete");
        task.setDescription("Delete me");
        task.setCompleted(false);
        TaskDto created = taskService.createTask(task);

        taskService.deleteTask(created.getId());

        assertFalse(taskRepository.findById(created.getId()).isPresent());
    }
}
