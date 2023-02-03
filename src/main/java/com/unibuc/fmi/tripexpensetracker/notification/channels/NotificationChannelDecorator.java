package com.unibuc.fmi.tripexpensetracker.notification.channels;

import com.unibuc.fmi.tripexpensetracker.dto.notification.NotificationDto;

abstract public class NotificationChannelDecorator implements BaseChannel {
    private final BaseChannel wrapped;

    NotificationChannelDecorator(BaseChannel source) {
        this.wrapped = source;
    }

    @Override
    public void send(NotificationDto message) {
        wrapped.send(message);
    }
}