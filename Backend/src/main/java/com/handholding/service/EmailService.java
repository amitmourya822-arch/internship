package com.handholding.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendMeetingEmail(
            String toEmail,
            String studentName,
            String meetingDate,
            String meetingTime) {

        SimpleMailMessage message =
                new SimpleMailMessage();

        message.setTo(toEmail);
        message.setSubject("Meeting Scheduled");

        message.setText(
                "Hello " + studentName + ",\n\n" +
                        "Your meeting has been scheduled.\n\n" +
                        "Date: " + meetingDate + "\n" +
                        "Time: " + meetingTime + "\n\n" +
                        "Regards,\nSmart Hand Holding System"
        );

        mailSender.send(message);
    }
}