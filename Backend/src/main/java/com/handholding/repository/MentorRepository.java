package com.handholding.repository;

import com.handholding.entity.Mentor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MentorRepository extends JpaRepository<Mentor, Long> {

    List<Mentor> findByEmail(String email);
}