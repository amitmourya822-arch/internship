package com.handholding.controller;

import com.handholding.entity.Task;
import com.handholding.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "*")
public class TaskController {

    @Autowired
    private TaskRepository taskRepository;

    @GetMapping
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    @PostMapping
    public Task createTask(@RequestBody Task task) {
        return taskRepository.save(task);
    }

    @PutMapping("/{id}")
    public Task updateTask(
            @PathVariable Long id,
            @RequestBody Task updatedTask) {

        Task task = taskRepository.findById(id).orElse(null);

        if (task == null) {
            return null;
        }

        task.setTitle(updatedTask.getTitle());
        task.setDescription(updatedTask.getDescription());
        task.setAssignedTo(updatedTask.getAssignedTo());
        task.setDueDate(updatedTask.getDueDate());
        task.setStatus(updatedTask.getStatus());

        return taskRepository.save(task);
    }

    @DeleteMapping("/{id}")
    public String deleteTask(@PathVariable Long id) {

        if (!taskRepository.existsById(id)) {
            return "Task not found";
        }

        taskRepository.deleteById(id);

        return "Task deleted successfully";
    }
}