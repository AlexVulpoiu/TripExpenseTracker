package com.unibuc.fmi.tripexpensetracker.dto.notification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDto {

    private String subject;
    private String message;
    private String source;
    private String destination;


}
