package com.taskmanager.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import com.taskmanager.service.TaskService;
import com.taskmanager.entity.Task;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest( webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = com.taskmanager.ApiApplication.class)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
public class TaskControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskService taskService;

    @Test
    public void givenTask_whenCreateTask_thenStatus201() throws Exception {
        
        Task task = new Task();
        task.setTitle("Test Task");
        task.setDescription("This is a test task.");
        task.setCompleted(false);

        mockMvc.perform(post("/api/tasks")
                .contentType("application/json")
                .content("{\"title\":\"Test Task\",\"description\":\"This is a test task.\",\"completed\":false}"))
                .andExpect(status().isCreated());

    }

    @Test
    public void givenTasks_whenListTasks_thenStatus200() throws Exception {

        Task task1 = new Task();
        task1.setTitle("Test Task 1");
        task1.setDescription("This is task #1.");
        task1.setCompleted(false);

        Task task2 = new Task();
        task2.setTitle("Test Task 2");
        task2.setDescription("This is task #2.");
        task2.setCompleted(true);

        Task task3 = new Task();
        task3.setTitle("Test Task 3");
        task3.setDescription("This is task #3.");
        task3.setCompleted(false);

        taskService.createTask(task1);
        taskService.createTask(task2);
        taskService.createTask(task3);

        var result = mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk());

        result.andExpect(mvcResult -> {
            String jsonResponse = mvcResult.getResponse().getContentAsString();
            assert jsonResponse.contains("Test Task 1");
            assert jsonResponse.contains("Test Task 2");
            assert jsonResponse.contains("Test Task 3");
        });
    }

    @Test
    public void givenTask_whenCreateTask_thenReturnTaskById() throws Exception {
        
        Task task = new Task();
        task.setTitle("Test Task");
        task.setDescription("This is a test task.");
        task.setCompleted(false);

        var taskCreated = taskService.createTask(task);

        var result = mockMvc.perform(get("/api/tasks/" + taskCreated.getId()))
                .andExpect(status().isOk());

        result.andExpect(mvcResult -> {
            String jsonResponse = mvcResult.getResponse().getContentAsString();
            assert jsonResponse.contains("Test Task");
            assert jsonResponse.contains("This is a test task.");
        });

    }

    @Test
    public void givenTask_whenCreateTask_thenUpdateTask() throws Exception {
        
        Task task = new Task();
        task.setTitle("Test Task");
        task.setDescription("This is a test task.");
        task.setCompleted(false);

        var taskCreated = taskService.createTask(task);

        var result = mockMvc.perform(put("/api/tasks/" + taskCreated.getId())
                .contentType("application/json")
                .content("{\"title\":\"Updated Test Task\",\"description\":\"This is an updated test task.\",\"completed\":true}"))
                .andExpect(status().isOk());

        result.andExpect(mvcResult -> {
            String jsonResponse = mvcResult.getResponse().getContentAsString();
            assert jsonResponse.contains("Updated Test Task");
            assert jsonResponse.contains("This is an updated test task.");
            assert jsonResponse.contains("true");
        });

    }

    @Test
    public void givenTask_whenCreateTask_thenDeleteTask() throws Exception {
        
        Task task = new Task();
        task.setTitle("Test Task");
        task.setDescription("This is a test task.");
        task.setCompleted(false);

        var taskCreated = taskService.createTask(task);

        mockMvc.perform(delete("/api/tasks/" + taskCreated.getId()))
                .andExpect(status().isNoContent());

    }
    
}
