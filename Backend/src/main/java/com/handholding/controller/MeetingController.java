package com.handholding.controller;

import com.handholding.entity.Meeting;
import com.handholding.entity.Notification;
import com.handholding.entity.Student;
import com.handholding.repository.MeetingRepository;
import com.handholding.repository.NotificationRepository;
import com.handholding.repository.StudentRepository;
import com.handholding.service.EmailService;

import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/meetings")
public class MeetingController {

    private static final Logger log = LoggerFactory.getLogger(MeetingController.class);

    private final MeetingRepository meetingRepository;
    private final NotificationRepository notificationRepository;
    private final StudentRepository studentRepository;
    private final EmailService emailService;

    public MeetingController(
            MeetingRepository meetingRepository,
            NotificationRepository notificationRepository,
            StudentRepository studentRepository,
            EmailService emailService) {
        this.meetingRepository = meetingRepository;
        this.notificationRepository = notificationRepository;
        this.studentRepository = studentRepository;
        this.emailService = emailService;
    }

    // GET ALL MEETINGS
    @GetMapping
    public List<Meeting> getAllMeetings() {
        return meetingRepository.findAll();
    }

    // GET MEETING BY ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getMeetingById(@PathVariable Long id) {

        Meeting meeting = meetingRepository.findById(id).orElse(null);

        if (meeting == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Meeting not found"));
        }

        return ResponseEntity.ok(meeting);
    }

    // CREATE MEETING
    @PostMapping
    public ResponseEntity<Meeting> createMeeting(
            @Valid @RequestBody Meeting meeting) {

        String studentEmail = meeting.getStudentEmail();

        if (studentEmail == null || studentEmail.isBlank()) {
            Student student = studentRepository.findByName(
                    meeting.getStudentName()
            );
            if (student != null && student.getEmail() != null) {
                studentEmail = student.getEmail();
            }
        }

        if (studentEmail != null && !studentEmail.isBlank()) {
            meeting.setStudentEmail(studentEmail);
        }

        Meeting savedMeeting = meetingRepository.save(meeting);

        Notification notification =
                new Notification(
                        "Meeting Scheduled",
                        meeting.getStudentName()
                                + " meeting with "
                                + meeting.getMentorName()
                                + " on "
                                + meeting.getMeetingDate()
                );

        notificationRepository.save(notification);

        sendMeetingEmail(meeting, studentEmail);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedMeeting);
    }

    // UPDATE MEETING
    @PutMapping("/{id}")
    public ResponseEntity<?> updateMeeting(
            @PathVariable Long id,
            @Valid @RequestBody Meeting updatedMeeting) {

        Meeting meeting = meetingRepository.findById(id).orElse(null);

        if (meeting == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Meeting not found"));
        }

        meeting.setStudentName(updatedMeeting.getStudentName());
        meeting.setMentorName(updatedMeeting.getMentorName());
        meeting.setMeetingDate(updatedMeeting.getMeetingDate());
        meeting.setMeetingTime(updatedMeeting.getMeetingTime());
        meeting.setStatus(updatedMeeting.getStatus());

        if (updatedMeeting.getStudentEmail() != null) {
            meeting.setStudentEmail(updatedMeeting.getStudentEmail());
        }

        return ResponseEntity.ok(meetingRepository.save(meeting));
    }

    // DELETE MEETING
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMeeting(@PathVariable Long id) {

        if (!meetingRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Meeting not found"));
        }

        meetingRepository.deleteById(id);

        return ResponseEntity.ok(
                Map.of("message", "Meeting deleted successfully")
        );
    }

    private void sendMeetingEmail(Meeting meeting, String toEmail) {

        if (toEmail == null || toEmail.isBlank()) {
            return;
        }

        try {

            emailService.sendMeetingEmail(
                    toEmail,
                    meeting.getStudentName(),
                    meeting.getMeetingDate(),
                    meeting.getMeetingTime()
            );

        } catch (Exception e) {

            log.warn(
                    "Failed to send meeting email to {}: {}",
                    toEmail,
                    e.getMessage()
            );
        }
    }
}