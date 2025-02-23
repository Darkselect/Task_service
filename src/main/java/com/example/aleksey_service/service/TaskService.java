package com.example.aleksey_service.service;

import com.example.aleksey_service.asspect.annotation.AfterReturningLogging;
import com.example.aleksey_service.asspect.annotation.BeforeLoggingAspect;
import com.example.aleksey_service.asspect.annotation.ExceptionLogging;
import com.example.aleksey_service.dto.TaskDto;
import com.example.aleksey_service.entity.TaskEntity;
import com.example.aleksey_service.mapper.TaskMapper;
import com.example.aleksey_service.repository.TaskRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterThrowing;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskService {
    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;

    @Transactional
    @ExceptionLogging
    @AfterReturningLogging
    public TaskDto createTask(TaskDto taskDto) {
        Optional<TaskEntity> taskEntity = taskRepository.findTaskById(taskDto.getId());

        if (taskEntity.isPresent()) {
            throw new IllegalStateException();
        }

        TaskEntity savedTask = taskRepository.save(taskMapper.taskToTaskEntity(taskDto));

        return taskMapper.taskEntityToTaskDto(savedTask);
    }

    @Transactional
    @AfterThrowing
    @AfterReturningLogging
    public TaskDto getTaskById(Long id) {
        TaskEntity taskEntity = taskRepository.findTaskById(id)
                .orElseThrow(() -> new EntityNotFoundException("Task does not exist!"));

        return taskMapper.taskEntityToTaskDto(taskEntity);
    }

    @ExceptionLogging
    @Transactional
    public TaskDto updateTask(Long id, TaskDto taskDto) {
        TaskEntity taskEntity = taskRepository.updateTask(id, taskDto.getTitle(), taskDto.getDescription(), taskDto.getUserId());

        if (taskEntity == null) {
            throw new EntityNotFoundException("Task not found");
        }

        return taskMapper.taskEntityToTaskDto(taskEntity);
    }

    @Transactional
    @AfterThrowing
    @AfterReturningLogging
    public void deleteTaskById(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new EntityNotFoundException(String.format("Task with id %s does not exist", id));
        }
        taskRepository.deleteTaskById(id);
    }

    @Transactional
    @BeforeLoggingAspect
    @AfterReturningLogging
    public List<TaskDto> getTasks(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return taskRepository.getAllTasks(pageable)
                .stream()
                .map(taskMapper::taskEntityToTaskDto)
                .toList();
    }
}
