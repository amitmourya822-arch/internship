package com.handholding.controller;

import com.handholding.entity.Goal;
import com.handholding.repository.GoalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/goals")
@CrossOrigin(origins = "*")
public class GoalController {

    @Autowired
    private GoalRepository goalRepository;

    @GetMapping
    public List<Goal> getAllGoals() {
        return goalRepository.findAll();
    }

    @PostMapping
    public Goal createGoal(@RequestBody Goal goal) {
        return goalRepository.save(goal);
    }

    @PutMapping("/{id}")
    public Goal updateGoal(
            @PathVariable Long id,
            @RequestBody Goal updatedGoal) {

        Goal goal =
                goalRepository.findById(id).orElse(null);

        if (goal == null) {
            return null;
        }

        goal.setTitle(updatedGoal.getTitle());
        goal.setDescription(updatedGoal.getDescription());
        goal.setTargetDate(updatedGoal.getTargetDate());
        goal.setStatus(updatedGoal.getStatus());

        return goalRepository.save(goal);
    }

    @DeleteMapping("/{id}")
    public String deleteGoal(@PathVariable Long id) {

        if (!goalRepository.existsById(id)) {
            return "Goal not found";
        }

        goalRepository.deleteById(id);

        return "Goal deleted successfully";
    }
}