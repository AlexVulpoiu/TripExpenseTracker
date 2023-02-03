package com.unibuc.fmi.tripexpensetracker.notification.notifications;

import com.unibuc.fmi.tripexpensetracker.dto.notification.NotificationDto;
import com.unibuc.fmi.tripexpensetracker.notification.channels.NotificationChannelDecorator;

abstract class BaseNotification {

    abstract public NotificationChannelDecorator getChannelStack();

    abstract public NotificationDto getNotificationDto();

    public void dispatch() {
        this.getChannelStack().send(
                this.getNotificationDto()
        );
    }
}
