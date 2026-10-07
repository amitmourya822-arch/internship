package com.handholding.controller;

import com.handholding.entity.AuthUser;
import com.handholding.entity.Goal;
import com.handholding.repository.GoalRepository;
import com.handholding.security.SecurityUtils;
import com.handholding.service.VisibilityService;

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
    private final VisibilityService visibilityService;

    public GoalController(
            GoalRepository goalRepository,
            VisibilityService visibilityService) {
        this.goalRepository = goalRepository;
        this.visibilityService = visibilityService;
    }

    @GetMapping
    public List<Goal> getAllGoals() {

        AuthUser user = SecurityUtils.currentUser();

        return visibilityService.visibleGoals(user);
    }

    // GET GOAL BY ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getGoalById(@PathVariable Long id) {

        AuthUser user = SecurityUtils.currentUser();

        Goal goal = goalRepository.findById(id).orElse(null);

        if (goal == null
                || !visibilityService.canAccessGoal(user, goal)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Goal not found"));
        }

        return ResponseEntity.ok(goal);
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

        AuthUser user = SecurityUtils.currentUser();

        Goal goal = goalRepository.findById(id).orElse(null);

        if (goal == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Goal not found"));
        }

        if (!visibilityService.canAccessGoal(user, goal)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Not allowed to update this goal"));
        }

        goal.setTitle(updatedGoal.getTitle());
        goal.setDescription(updatedGoal.getDescription());
        goal.setTargetDate(updatedGoal.getTargetDate());
        goal.setStatus(updatedGoal.getStatus());
        goal.setAssignedTo(updatedGoal.getAssignedTo());

        return ResponseEntity.ok(goalRepository.save(goal));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteGoal(@PathVariable Long id) {

        AuthUser user = SecurityUtils.currentUser();

        Goal goal = goalRepository.findById(id).orElse(null);

        if (goal == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Goal not found"));
        }

        if (!visibilityService.canAccessGoal(user, goal)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Not allowed to delete this goal"));
        }

        goalRepository.deleteById(id);

        return ResponseEntity.ok(
                Map.of("message", "Goal deleted successfully")
        );
    }
}