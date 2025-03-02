package com.example.aleksey_service.service;

import com.example.aleksey_service.dto.KafkaDto;
import com.example.aleksey_service.dto.TaskDto;
import com.example.aleksey_service.dto.TaskResponseDto;
import com.example.aleksey_service.dto.UpdatedDto;
import com.example.aleksey_service.entity.TaskStatus;
import com.example.aleksey_service.entity.TasksEntity;
import com.example.aleksey_service.kafka.KafkaTaskProducer;
import com.example.aleksey_service.mapper.TaskMapper;
import com.example.aleksey_service.repository.TaskRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
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

    @Mock
    private KafkaTaskProducer kafkaTaskProducer;

    private TaskDto createTaskDto(Long id, String title, String description, Long userId) {
        return TaskDto.builder()
                .id(id)
                .title(title)
                .description(description)
                .userId(userId)
                .build();
    }

    private TasksEntity createTaskEntity(String title, String description, Long userId) {
        return TasksEntity.builder()
                .id(1L)
                .title(title)
                .description(description)
                .userId(userId)
                .build();
    }

    @Test
    void testCreateTaskSuccess() {
        TaskDto taskDto = createTaskDto(1L, "Test title", "test description", 1L);
        TasksEntity taskEntity = createTaskEntity("Test title", "test description", 1L);

        when(taskRepository.findTaskById(taskDto.getId())).thenReturn(Optional.empty());
        when(taskRepository.save(any(TasksEntity.class))).thenReturn(taskEntity);
        when(taskMapper.taskToTaskEntity(any(TaskDto.class))).thenReturn(taskEntity);
        when(taskMapper.taskEntityToTaskDto(any(TasksEntity.class))).thenReturn(taskDto);

        TaskDto createdTask = taskService.createTask(taskDto);

        assertNotNull(createdTask);
        assertEquals(taskDto.getId(), createdTask.getId());
        assertEquals(taskDto.getTitle(), createdTask.getTitle());
        assertEquals(taskDto.getDescription(), createdTask.getDescription());

        verify(taskRepository).save(any(TasksEntity.class));
    }

    @Test
    void testCreateTaskAlreadyExists() {
        TaskDto taskDto = createTaskDto(1L, "Test title", "Test description", 1L);
        TasksEntity taskEntity = createTaskEntity("Test title", "Test description", 1L);

        when(taskRepository.findTaskById(taskDto.getId())).thenReturn(Optional.of(taskEntity));

        Exception exception = assertThrows(RuntimeException.class, () -> taskService.createTask(taskDto));

        assertEquals(null, exception.getMessage());
        verify(taskRepository, never()).save(any(TasksEntity.class));
    }

    @Test
    void testGetTaskByIdSuccess() {
        TaskDto taskDto = createTaskDto(1L, "Test title", "Test description", 1L);
        TasksEntity taskEntity = createTaskEntity("Test title", "Test description", 1L);

        when(taskRepository.findTaskById(anyLong())).thenReturn(Optional.of(taskEntity));
        when(taskMapper.taskEntityToTaskDto(any(TasksEntity.class))).thenReturn(taskDto);

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
        when(taskRepository.findTaskById(999L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> taskService.getTaskById(999L));
    }

    @Test
    void testUpdateSuccess() {
        long taskId = 1L;

        UpdatedDto updatedDto = UpdatedDto.builder()
                .title("Updated title")
                .description("Updated description")
                .userId(1L)
                .taskStatus(TaskStatus.UPDATED)
                .build();

        TasksEntity mockTask = TasksEntity.builder()
                .id(taskId)
                .title("Updated title")
                .description("Updated description")
                .status(TaskStatus.UPDATED)
                .build();

        KafkaDto kafkaDto = KafkaDto.builder()
                .id(taskId)
                .status(TaskStatus.UPDATED)
                .build();

        when(taskRepository.updateTaskById(eq(taskId), anyString(), anyString(), anyLong(), anyString()))
                .thenReturn(Optional.of(mockTask));
        when(taskMapper.toKafkaDto(mockTask)).thenReturn(kafkaDto);
        when(taskMapper.toTaskResponseDto(mockTask)).thenReturn(new TaskResponseDto(taskId, "Updated title", "Updated description", 2L, TaskStatus.UPDATED));

        TaskResponseDto responseDto = taskService.updateTask(taskId, updatedDto);

        assertNotNull(responseDto);
        assertEquals("Updated title", responseDto.getTitle());
        assertEquals("Updated description", responseDto.getDescription());
        assertEquals(1L, responseDto.getId());
        assertEquals(TaskStatus.UPDATED, responseDto.getStatus());

        verify(kafkaTaskProducer, times(1)).send(List.of(kafkaDto));
    }

    @Test
    void testUpdateFailure_TaskNotFound() {
        long taskId = 1L;

        UpdatedDto updatedDto = UpdatedDto.builder()
                .title("Updated title")
                .description("Updated description")
                .userId(1L)
                .taskStatus(TaskStatus.UPDATED)
                .build();

        when(taskRepository.updateTaskById(eq(taskId), anyString(), anyString(), anyLong(), anyString()))
                .thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> taskService.updateTask(taskId, updatedDto));

        assertEquals("Task with " + taskId + " not found", exception.getMessage());

        verify(taskRepository, times(1)).updateTaskById(eq(taskId), anyString(), anyString(), anyLong(), anyString());
        verify(taskMapper, never()).toKafkaDto(any());
        verify(taskMapper, never()).toTaskResponseDto(any());
        verify(kafkaTaskProducer, never()).send(any());
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

    @Test
    void testDeleteTaskByIdNotFound() {
        long taskId = 99L;
        assertThrows(EntityNotFoundException.class, () -> taskService.deleteTaskById(taskId));
    }

    @Test
    void testGetTasksSuccess() {
        int page = 0;
        int size = 5;

        Pageable pageable = PageRequest.of(page, size);

        TasksEntity taskEntity = createTaskEntity("test title", "test description", 1L);

        TaskDto taskDto = createTaskDto(1L, "test title", "test description", 1L);

        when(taskRepository.getAllTasks(pageable)).thenReturn(List.of(taskEntity));
        when(taskMapper.taskEntityToTaskDto(taskEntity)).thenReturn(taskDto);

        List<TaskDto> tasks = taskService.getTasks(page, size);

        assertEquals(1, tasks.size());
        assertEquals(taskDto, tasks.get(0));
    }

    @Test
    void testGetTasksEmptyList() {
        int page = 0;
        int size = 5;
        Pageable pageable = PageRequest.of(page, size);

        when(taskRepository.getAllTasks(pageable)).thenReturn(Collections.emptyList());

        List<TaskDto> tasks = taskService.getTasks(page, size);

        assertTrue(tasks.isEmpty());
    }

}
