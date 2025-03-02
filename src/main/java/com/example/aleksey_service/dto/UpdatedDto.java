package com.example.aleksey_service.dto;


import com.example.aleksey_service.entity.TaskStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UpdatedDto {
    @NotBlank(message = "title can`t be blank")
    private String title;
    private String description;

    private Long userId;

    @JsonProperty("status")
    @NotNull(message = "status can`t be blank")
    private TaskStatus taskStatus;
}
