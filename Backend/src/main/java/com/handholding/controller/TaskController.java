package com.handholding.controller;

import com.handholding.entity.AuthUser;
import com.handholding.entity.Task;
import com.handholding.repository.TaskRepository;
import com.handholding.security.SecurityUtils;
import com.handholding.service.VisibilityService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskRepository taskRepository;
    private final VisibilityService visibilityService;

    public TaskController(
            TaskRepository taskRepository,
            VisibilityService visibilityService) {
        this.taskRepository = taskRepository;
        this.visibilityService = visibilityService;
    }

    @GetMapping
    public List<Task> getAllTasks() {

        AuthUser user = SecurityUtils.currentUser();

        return visibilityService.visibleTasks(user);
    }

    // GET TASK BY ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getTaskById(@PathVariable Long id) {

        AuthUser user = SecurityUtils.currentUser();

        Task task = taskRepository.findById(id).orElse(null);

        if (task == null
                || !visibilityService.canAccessTask(user, task)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Task not found"));
        }

        return ResponseEntity.ok(task);
    }

    @PostMapping
    public ResponseEntity<Task> createTask(
            @Valid @RequestBody Task task) {

        Task saved = taskRepository.save(task);

        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTask(
            @PathVariable Long id,
            @Valid @RequestBody Task updatedTask) {

        AuthUser user = SecurityUtils.currentUser();

        Task task = taskRepository.findById(id).orElse(null);

        if (task == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Task not found"));
        }

        if (!visibilityService.canAccessTask(user, task)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Not allowed to update this task"));
        }

        task.setTitle(updatedTask.getTitle());
        task.setDescription(updatedTask.getDescription());
        task.setAssignedTo(updatedTask.getAssignedTo());
        task.setDueDate(updatedTask.getDueDate());
        task.setStatus(updatedTask.getStatus());

        return ResponseEntity.ok(taskRepository.save(task));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable Long id) {

        AuthUser user = SecurityUtils.currentUser();

        Task task = taskRepository.findById(id).orElse(null);

        if (task == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Task not found"));
        }

        if (!visibilityService.canAccessTask(user, task)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Not allowed to delete this task"));
        }

        taskRepository.deleteById(id);

        return ResponseEntity.ok(
                Map.of("message", "Task deleted successfully")
        );
    }
}