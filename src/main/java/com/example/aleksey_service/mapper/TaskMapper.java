package com.example.aleksey_service.mapper;

import com.example.aleksey_service.dto.KafkaDto;
import com.example.aleksey_service.dto.TaskDto;
import com.example.aleksey_service.dto.TaskResponseDto;
import com.example.aleksey_service.entity.TasksEntity;
import com.example.aleksey_service.entity.TaskStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;


@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TaskMapper {
    @Mapping(target = "title", source = "title")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "status", source = "status", qualifiedByName = "mapStringToStatus")
    TasksEntity taskToTaskEntity(TaskDto taskDto);

    @Mapping(target = "title", source = "title")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "status", source = "status", qualifiedByName = "mapStatusToString")
    TaskDto taskEntityToTaskDto(TasksEntity tasksEntity);

    @Named("mapStringToStatus")
    default TaskStatus mapStringToStatus(String status) {
        return (status != null) ? TaskStatus.valueOf(status.toUpperCase()) : TaskStatus.CREATED;
    }

    @Named("mapStatusToString")
    default String mapStatusToString(TaskStatus status) {
        return (status != null) ? status.name() : "CREATED";
    }

    @Mapping(target = "id", source = "id")
    @Mapping(target = "title", source = "title")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "status", source = "status")
    TaskResponseDto toTaskResponseDto(TasksEntity task);

    KafkaDto toKafkaDto(TasksEntity task);
}
