package com.example.aleksey_service.service;

import com.example.aleksey_service.dto.TaskDto;
import com.example.aleksey_service.entity.TaskEntity;
import com.example.aleksey_service.mapper.TaskMapper;
import com.example.aleksey_service.repository.TaskRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    public TaskDto createTask(TaskDto taskDto) {
        Optional<TaskEntity> taskEntity = taskRepository.findTaskById(taskDto.getId());

        if (taskEntity.isPresent()) {
            log.error("Task with id {} already exists", taskDto.getId());
            throw new RuntimeException("Task already exists!");
        }

        TaskEntity savedTask = taskRepository.save(taskMapper.taskToTaskEntity(taskDto));

        log.info("Task created {}", taskDto.getId());
        return taskMapper.taskEntityToTaskDto(savedTask);
    }

    @Transactional
    public TaskDto getTaskById(Long id) {
        TaskEntity taskEntity = taskRepository.findTaskById(id)
                .orElseThrow(() -> {
                    log.warn("Task with id {} does not exist", id);
                    return new EntityNotFoundException("Task does not exist!");
                });

        return taskMapper.taskEntityToTaskDto(taskEntity);
    }

    @Transactional
    public TaskDto updateTask(Long id, TaskDto taskDto) {
        TaskEntity taskEntity =
                taskRepository.updateTask(id, taskDto.getTitle(), taskDto.getDescription(), taskDto.getUserId(), taskDto.getTaskStatus());

        if (taskEntity == null) {
            log.error("Task with id {}, not found", id);
            throw new EntityNotFoundException("Task not found");
        }

        return taskMapper.taskEntityToTaskDto(taskEntity);
    }

    @Transactional
    public void deleteTaskById(Long id) {
        if (!taskRepository.existsById(id)) {
            log.error("Task with id {} does not exist", id);
            throw new EntityNotFoundException(String.format("Task with id %s does not exist", id));
        }
        taskRepository.deleteTaskById(id);
    }

    @Transactional
    public List<TaskDto> getTasks(int page, int size) {
        log.info("Fetching tasks with page: {} and size: {}", page, size);

        Pageable pageable = PageRequest.of(page, size);
        List<TaskDto> tasks = taskRepository.getAllTasks(pageable)
                .stream()
                .map(taskMapper::taskEntityToTaskDto)
                .toList();

        log.info("Retrieved {} tasks", tasks.size());
        return tasks;
    }
}
