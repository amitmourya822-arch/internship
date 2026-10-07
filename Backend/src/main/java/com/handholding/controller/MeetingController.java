package com.handholding.controller;

import com.handholding.entity.AuthUser;
import com.handholding.entity.Meeting;
import com.handholding.entity.Notification;
import com.handholding.entity.Student;
import com.handholding.repository.MeetingRepository;
import com.handholding.repository.NotificationRepository;
import com.handholding.repository.StudentRepository;
import com.handholding.security.SecurityUtils;
import com.handholding.service.EmailService;
import com.handholding.service.VisibilityService;

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
    private final VisibilityService visibilityService;

    public MeetingController(
            MeetingRepository meetingRepository,
            NotificationRepository notificationRepository,
            StudentRepository studentRepository,
            EmailService emailService,
            VisibilityService visibilityService) {
        this.meetingRepository = meetingRepository;
        this.notificationRepository = notificationRepository;
        this.studentRepository = studentRepository;
        this.emailService = emailService;
        this.visibilityService = visibilityService;
    }

    // GET ALL MEETINGS
    @GetMapping
    public List<Meeting> getAllMeetings() {

        AuthUser user = SecurityUtils.currentUser();

        return visibilityService.visibleMeetings(user);
    }

    // GET MEETING BY ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getMeetingById(@PathVariable Long id) {

        AuthUser user = SecurityUtils.currentUser();

        Meeting meeting = meetingRepository.findById(id).orElse(null);

        if (meeting == null
                || !visibilityService.canAccessMeeting(user, meeting)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Meeting not found"));
        }

        return ResponseEntity.ok(meeting);
    }

    // CREATE MEETING
    @PostMapping
    public ResponseEntity<?> createMeeting(
            @Valid @RequestBody Meeting meeting) {

        AuthUser user = SecurityUtils.currentUser();

        String studentEmail = meeting.getStudentEmail();

        if (studentEmail == null || studentEmail.isBlank()) {

            Student student = studentRepository.findByName(
                    meeting.getStudentName()
            );

            if (student != null && student.getEmail() != null) {
                studentEmail = student.getEmail();
            }
        }

        if (visibilityService.isMentorScope(user)) {

            Student student = findVisibleStudent(user, meeting);

            if (student == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of(
                                "message",
                                "Student is not assigned to you"
                        ));
            }

            if (student.getEmail() != null) {
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

    private Student findVisibleStudent(AuthUser user, Meeting meeting) {

        for (Student student : visibilityService.visibleStudents(user)) {

            boolean nameMatch = meeting.getStudentName() != null
                    && meeting.getStudentName().equalsIgnoreCase(
                            student.getName()
                    );

            boolean emailMatch = meeting.getStudentEmail() != null
                    && meeting.getStudentEmail().equalsIgnoreCase(
                            student.getEmail()
                    );

            if (nameMatch || emailMatch) {
                return student;
            }
        }

        return null;
    }

    // UPDATE MEETING
    @PutMapping("/{id}")
    public ResponseEntity<?> updateMeeting(
            @PathVariable Long id,
            @Valid @RequestBody Meeting updatedMeeting) {

        AuthUser user = SecurityUtils.currentUser();

        Meeting meeting = meetingRepository.findById(id).orElse(null);

        if (meeting == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Meeting not found"));
        }

        if (!visibilityService.canAccessMeeting(user, meeting)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Not allowed to update this meeting"));
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

        AuthUser user = SecurityUtils.currentUser();

        Meeting meeting = meetingRepository.findById(id).orElse(null);

        if (meeting == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Meeting not found"));
        }

        if (!visibilityService.canAccessMeeting(user, meeting)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Not allowed to delete this meeting"));
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