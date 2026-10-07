package com.handholding.controller;

import com.handholding.entity.AuthUser;
import com.handholding.entity.Student;
import com.handholding.repository.StudentRepository;
import com.handholding.security.SecurityUtils;
import com.handholding.service.VisibilityService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentRepository studentRepository;
    private final VisibilityService visibilityService;

    public StudentController(
            StudentRepository studentRepository,
            VisibilityService visibilityService) {
        this.studentRepository = studentRepository;
        this.visibilityService = visibilityService;
    }

    // GET ALL STUDENTS
    @GetMapping
    public List<Student> getAllStudents() {

        AuthUser user = SecurityUtils.currentUser();

        return visibilityService.visibleStudents(user);
    }

    // GET STUDENT BY ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getStudentById(@PathVariable Long id) {

        AuthUser user = SecurityUtils.currentUser();

        Student student = studentRepository.findById(id).orElse(null);

        if (student == null
                || !visibilityService.canAccessStudent(user, student)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Student not found"));
        }

        return ResponseEntity.ok(student);
    }

    // CREATE STUDENT
    @PostMapping
    public ResponseEntity<Student> createStudent(
            @Valid @RequestBody Student student) {

        Student saved = studentRepository.save(student);

        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // UPDATE STUDENT
    @PutMapping("/{id}")
    public ResponseEntity<?> updateStudent(
            @PathVariable Long id,
            @Valid @RequestBody Student updatedStudent) {

        AuthUser user = SecurityUtils.currentUser();

        Student student = studentRepository.findById(id).orElse(null);

        if (student == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Student not found"));
        }

        if (!visibilityService.canAccessStudent(user, student)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Not allowed to update this student"));
        }

        student.setName(updatedStudent.getName());
        student.setEmail(updatedStudent.getEmail());
        student.setCourse(updatedStudent.getCourse());
        student.setPhone(updatedStudent.getPhone());
        student.setAssignedMentor(
                updatedStudent.getAssignedMentor()
        );

        return ResponseEntity.ok(studentRepository.save(student));
    }

    // ASSIGN MENTOR TO STUDENT
    @PutMapping("/{id}/assign-mentor")
    public ResponseEntity<?> assignMentor(
            @PathVariable Long id,
            @RequestParam String mentorName) {

        AuthUser user = SecurityUtils.currentUser();

        Student student = studentRepository.findById(id).orElse(null);

        if (student == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Student not found"));
        }

        if (!visibilityService.canAccessStudent(user, student)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Not allowed to modify this student"));
        }

        student.setAssignedMentor(mentorName);

        return ResponseEntity.ok(studentRepository.save(student));
    }

    // DELETE STUDENT
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteStudent(@PathVariable Long id) {

        AuthUser user = SecurityUtils.currentUser();

        Student student = studentRepository.findById(id).orElse(null);

        if (student == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Student not found"));
        }

        if (!visibilityService.canAccessStudent(user, student)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Not allowed to delete this student"));
        }

        studentRepository.deleteById(id);

        return ResponseEntity.ok(
                Map.of("message", "Student deleted successfully")
        );
    }
}