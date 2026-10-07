package com.handholding.controller;

import com.handholding.entity.Goal;
import com.handholding.repository.GoalRepository;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/goals")
public class GoalController {

    private final GoalRepository goalRepository;

    public GoalController(GoalRepository goalRepository) {
        this.goalRepository = goalRepository;
    }

    @GetMapping
    public List<Goal> getAllGoals() {
        return goalRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<Goal> createGoal(
            @Valid @RequestBody Goal goal) {

        Goal saved = goalRepository.save(goal);

        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateGoal(
            @PathVariable Long id,
            @Valid @RequestBody Goal updatedGoal) {

        Goal goal = goalRepository.findById(id).orElse(null);

        if (goal == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Goal not found"));
        }

        goal.setTitle(updatedGoal.getTitle());
        goal.setDescription(updatedGoal.getDescription());
        goal.setTargetDate(updatedGoal.getTargetDate());
        goal.setStatus(updatedGoal.getStatus());

        return ResponseEntity.ok(goalRepository.save(goal));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteGoal(@PathVariable Long id) {

        if (!goalRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Goal not found"));
        }

        goalRepository.deleteById(id);

        return ResponseEntity.ok(
                Map.of("message", "Goal deleted successfully")
        );
    }
}