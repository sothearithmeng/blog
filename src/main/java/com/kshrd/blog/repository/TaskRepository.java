package com.kshrd.blog.repository;

import com.kshrd.blog.entity.Task;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, UUID> {

    boolean existsByTitleIgnoreCase(String title);
}
