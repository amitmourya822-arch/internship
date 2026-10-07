package com.handholding.controller;

import com.handholding.entity.AuthUser;
import com.handholding.entity.Mentor;
import com.handholding.repository.MentorRepository;
import com.handholding.security.SecurityUtils;
import com.handholding.service.VisibilityService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/mentors")
public class MentorController {

    private final MentorRepository mentorRepository;
    private final VisibilityService visibilityService;

    public MentorController(
            MentorRepository mentorRepository,
            VisibilityService visibilityService) {
        this.mentorRepository = mentorRepository;
        this.visibilityService = visibilityService;
    }

    @GetMapping
    public List<Mentor> getAllMentors() {

        AuthUser user = SecurityUtils.currentUser();

        return visibilityService.visibleMentors(user);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getMentorById(@PathVariable Long id) {

        AuthUser user = SecurityUtils.currentUser();

        Mentor mentor = mentorRepository.findById(id).orElse(null);

        if (mentor == null
                || !visibilityService.canAccessMentor(user, mentor)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Mentor not found"));
        }

        return ResponseEntity.ok(mentor);
    }

    @PostMapping
    public ResponseEntity<Mentor> createMentor(
            @Valid @RequestBody Mentor mentor) {

        Mentor saved = mentorRepository.save(mentor);

        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateMentor(
            @PathVariable Long id,
            @Valid @RequestBody Mentor updatedMentor) {

        AuthUser user = SecurityUtils.currentUser();

        Mentor mentor = mentorRepository.findById(id).orElse(null);

        if (mentor == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Mentor not found"));
        }

        if (!visibilityService.canAccessMentor(user, mentor)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Not allowed to update this mentor"));
        }

        mentor.setName(updatedMentor.getName());
        mentor.setEmail(updatedMentor.getEmail());
        mentor.setExpertise(updatedMentor.getExpertise());
        mentor.setPhone(updatedMentor.getPhone());

        return ResponseEntity.ok(mentorRepository.save(mentor));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMentor(@PathVariable Long id) {

        AuthUser user = SecurityUtils.currentUser();

        Mentor mentor = mentorRepository.findById(id).orElse(null);

        if (mentor == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Mentor not found"));
        }

        if (!visibilityService.canAccessMentor(user, mentor)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Not allowed to delete this mentor"));
        }

        mentorRepository.deleteById(id);

        return ResponseEntity.ok(
                Map.of("message", "Mentor deleted successfully")
        );
    }
}