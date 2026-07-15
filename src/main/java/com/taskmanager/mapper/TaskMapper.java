package com.taskmanager.mapper;

import com.taskmanager.dto.TaskDto;
import com.taskmanager.entity.Task;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TaskMapper {

    TaskDto toTaskDto(Task task);
    
}
