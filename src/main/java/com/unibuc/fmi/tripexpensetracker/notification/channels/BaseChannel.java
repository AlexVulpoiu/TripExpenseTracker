package com.unibuc.fmi.tripexpensetracker.notification.channels;

import com.unibuc.fmi.tripexpensetracker.dto.notification.NotificationDto;

public interface BaseChannel {
    public void send(NotificationDto message);
}
