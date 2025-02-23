package com.example.aleksey_service.mapper;

import com.example.aleksey_service.dto.TaskDto;
import com.example.aleksey_service.entity.TaskEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TaskMapper {
    @Mapping(target = "title", source = "title")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "userId", source = "userId")
    TaskEntity taskToTaskEntity(TaskDto taskDto);

    @Mapping(target = "title", source = "title")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "userId", source = "userId")
    TaskDto taskEntityToTaskDto(TaskEntity taskEntity);
}
