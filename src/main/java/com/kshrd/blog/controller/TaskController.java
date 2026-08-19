package com.kshrd.blog.controller;

import com.kshrd.blog.dto.request.CreateTaskRequest;
import com.kshrd.blog.dto.request.CreateTasksRequest;
import com.kshrd.blog.dto.request.UpdateTaskRequest;
import com.kshrd.blog.dto.response.PageResponse;
import com.kshrd.blog.dto.response.TaskResponse;
import com.kshrd.blog.service.TaskService;
import com.kshrd.blog.util.ApiResponse;
import com.kshrd.blog.util.PageUtils;
import com.kshrd.blog.util.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * End-to-end reference flow: controller -> service -> repository -> entity, wired through the
 * shared {@code dto}, {@code exception}, {@code util}, and {@code config} packages. Copy this
 * slice as the starting point for a new resource.
 */
@RestController
@RequestMapping("/api/v1/tasks")
@Tag(name = "Tasks", description = "Example CRUD flow demonstrating the starter's layers")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    @Operation(summary = "Create a task")
    public ResponseEntity<ApiResponse<TaskResponse>> create(@Valid @RequestBody CreateTaskRequest request) {
        return ResponseUtil.created(taskService.create(request), "Task created");
    }

    @PostMapping("/batch")
    @Operation(summary = "Create multiple tasks in one call")
    public ResponseEntity<ApiResponse<List<TaskResponse>>> createBatch(@Valid @RequestBody CreateTasksRequest request) {
        return ResponseUtil.created(taskService.createBatch(request.tasks()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a task by id")
    public ResponseEntity<ApiResponse<TaskResponse>> get(@PathVariable UUID id) {
        return ResponseUtil.ok(taskService.get(id));
    }

    @GetMapping
    @Operation(summary = "List tasks, paginated")
    public ResponseEntity<ApiResponse<PageResponse<TaskResponse>>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction) {
        Pageable pageable = PageUtils.of(page, size, sortBy, direction);
        return ResponseUtil.ok(taskService.list(pageable));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Partially update a task")
    public ResponseEntity<ApiResponse<TaskResponse>> update(
            @PathVariable UUID id, @Valid @RequestBody UpdateTaskRequest request) {
        return ResponseUtil.ok(taskService.update(id, request), "Task updated");
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a task")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        taskService.delete(id);
        return ResponseUtil.noContent();
    }
}
