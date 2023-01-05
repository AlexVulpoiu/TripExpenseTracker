package com.unibuc.fmi.tripexpensetracker.model;


import lombok.*;

import javax.persistence.*;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(	name = "users_trips")
public class UserTrip {
    @EmbeddedId
    private UserTripId id;

    @MapsId("userId")
    @ManyToOne(fetch= FetchType.LAZY)
    private User user;

    @MapsId("tripId")
    @ManyToOne(fetch= FetchType.LAZY)
    private Trip trip;
}
