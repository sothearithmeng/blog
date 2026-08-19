package com.kshrd.blog.service.impl;

import com.kshrd.blog.dto.request.CreateTaskRequest;
import com.kshrd.blog.dto.request.UpdateTaskRequest;
import com.kshrd.blog.dto.response.PageResponse;
import com.kshrd.blog.dto.response.TaskResponse;
import com.kshrd.blog.entity.Task;
import com.kshrd.blog.exception.ConflictException;
import com.kshrd.blog.exception.ResourceNotFoundException;
import com.kshrd.blog.repository.TaskRepository;
import com.kshrd.blog.service.TaskService;
import com.kshrd.blog.util.CollectionUtils;
import com.kshrd.blog.util.JsonUtils;
import com.kshrd.blog.util.StringUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TaskServiceImpl implements TaskService {

    private static final Logger log = LoggerFactory.getLogger(TaskServiceImpl.class);

    /** Keeps a single batch insert from holding one giant transaction open; see {@link CollectionUtils#partition}. */
    private static final int BATCH_SIZE = 50;

    private final TaskRepository taskRepository;

    public TaskServiceImpl(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public TaskResponse create(CreateTaskRequest request) {
        if (taskRepository.existsByTitleIgnoreCase(request.title())) {
            throw new ConflictException("A task titled '" + request.title() + "' already exists");
        }

        Task task = new Task(request.title(), request.description(), request.reporterEmail());
        Task saved = taskRepository.save(task);
        log.debug("Created task {}", JsonUtils.toJson(TaskResponse.from(saved)));
        return TaskResponse.from(saved);
    }

    @Override
    public List<TaskResponse> createBatch(List<CreateTaskRequest> requests) {
        List<TaskResponse> results = new ArrayList<>();
        for (List<CreateTaskRequest> batch : CollectionUtils.partition(requests, BATCH_SIZE)) {
            batch.forEach(request -> results.add(create(request)));
        }
        return results;
    }

    @Override
    @Transactional(readOnly = true)
    public TaskResponse get(UUID id) {
        return TaskResponse.from(findOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<TaskResponse> list(Pageable pageable) {
        Page<TaskResponse> page = taskRepository.findAll(pageable).map(TaskResponse::from);
        return PageResponse.from(page);
    }

    @Override
    public TaskResponse update(UUID id, UpdateTaskRequest request) {
        Task task = findOrThrow(id);
        if (StringUtils.isNotBlank(request.title())) {
            task.setTitle(request.title());
        }
        if (request.description() != null) {
            task.setDescription(request.description());
        }
        if (request.completed() != null) {
            task.setCompleted(request.completed());
        }
        // No explicit save() needed: `task` is a managed entity inside this @Transactional method,
        // so Hibernate flushes changes to the database automatically at commit.
        return TaskResponse.from(task);
    }

    @Override
    public void delete(UUID id) {
        taskRepository.delete(findOrThrow(id));
    }

    private Task findOrThrow(UUID id) {
        return taskRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Task", id));
    }
}
