package com.cakedelight.notification.service;

import org.springframework.stereotype.Service;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

@Service
public class NotificationService {

    public String sendNotification(String message) {

        System.out.println("=================================");
        System.out.println("NOTIFICATION SENT");
        System.out.println(message);
        System.out.println("=================================");

        return "Notification sent successfully!";
    }
    @RabbitListener(queues = "cakedelight.order.queue")
    public void receiveOrderEvent(String message) {

        sendNotification(
                "New order received: " + message
        );
    }
}