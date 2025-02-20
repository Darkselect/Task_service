package com.example.aleksey_service.dto;

import jakarta.validation.constraints.NotBlank;
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

    private Long userId;
}
