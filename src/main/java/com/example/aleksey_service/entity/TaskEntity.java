package com.example.aleksey_service.entity;


import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder
@Entity
@Table(name = "task")
public class TaskEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "user_id")
    private Long userId;

    @Version
    @Column(name = "version", nullable = false)
    private Integer version = 0;

    @Enumerated(EnumType.STRING)
    private TaskStatus taskStatus;
}
