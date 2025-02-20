package com.example.aleksey_service.service;

import com.example.aleksey_service.dto.TaskDto;
import com.example.aleksey_service.entity.TaskEntity;
import com.example.aleksey_service.mapper.TaskMapper;
import com.example.aleksey_service.repository.TaskRepository;
import jakarta.persistence.EntityNotFoundException;
import org.hibernate.action.internal.EntityActionVetoException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {
    @InjectMocks
    private TaskService taskService;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskMapper taskMapper;

    @Test
    void testCreateTaskSuccess() {
        TaskDto taskDto = TaskDto.builder()
                .id(1L)
                .title("Test title")
                .description("test description")
                .userId(1L)
                .build();


        TaskEntity taskEntity = TaskEntity.builder()
                .id(1L)
                .title("Test title")
                .description("test description")
                .userId(1L)
                .build();
        Result result = new Result(taskDto, taskEntity);


        when(taskRepository.findTaskById(result.taskDto().getId())).thenReturn(Optional.empty());
        when(taskRepository.save(any(TaskEntity.class))).thenReturn(result.taskEntity());
        when(taskMapper.taskToTaskEntity(any(TaskDto.class))).thenReturn(result.taskEntity());
        when(taskMapper.taskEntityToTaskDto(any(TaskEntity.class))).thenReturn(result.taskDto());

        TaskDto createdTask = taskService.createTask(result.taskDto());

        assertNotNull(createdTask);
        assertEquals(result.taskDto().getId(), createdTask.getId());
        assertEquals(result.taskDto().getTitle(), createdTask.getTitle());
        assertEquals(result.taskDto().getDescription(), createdTask.getDescription());

        verify(taskRepository).save(any(TaskEntity.class));
    }

    private record Result(TaskDto taskDto, TaskEntity taskEntity) {
    }

    @Test
    void testCreateTaskAlreadyExists() {
        TaskDto taskDto = TaskDto.builder()
                .id(1L)
                .title("Test title")
                .description("Test description")
                .userId(1L)
                .build();

        TaskEntity taskEntity = TaskEntity.builder()
                .id(1L)
                .title("Test title")
                .description("Test description")
                .userId(1L)
                .build();

        when(taskRepository.findTaskById(taskDto.getId())).thenReturn(Optional.of(taskEntity));

        Exception exception = assertThrows(RuntimeException.class, () -> taskService.createTask(taskDto));

        assertEquals("Task already exists!", exception.getMessage());
        verify(taskRepository, never()).save(any(TaskEntity.class));
    }

    @Test
    void testGetTaskByIdSuccess() {
        TaskDto taskDto = TaskDto.builder()
                .id(1L)
                .build();

        TaskEntity taskEntity = TaskEntity.builder()
                .id(1L)
                .title("Test title")
                .description("Test description")
                .userId(1L)
                .build();



        when(taskRepository.findTaskById(anyLong())).thenReturn(Optional.of(taskEntity));
        when(taskMapper.taskEntityToTaskDto(any(TaskEntity.class))).thenReturn(taskDto);

        TaskDto foundTask = taskService.getTaskById(1L);

        assertNotNull(foundTask);
        assertEquals(taskDto.getId(), foundTask.getId());
        assertEquals(taskDto.getTitle(), foundTask.getTitle());
        assertEquals(taskDto.getDescription(), foundTask.getDescription());
        assertEquals(taskDto.getUserId(), foundTask.getUserId());

        verify(taskRepository).findTaskById(eq(1L));
    }

    @Test
    void testGetTaskByIdNotFound() {
         TaskDto taskDto = TaskDto.builder()
                 .id(999L)
                 .build();

         when(taskRepository.findTaskById(taskDto.getId())).thenReturn(Optional.empty());

         assertThrows(EntityNotFoundException.class, () -> taskService.getTaskById(taskDto.getId()));
    }

    @Test
    void testUpdateTaskById() {
        TaskDto taskDto = TaskDto.builder()
                .id(1L)
                .title("Updated title")
                .description("Updated description")
                .userId(2L)
                .build();


        TaskEntity taskEntity = TaskEntity.builder()
                .id(1L)
                .title("Updated title")
                .description("Updated description")
                .userId(2L)
                .build();

        when(taskRepository.updateTask(taskDto.getId(), taskDto.getTitle(), taskDto.getDescription(), taskDto.getUserId()))
                .thenReturn(taskEntity);

        when(taskMapper.taskEntityToTaskDto(any(TaskEntity.class))).thenReturn(taskDto);

        TaskDto responseDto = taskService.updateTask(taskDto.getId(), taskDto);

        assertNotNull(responseDto);
        assertEquals(taskDto.getId(), responseDto.getId());
        assertEquals(taskDto.getTitle(), responseDto.getTitle());
        assertEquals(taskDto.getDescription(), responseDto.getDescription());
        assertEquals(taskDto.getUserId(), responseDto.getUserId());

        verify(taskRepository).updateTask(taskDto.getId(), taskDto.getTitle(), taskDto.getDescription(), taskDto.getUserId());
        verify(taskMapper).taskEntityToTaskDto(taskEntity);
    }

    @Test
    void testUpdateTaskByIdNotFound() {
        TaskDto taskDto = TaskDto.builder()
                .id(99L)
                .title("Updated title")
                .description("Updated description")
                .userId(2L)
                .build();

        when(taskRepository.updateTask(taskDto.getId(), taskDto.getTitle(), taskDto.getDescription(), taskDto.getUserId()))
                .thenReturn(null);

        assertThrows(EntityNotFoundException.class, () -> taskService.updateTask(taskDto.getId(), taskDto));

        verify(taskRepository).updateTask(taskDto.getId(), taskDto.getTitle(), taskDto.getDescription(), taskDto.getUserId());
        verify(taskMapper, never()).taskEntityToTaskDto(any());
    }

    @Test
    void testDeleteTaskByIdSuccess() {
        long taskId = 1L;

        when(taskRepository.existsById(taskId)).thenReturn(true);
        doNothing().when(taskRepository).deleteTaskById(taskId);

        taskService.deleteTaskById(taskId);

        verify(taskRepository).existsById(taskId);
        verify(taskRepository).deleteTaskById(taskId);
    }



}
