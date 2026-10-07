package com.handholding.controller;

import com.handholding.entity.Mentor;
import com.handholding.repository.MentorRepository;

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

    public MentorController(MentorRepository mentorRepository) {
        this.mentorRepository = mentorRepository;
    }

    @GetMapping
    public List<Mentor> getAllMentors() {
        return mentorRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getMentorById(@PathVariable Long id) {

        Mentor mentor = mentorRepository.findById(id).orElse(null);

        if (mentor == null) {
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

        Mentor mentor = mentorRepository.findById(id).orElse(null);

        if (mentor == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Mentor not found"));
        }

        mentor.setName(updatedMentor.getName());
        mentor.setEmail(updatedMentor.getEmail());
        mentor.setExpertise(updatedMentor.getExpertise());
        mentor.setPhone(updatedMentor.getPhone());

        return ResponseEntity.ok(mentorRepository.save(mentor));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMentor(@PathVariable Long id) {

        if (!mentorRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Mentor not found"));
        }

        mentorRepository.deleteById(id);

        return ResponseEntity.ok(
                Map.of("message", "Mentor deleted successfully")
        );
    }
}