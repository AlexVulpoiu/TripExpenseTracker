package com.unibuc.fmi.tripexpensetracker.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
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

    @JsonIgnore
    @OneToMany(mappedBy = "userTrip", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Spending> spendings;
}
