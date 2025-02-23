package com.example.aleksey_service.repository;

import com.example.aleksey_service.entity.TaskEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<TaskEntity, Long> {
    @Query(value = "SELECT t FROM TaskEntity t WHERE t.id = :id")
    Optional<TaskEntity> findTaskById(Long id);


    @Query(nativeQuery = true, value = """
                    UPDATE task
                    SET title = COALESCE(:title, task.title),
                        description = COALESCE(:description, task.description),
                        user_id = COALESCE(:user_id, task.user_id)
                        WHERE id = :id
                RETURNING *
            """)
    TaskEntity updateTask(@Param("id") long id, @Param("title") String title, @Param("description") String description, @Param("user_id") long userId);


    @Modifying
    @Query(value = "DELETE FROM TaskEntity t WHERE t.id = :id")
    void deleteTaskById(@Param("id")Long id);

    @Query(value = "SELECT t from TaskEntity t")
    List<TaskEntity> getAllTasks(Pageable pageable);
}
