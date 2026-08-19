package com.kshrd.blog.service;

import com.kshrd.blog.dto.request.CreateTaskRequest;
import com.kshrd.blog.dto.request.UpdateTaskRequest;
import com.kshrd.blog.dto.response.PageResponse;
import com.kshrd.blog.dto.response.TaskResponse;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;

public interface TaskService {

    TaskResponse create(CreateTaskRequest request);

    List<TaskResponse> createBatch(List<CreateTaskRequest> requests);

    TaskResponse get(UUID id);

    PageResponse<TaskResponse> list(Pageable pageable);

    TaskResponse update(UUID id, UpdateTaskRequest request);

    void delete(UUID id);
}
