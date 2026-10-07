package com.handholding.controller;

import com.handholding.entity.Student;
import com.handholding.repository.StudentRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
@CrossOrigin(origins = "http://localhost:5173")
public class StudentController {

    @Autowired
    private StudentRepository studentRepository;

    // GET ALL STUDENTS
    @GetMapping
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    // GET STUDENT BY ID
    @GetMapping("/{id}")
    public Student getStudentById(@PathVariable Long id) {
        return studentRepository.findById(id).orElse(null);
    }

    // CREATE STUDENT
    @PostMapping
    public Student createStudent(@RequestBody Student student) {
        return studentRepository.save(student);
    }

    // UPDATE STUDENT
    @PutMapping("/{id}")
    public Student updateStudent(
            @PathVariable Long id,
            @RequestBody Student updatedStudent) {

        Student student =
                studentRepository.findById(id).orElse(null);

        if (student == null) {
            return null;
        }

        student.setName(updatedStudent.getName());
        student.setEmail(updatedStudent.getEmail());
        student.setCourse(updatedStudent.getCourse());
        student.setPhone(updatedStudent.getPhone());
        student.setAssignedMentor(
                updatedStudent.getAssignedMentor()
        );

        return studentRepository.save(student);
    }

    // ASSIGN MENTOR TO STUDENT
    @PutMapping("/{id}/assign-mentor")
    public Student assignMentor(
            @PathVariable Long id,
            @RequestParam String mentorName) {

        Student student =
                studentRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException("Student Not Found"));

        student.setAssignedMentor(mentorName);

        return studentRepository.save(student);
    }

    // DELETE STUDENT
    @DeleteMapping("/{id}")
    public String deleteStudent(@PathVariable Long id) {

        if (!studentRepository.existsById(id)) {
            return "Student not found";
        }

        studentRepository.deleteById(id);

        return "Student deleted successfully";
    }
}