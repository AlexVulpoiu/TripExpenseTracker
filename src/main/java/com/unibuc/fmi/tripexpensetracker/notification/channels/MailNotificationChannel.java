package com.unibuc.fmi.tripexpensetracker.notification.channels;

import com.unibuc.fmi.tripexpensetracker.dto.notification.NotificationDto;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

public class MailNotificationChannel extends NotificationChannelDecorator {

    JavaMailSender mailSender;

    public MailNotificationChannel(BaseChannel source) {
        super(source);
    }

    protected void sendMail(NotificationDto message) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(message.getSource());
        msg.setTo(message.getDestination());
        msg.setSubject(message.getSubject());
        msg.setText(message.getMessage());

        // mailSender.send(msg);
    }

    @Override
    public void send(NotificationDto message) {
        super.send(message);
        sendMail(message);
    }
}
