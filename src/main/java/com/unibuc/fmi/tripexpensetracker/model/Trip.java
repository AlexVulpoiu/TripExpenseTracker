package com.unibuc.fmi.tripexpensetracker.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(	name = "trips")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Trip {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 20)
    private String title;

    @NotBlank
    @Size(max = 30)
    private String location;

    @NotNull
    private LocalDate startDate;

    @NotNull
    private LocalDate endDate;

    @JsonIgnore
    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<UserTrip> users;

    public void addUserToTrip(User user, UserTrip userTrip) {
        this.users.add(userTrip);
        user.getTrips().add(userTrip);
    }
}
