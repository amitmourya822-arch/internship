package com.handholding.controller;

import com.handholding.entity.Meeting;
import com.handholding.entity.Notification;
import com.handholding.repository.MeetingRepository;
import com.handholding.repository.NotificationRepository;
import com.handholding.service.EmailService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/meetings")
@CrossOrigin(origins = "*")
public class MeetingController {

    @Autowired
    private MeetingRepository meetingRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private EmailService emailService;

    // GET ALL MEETINGS
    @GetMapping
    public List<Meeting> getAllMeetings() {
        return meetingRepository.findAll();
    }

    // GET MEETING BY ID
    @GetMapping("/{id}")
    public Meeting getMeetingById(@PathVariable Long id) {
        return meetingRepository.findById(id).orElse(null);
    }

    // CREATE MEETING
    @PostMapping
    public Meeting createMeeting(@RequestBody Meeting meeting) {

        Meeting savedMeeting =
                meetingRepository.save(meeting);

        // Save Notification
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

        // Send Email
        if (meeting.getStudentEmail() != null
                && !meeting.getStudentEmail().isEmpty()) {

            emailService.sendMeetingEmail(
                    meeting.getStudentEmail(),
                    meeting.getStudentName(),
                    meeting.getMeetingDate(),
                    meeting.getMeetingTime()
            );
        }

        return savedMeeting;
    }

    // UPDATE MEETING
    @PutMapping("/{id}")
    public Meeting updateMeeting(
            @PathVariable Long id,
            @RequestBody Meeting updatedMeeting) {

        Meeting meeting =
                meetingRepository.findById(id).orElse(null);

        if (meeting == null) {
            return null;
        }

        meeting.setStudentName(updatedMeeting.getStudentName());
        meeting.setMentorName(updatedMeeting.getMentorName());
        meeting.setMeetingDate(updatedMeeting.getMeetingDate());
        meeting.setMeetingTime(updatedMeeting.getMeetingTime());
        meeting.setStatus(updatedMeeting.getStatus());

        return meetingRepository.save(meeting);
    }

    // DELETE MEETING
    @DeleteMapping("/{id}")
    public String deleteMeeting(@PathVariable Long id) {

        if (!meetingRepository.existsById(id)) {
            return "Meeting not found";
        }

        meetingRepository.deleteById(id);

        return "Meeting deleted successfully";
    }
}