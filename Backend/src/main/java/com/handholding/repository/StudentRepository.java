package com.handholding.repository;

import com.handholding.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudentRepository extends JpaRepository<Student, Long> {

    Student findByName(String name);

    Student findByEmail(String email);

    List<Student> findByAssignedMentorIn(List<String> names);
}