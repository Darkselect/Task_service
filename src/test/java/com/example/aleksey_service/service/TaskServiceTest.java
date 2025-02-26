package com.example.aleksey_service.service;

import com.example.aleksey_service.dto.TaskDto;
import com.example.aleksey_service.entity.TaskEntity;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {
    @InjectMocks
    private TaskService taskService;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskMapper taskMapper;

    private TaskDto createTaskDto(Long id, String title, String description, Long userId) {
        return TaskDto.builder()
                .id(id)
                .title(title)
                .description(description)
                .userId(userId)
                .build();
    }

    private TaskEntity createTaskEntity(String title, String description, Long userId) {
        return TaskEntity.builder()
                .id(1L)
                .title(title)
                .description(description)
                .userId(userId)
                .build();
    }

    @Test
    void testCreateTaskSuccess() {
        TaskDto taskDto = createTaskDto(1L, "Test title", "test description", 1L);
        TaskEntity taskEntity = createTaskEntity("Test title", "test description", 1L);

        when(taskRepository.findTaskById(taskDto.getId())).thenReturn(Optional.empty());
        when(taskRepository.save(any(TaskEntity.class))).thenReturn(taskEntity);
        when(taskMapper.taskToTaskEntity(any(TaskDto.class))).thenReturn(taskEntity);
        when(taskMapper.taskEntityToTaskDto(any(TaskEntity.class))).thenReturn(taskDto);

        TaskDto createdTask = taskService.createTask(taskDto);

        assertNotNull(createdTask);
        assertEquals(taskDto.getId(), createdTask.getId());
        assertEquals(taskDto.getTitle(), createdTask.getTitle());
        assertEquals(taskDto.getDescription(), createdTask.getDescription());

        verify(taskRepository).save(any(TaskEntity.class));
    }

    @Test
    void testCreateTaskAlreadyExists() {
        TaskDto taskDto = createTaskDto(1L, "Test title", "Test description", 1L);
        TaskEntity taskEntity = createTaskEntity("Test title", "Test description", 1L);

        when(taskRepository.findTaskById(taskDto.getId())).thenReturn(Optional.of(taskEntity));

        Exception exception = assertThrows(RuntimeException.class, () -> taskService.createTask(taskDto));

        assertEquals("Task already exists!", exception.getMessage());
        verify(taskRepository, never()).save(any(TaskEntity.class));
    }

    @Test
    void testGetTaskByIdSuccess() {
        TaskDto taskDto = createTaskDto(1L, "Test title", "Test description", 1L);
        TaskEntity taskEntity = createTaskEntity("Test title", "Test description", 1L);

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
        when(taskRepository.findTaskById(999L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> taskService.getTaskById(999L));
    }

    @Test
    void testUpdateTaskById() {
        TaskDto taskDto = createTaskDto(1L, "Updated title", "Updated description", 2L);
        TaskEntity taskEntity = createTaskEntity("Updated title", "Updated description", 2L);

        when(taskRepository.updateTask(taskDto.getId(), taskDto.getTitle(), taskDto.getDescription(), taskDto.getUserId(), taskDto.getTaskStatus()))
                .thenReturn(taskEntity);
        when(taskMapper.taskEntityToTaskDto(any(TaskEntity.class))).thenReturn(taskDto);

        TaskDto responseDto = taskService.updateTask(taskDto.getId(), taskDto);

        assertNotNull(responseDto);
        assertEquals(taskDto.getId(), responseDto.getId());
        assertEquals(taskDto.getTitle(), responseDto.getTitle());
        assertEquals(taskDto.getDescription(), responseDto.getDescription());
        assertEquals(taskDto.getUserId(), responseDto.getUserId());

        verify(taskRepository).updateTask(taskDto.getId(), taskDto.getTitle(), taskDto.getDescription(), taskDto.getUserId(), taskDto.getTaskStatus());
        verify(taskMapper).taskEntityToTaskDto(taskEntity);
    }

    @Test
    void testUpdateTaskByIdNotFound() {
        TaskDto taskDto = createTaskDto(99L, "Updated title", "Updated description", 2L);

        when(taskRepository.updateTask(taskDto.getId(), taskDto.getTitle(), taskDto.getDescription(), taskDto.getUserId(), taskDto.getTaskStatus()))
                .thenReturn(null);

        assertThrows(EntityNotFoundException.class, () -> taskService.updateTask(taskDto.getId(), taskDto));

        verify(taskRepository).updateTask(taskDto.getId(), taskDto.getTitle(), taskDto.getDescription(), taskDto.getUserId(), taskDto.getTaskStatus());
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

        TaskEntity taskEntity = createTaskEntity("test title", "test description", 1L);

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
