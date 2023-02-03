package com.unibuc.fmi.tripexpensetracker.notification.notifications;

import com.unibuc.fmi.tripexpensetracker.dto.notification.NotificationDto;
import com.unibuc.fmi.tripexpensetracker.model.Spending;
import com.unibuc.fmi.tripexpensetracker.model.User;
import com.unibuc.fmi.tripexpensetracker.notification.channels.LogNotificationChannel;
import com.unibuc.fmi.tripexpensetracker.notification.channels.MailNotificationChannel;
import com.unibuc.fmi.tripexpensetracker.notification.channels.NotificationChannelDecorator;

public class AddedToSpendingNotification extends BaseNotification {

    User from;
    User to;
    Spending spending;

    public AddedToSpendingNotification(User from, User to, Spending spending) {
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
                .message(String.format("You have been added to spending with ID #%d", spending.getId()))
                .source(this.from.getEmail())
                .destination(this.to.getEmail())
                .build();
    }
}
