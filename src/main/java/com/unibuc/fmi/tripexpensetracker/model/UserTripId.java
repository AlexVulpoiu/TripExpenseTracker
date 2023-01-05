package com.unibuc.fmi.tripexpensetracker.model;

import lombok.*;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class UserTripId implements Serializable {
    private Long userId;

    private Long tripId;
}
