package com.handholding.controller;

import com.handholding.entity.Notification;
import com.handholding.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "*")
public class NotificationController {

    @Autowired
    private NotificationRepository notificationRepository;

    @GetMapping
    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }

    @GetMapping("/{id}")
    public Notification getNotificationById(@PathVariable Long id) {
        return notificationRepository.findById(id).orElse(null);
    }


    @PutMapping("/read/{id}")
    public Notification markAsRead(@PathVariable Long id) {

        Notification notification =
                notificationRepository.findById(id)
                        .orElse(null);

        if (notification == null) {
            return null;
        }

        notification.setRead(true);

        return notificationRepository.save(notification);
    }

    @DeleteMapping("/{id}")
    public String deleteNotification(
            @PathVariable Long id) {

        if (!notificationRepository.existsById(id)) {
            return "Notification not found";
        }

        notificationRepository.deleteById(id);

        return "Notification deleted successfully";
    }
}

