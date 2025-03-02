package com.example.aleksey_service.dto;

import com.example.aleksey_service.entity.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class TaskDto {
    private long id;

    @NotBlank(message = "title can not be null or empty")
    private String title;

    @NotBlank(message = "description can not be null or empty")
    private String description;

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "status can`t be null")
    private TaskStatus status;
}
