package com.unibuc.fmi.tripexpensetracker.notification.notifications;

import com.unibuc.fmi.tripexpensetracker.dto.notification.NotificationDto;
import com.unibuc.fmi.tripexpensetracker.model.Spending;
import com.unibuc.fmi.tripexpensetracker.model.User;
import com.unibuc.fmi.tripexpensetracker.notification.channels.LogNotificationChannel;
import com.unibuc.fmi.tripexpensetracker.notification.channels.MailNotificationChannel;
import com.unibuc.fmi.tripexpensetracker.notification.channels.NotificationChannelDecorator;

public class SpendingUpdatedNotification extends BaseNotification {

    User from;
    User to;
    Spending spending;

    public SpendingUpdatedNotification(User from, User to, Spending spending) {
        this.from = from;
        this.to = to;
        this.spending = spending;
    }


    @Override
    public NotificationChannelDecorator getChannelStack() {
        return new MailNotificationChannel(
                new LogNotificationChannel()
        );
    }

    @Override
    public NotificationDto getNotificationDto() {
        return NotificationDto.builder()
                .subject("One of your spending has been changed")
                .message(String.format("Spending with ID #%d has been changed", spending.getId()))
                .source(this.from.getEmail())
                .destination(this.to.getEmail())
                .build();
    }
}
