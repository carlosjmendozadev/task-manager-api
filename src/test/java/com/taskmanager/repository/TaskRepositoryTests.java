package com.taskmanager.repository;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import com.taskmanager.entity.Task;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import java.util.Optional;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(org.springframework.test.context.junit.jupiter.SpringExtension.class)
@DataJpaTest
public class TaskRepositoryTests {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TaskRepository taskRepository;

    @Test
    public void givenTask_whenSave_thenReturnSavedTask() {
        Task task = new Task();
        task.setTitle("Test Task");
        task.setDescription("This is a test task.");
        task.setCompleted(false);

        var taskCreated = taskRepository.save(task);

        Task savedTask = entityManager.find(Task.class, taskCreated.getId());

        assertNotNull(savedTask.getId());
        assertEquals(task.getTitle(), savedTask.getTitle());
        assertEquals(task.getDescription(), savedTask.getDescription());
        assertEquals(task.getCompleted(), savedTask.getCompleted());
    }

    @Test
    public void givenTasks_whenFindAll_thenReturnAllTasks() {
        Task task1 = new Task();
        task1.setTitle("Test Task 1");
        task1.setDescription("This is task #1.");
        task1.setCompleted(false);

        Task task2 = new Task();
        task2.setTitle("Test Task 2");
        task2.setDescription("This is task #2.");
        task2.setCompleted(true);

        entityManager.persist(task1);
        entityManager.persist(task2);
        entityManager.flush();

        List<Task> tasks = taskRepository.findAll();
        assertNotNull(tasks);
        assertEquals(2, tasks.size());
    
    }

    @Test
    public void givenTask_whenFindById_thenReturnTask() {
        Task task = new Task();
        task.setTitle("Test Task");
        task.setDescription("This is a test task.");
        task.setCompleted(false);

        var taskCreated = entityManager.persist(task);
        entityManager.flush();

        Optional<Task> foundTask = taskRepository.findById(taskCreated.getId());
        assertNotNull(foundTask);
        assertEquals(task.getTitle(), foundTask.get().getTitle());
        assertEquals(task.getDescription(), foundTask.get().getDescription());
        assertEquals(task.getCompleted(), foundTask.get().getCompleted());
    }

    @Test
    public void givenTask_whenUpdate_thenTaskShouldBeUpdated() {
        Task task = new Task();
        task.setTitle("Test Task");
        task.setDescription("This is a test task.");
        task.setCompleted(false);

        var taskCreated = entityManager.persist(task);
        entityManager.flush();

        taskCreated.setTitle("Updated Test Task");
        taskCreated.setDescription("This is an updated test task.");
        taskCreated.setCompleted(true);

        var updatedTask = taskRepository.save(taskCreated);

        assertEquals("Updated Test Task", updatedTask.getTitle());
        assertEquals("This is an updated test task.", updatedTask.getDescription());
        assertEquals(true, updatedTask.getCompleted());
    }

    @Test
    public void givenTask_whenDelete_thenTaskShouldBeDeleted() {
        Task task = new Task();
        task.setTitle("Test Task");
        task.setDescription("This is a test task.");
        task.setCompleted(false);

        var taskCreated = entityManager.persist(task);
        entityManager.flush();

        taskRepository.deleteById(taskCreated.getId());

        Optional<Task> deletedTask = taskRepository.findById(taskCreated.getId());
        assertEquals(Optional.empty(), deletedTask);
    }

}
