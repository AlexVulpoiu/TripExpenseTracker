package com.unibuc.fmi.tripexpensetracker.notification.channels;

import com.unibuc.fmi.tripexpensetracker.dto.notification.NotificationDto;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LogNotificationChannel implements BaseChannel {

    protected void writeToLog(NotificationDto message) {
        log.info(String.format("[%s] (%s)->(%s) : (%s)", message.getSubject(), message.getSource(), message.getDestination(), message.getMessage()));
    }

    @Override
    public void send(NotificationDto message) {
        writeToLog(message);
    }
}
