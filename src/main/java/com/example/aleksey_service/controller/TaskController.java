package com.example.aleksey_service.controller;

import com.example.aleksey_service.dto.TaskDto;
import com.example.aleksey_service.dto.TaskResponseDto;
import com.example.aleksey_service.dto.UpdatedDto;
import com.example.aleksey_service.service.TaskService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Validated
@Slf4j
public class TaskController {
    private final TaskService taskService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/tasks")
    public TaskDto createTask(@RequestBody @Valid TaskDto taskDto) {
        return taskService.createTask(taskDto);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/tasks/{id}")
    public TaskDto getTaskById(@PathVariable Long id) {
        return taskService.getTaskById(id);
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("tasks/{id}")
    public TaskResponseDto updateTaskById(@PathVariable Long id, @Valid @RequestBody UpdatedDto taskDto) {
        return taskService.updateTask(id, taskDto);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("tasks/{id}")
    public void deleteTaskById(@PathVariable Long id) {
        taskService.deleteTaskById(id);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/tasks")
    public List<TaskDto> getTasks(@RequestParam(defaultValue = "0") @Min(0) int page,
                                  @RequestParam(defaultValue = "10") @Min(0) @Max(100) int size) {
        return taskService.getTasks(page, size);
    }
}
