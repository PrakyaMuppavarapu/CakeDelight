package com.cakedelight.notification.controller;

import com.cakedelight.notification.service.NotificationService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping
    public String sendNotification(@RequestParam String message) {

        return notificationService.sendNotification(message);
    }
}