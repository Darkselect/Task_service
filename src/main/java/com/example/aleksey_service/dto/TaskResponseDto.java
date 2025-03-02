package com.example.aleksey_service.dto;

import com.example.aleksey_service.entity.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class TaskResponseDto {
    private Long id;
    private String title;
    private String description;
    private Long userId;
    private TaskStatus status;
}
