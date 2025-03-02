package com.example.aleksey_service.repository;

import com.example.aleksey_service.entity.TasksEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<TasksEntity, Long> {
    @Query(value = "SELECT t FROM TasksEntity t WHERE t.id = :id")
    Optional<TasksEntity> findTaskById(Long id);

    @Query(nativeQuery = true, value = """
                    UPDATE tasks
                    SET title = COALESCE(:title, tasks.title),
                        description = COALESCE(:description, tasks.description),
                        user_id = COALESCE(:user_id, tasks.user_id),
                        status = COALESCE(:status, tasks.status)
                        WHERE id = :id
                RETURNING *
            """)
    Optional<TasksEntity> updateTaskById(@Param("id") long id,
                                         @Param("title") String title,
                                         @Param("description") String description,
                                         @Param("user_id") Long userId,
                                         @Param("status") String status);


    @Modifying
    @Query(value = "DELETE FROM TasksEntity t WHERE t.id = :id")
    void deleteTaskById(@Param("id")Long id);

    @Query(value = "SELECT t from TasksEntity t")
    List<TasksEntity> getAllTasks(Pageable pageable);
}
