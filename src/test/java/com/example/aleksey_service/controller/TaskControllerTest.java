package com.example.aleksey_service.controller;

import com.example.aleksey_service.dto.TaskDto;
import com.example.aleksey_service.service.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(TaskController.class)
public class TaskControllerTest {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private TaskService taskService;

    @Test
    void testCreateTaskSuccess() throws Exception {
        TaskDto taskDto = TaskDto.builder()
                .id(1L)
                .title("test title")
                .description("test description")
                .userId(1L)
                .build();

        TaskDto expectedResponseDto = TaskDto.builder()
                .id(1L)
                .title("test title")
                .description("test description")
                .userId(1L)
                .build();

        when(taskService.createTask(any(TaskDto.class))).thenReturn(taskDto);

        mvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(OBJECT_MAPPER.writeValueAsString(taskDto)))
                .andExpect(status().isCreated())
                .andExpect(content().json(OBJECT_MAPPER.writeValueAsString(expectedResponseDto)));
    }

    @Test
    void testCreateTaskFail() throws Exception {
        TaskDto taskDto = TaskDto.builder()
                .id(1L)
                .userId(1L)
                .title(" ")
                .description(" ")
                .build();

        mvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(OBJECT_MAPPER.writeValueAsString(taskDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetTaskByIdSuccess() throws Exception {
        TaskDto taskDto = TaskDto.builder()
                .id(1L)
                .build();

        TaskDto responseTaskDto = TaskDto.builder()
                .id(1L)
                .title("Test title")
                .description("Test description")
                .userId(1L)
                .build();

        when(taskService.getTaskById(taskDto.getId())).thenReturn(responseTaskDto);
        mvc.perform(get("/tasks/{id}", taskDto.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(OBJECT_MAPPER.writeValueAsString(taskDto)))
                .andExpect(status().isOk());
    }

    @Test
    void testGetTaskByIdFail() throws Exception {
        long invalidId = 999L;

        when(taskService.getTaskById(invalidId)).thenThrow(new EntityNotFoundException("Task not found"));

        mvc.perform(get("/tasks/{id}", invalidId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateTaskSuccess() throws Exception {
        TaskDto taskDto = TaskDto.builder()
                .id(1L)
                .title("Updated title")
                .description("Updated description")
                .userId(1L)
                .build();

        TaskDto expectedResponseDto = TaskDto.builder()
                .id(1L)
                .title("test title")
                .description("test description")
                .userId(1L)
                .build();

        when(taskService.updateTask(eq(taskDto.getId()), any(TaskDto.class))).thenReturn(expectedResponseDto);

        mvc.perform(put("/tasks/{id}", taskDto.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(OBJECT_MAPPER.writeValueAsString(taskDto)))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdateTaskNotFound() throws Exception {
        TaskDto taskDto = TaskDto.builder()
                .id(99L)
                .title("Updated title")
                .description("Updated description")
                .userId(1L)
                .build();

        when(taskService.updateTask(eq(taskDto.getId()), any(TaskDto.class)))
                .thenThrow(new EntityNotFoundException("Task not found"));

        mvc.perform(put("/tasks/{id}", taskDto.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(OBJECT_MAPPER.writeValueAsString(taskDto)))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Task not found")));
    }

    @Test
    void testUpdateTaskInvalidRequest() throws Exception {
        TaskDto taskDto = TaskDto.builder().build();

        mvc.perform(put("/tasks/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(OBJECT_MAPPER.writeValueAsString(taskDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testDeleteTaskByIdSuccess() throws Exception {
        long taskId = 1L;

        doNothing().when(taskService).deleteTaskById(anyLong());

        mvc.perform(delete("/tasks/{id}", taskId))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteTaskByIdNotFound() throws  Exception {
        long taskId = 999L;

        doThrow(new EntityNotFoundException("Task not found")).when(taskService).deleteTaskById(taskId);
        mvc.perform(delete("/tasks/{id}", taskId))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetTaskSuccess() throws Exception {
        List<TaskDto> tasks = List.of(
                new TaskDto(1L, "Task 1", "Task description 1", 1L),
                new TaskDto(2L, "Task 2", "Task description 2", 2L)
        );


        TaskDto taskResponseDto = TaskDto.builder()
                .id(1L)
                .title("Test title")
                .description("test description")
                .userId(1L)
                .build();


        when(taskService.getTasks(0, 10)).thenReturn(tasks);

        mvc.perform(get("/tasks")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(taskResponseDto)))
                .andExpect(status().isOk());
    }

    @Test
    void testGetTasksInvalidParams() throws Exception {
       mvc.perform(get("/tasks")
               .param("page", "-1")
               .param("size", "10")
               .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isBadRequest());
    }
}
