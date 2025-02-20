package com.example.aleksey_service.controller;

import com.example.aleksey_service.dto.TaskDto;
import com.example.aleksey_service.service.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
public class TaskControllerTest {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 10;

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private TaskService taskService;

    private TaskDto taskDto;
    private TaskDto expectedResponseDto;
    private List<TaskDto> taskList;

    @BeforeEach
    void setUp() {
        taskDto = TaskDto.builder()
                .id(1L)
                .title("Test title")
                .description("Test description")
                .userId(1L)
                .build();

        expectedResponseDto = TaskDto.builder()
                .id(1L)
                .title("Test title")
                .description("Test description")
                .userId(1L)
                .build();

        taskList = List.of(
                new TaskDto(1L, "Task 1", "Task description 1", 1L),
                new TaskDto(2L, "Task 2", "Task description 2", 2L)
        );
    }

    @Test
    void testCreateTaskSuccess() throws Exception {
        when(taskService.createTask(any(TaskDto.class))).thenReturn(taskDto);
        performPost("/tasks", taskDto, status().isCreated(), expectedResponseDto);
    }

    @Test
    void testCreateTaskFail() throws Exception {
        TaskDto invalidTask = new TaskDto(1L, " ", " ", 1L);
        performPost("/tasks", invalidTask, status().isBadRequest(), null);
    }

    @Test
    void testGetTaskByIdSuccess() throws Exception {
        when(taskService.getTaskById(taskDto.getId())).thenReturn(expectedResponseDto);
        mvc.perform(get("/tasks/{id}", taskDto.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(OBJECT_MAPPER.writeValueAsString(expectedResponseDto)));
    }

    @Test
    void testGetTaskByIdFail() throws Exception {
        when(taskService.getTaskById(999L)).thenThrow(new EntityNotFoundException("Task not found"));
        mvc.perform(get("/tasks/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Task not found")));
    }

    @Test
    void testUpdateTaskSuccess() throws Exception {
        when(taskService.updateTask(eq(taskDto.getId()), any(TaskDto.class))).thenReturn(expectedResponseDto);
        performPut(taskDto.getId(), taskDto, status().isOk(), expectedResponseDto);
    }

    @Test
    void testUpdateTaskNotFound() throws Exception {
        when(taskService.updateTask(eq(99L), any(TaskDto.class)))
                .thenThrow(new EntityNotFoundException("Task not found"));

        performPut(99L, taskDto, status().isNotFound(), null);
    }

    @Test
    void testUpdateTaskInvalidRequest() throws Exception {
        performPut(1L, new TaskDto(), status().isBadRequest(), null);
    }

    @Test
    void testDeleteTaskByIdSuccess() throws Exception {
        doNothing().when(taskService).deleteTaskById(anyLong());
        mvc.perform(delete("/tasks/{id}", 1L))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteTaskByIdNotFound() throws Exception {
        doThrow(new EntityNotFoundException("Task not found")).when(taskService).deleteTaskById(999L);
        mvc.perform(delete("/tasks/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Task not found")));
    }

    @Test
    void testGetTasksSuccess() throws Exception {
        when(taskService.getTasks(DEFAULT_PAGE, DEFAULT_SIZE)).thenReturn(taskList);
        mvc.perform(get("/tasks")
                        .param("page", String.valueOf(DEFAULT_PAGE))
                        .param("size", String.valueOf(DEFAULT_SIZE))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(OBJECT_MAPPER.writeValueAsString(taskList)));
    }

    @Test
    void testGetTasksInvalidParams() throws Exception {
        mvc.perform(get("/tasks")
                        .param("page", "-1")
                        .param("size", "10"))
                .andExpect(status().isBadRequest());
    }

    private void performPost(String url, TaskDto dto, ResultMatcher expectedStatus, TaskDto expectedResponse) throws Exception {
        var request = mvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(dto)))
                .andExpect(expectedStatus);

        if (expectedResponse != null) {
            request.andExpect(content().json(OBJECT_MAPPER.writeValueAsString(expectedResponse)));
        }
    }

    private void performPut(Long id, TaskDto dto, ResultMatcher expectedStatus, TaskDto expectedResponse) throws Exception {
        var request = mvc.perform(put("/tasks/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(dto)))
                .andExpect(expectedStatus);

        if (expectedResponse != null) {
            request.andExpect(content().json(OBJECT_MAPPER.writeValueAsString(expectedResponse)));
        }
    }
}
