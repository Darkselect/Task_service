package com.example.aleksey_service.mapper;

import com.example.aleksey_service.dto.TaskDto;
import com.example.aleksey_service.entity.TaskEntity;
import com.example.aleksey_service.entity.TaskStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;


public interface TaskMapper {
    @Mapping(target = "title", source = "title")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "taskStatus", source = "taskStatus", qualifiedByName = "mapStatus")
    TaskEntity taskToTaskEntity(TaskDto taskDto);

    @Mapping(target = "title", source = "title")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "taskStatus", source = "taskStatus", qualifiedByName = "mapStatusToString")
    TaskDto taskEntityToTaskDto(TaskEntity taskEntity);

    @Named("mapStatus")
    default TaskStatus mapStatus(String status) {
        return TaskStatus.valueOf(status.toUpperCase());
    }

    @Named("mapStatusToString")
    default String mapStatusToString(TaskStatus status) {
        return status.name();
    }
}
