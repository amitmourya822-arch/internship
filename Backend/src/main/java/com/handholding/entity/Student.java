package com.handholding.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "students")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String email;

    private String course;

    private String phone;

    private String assignedMentor;

    public Student() {
    }

    public Student(Long id, String name, String email,
                   String course, String phone,
                   String assignedMentor) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.course = course;
        this.phone = phone;
        this.assignedMentor = assignedMentor;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAssignedMentor() {
        return assignedMentor;
    }

    public void setAssignedMentor(String assignedMentor) {
        this.assignedMentor = assignedMentor;
    }
}

