package com.handholding.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "meetings")
public class Meeting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Student name is required")
    private String studentName;

    @Email(message = "Invalid email format")
    private String studentEmail;

    private String mentorName;

    @NotBlank(message = "Meeting date is required")
    private String meetingDate;

    @NotBlank(message = "Meeting time is required")
    private String meetingTime;

    private String status;

    public Meeting() {
    }

    public Meeting(Long id,
                   String studentName,
                   String studentEmail,
                   String mentorName,
                   String meetingDate,
                   String meetingTime,
                   String status) {

        this.id = id;
        this.studentName = studentName;
        this.studentEmail = studentEmail;
        this.mentorName = mentorName;
        this.meetingDate = meetingDate;
        this.meetingTime = meetingTime;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getStudentEmail() {
        return studentEmail;
    }

    public void setStudentEmail(String studentEmail) {
        this.studentEmail = studentEmail;
    }

    public String getMentorName() {
        return mentorName;
    }

    public void setMentorName(String mentorName) {
        this.mentorName = mentorName;
    }

    public String getMeetingDate() {
        return meetingDate;
    }

    public void setMeetingDate(String meetingDate) {
        this.meetingDate = meetingDate;
    }

    public String getMeetingTime() {
        return meetingTime;
    }

    public void setMeetingTime(String meetingTime) {
        this.meetingTime = meetingTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

