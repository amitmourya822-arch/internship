package com.handholding.controller;

import com.handholding.entity.Mentor;
import com.handholding.repository.MentorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mentors")
@CrossOrigin(origins = "*")
public class MentorController {

    @Autowired
    private MentorRepository mentorRepository;

    @GetMapping
    public List<Mentor> getAllMentors() {
        return mentorRepository.findAll();
    }

    @GetMapping("/{id}")
    public Mentor getMentorById(@PathVariable Long id) {
        return mentorRepository.findById(id).orElse(null);
    }

    @PostMapping
    public Mentor createMentor(@RequestBody Mentor mentor) {
        return mentorRepository.save(mentor);
    }

    @PutMapping("/{id}")
    public Mentor updateMentor(@PathVariable Long id,
                               @RequestBody Mentor updatedMentor) {

        Mentor mentor = mentorRepository.findById(id).orElse(null);

        if (mentor == null) {
            return null;
        }

        mentor.setName(updatedMentor.getName());
        mentor.setEmail(updatedMentor.getEmail());
        mentor.setExpertise(updatedMentor.getExpertise());
        mentor.setPhone(updatedMentor.getPhone());

        return mentorRepository.save(mentor);
    }

    @DeleteMapping("/{id}")
    public String deleteMentor(@PathVariable Long id) {

        if (!mentorRepository.existsById(id)) {
            return "Mentor not found";
        }

        mentorRepository.deleteById(id);

        return "Mentor deleted successfully";
    }
}