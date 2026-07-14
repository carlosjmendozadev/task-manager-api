package com.taskmanager;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import com.taskmanager.repository.TaskRepository;
import com.taskmanager.controller.TaskController;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class ApiApplicationTests {

	@Autowired
	private TaskRepository taskRepository;

	@Autowired
	private TaskController taskController;

	@Test
	void contextLoads() {
	}

	@Test
	void mainStarts(){
		ApiApplication.main(new String[] {});
	}

	@Test
	void taskRepositoryIsCreated(){
		assertThat(taskRepository).isNotNull();
	}

	@Test
	void taskControllerIsCreated(){
		assertThat(taskController).isNotNull();
	}

}
