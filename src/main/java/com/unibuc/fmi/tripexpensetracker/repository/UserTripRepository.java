package com.unibuc.fmi.tripexpensetracker.repository;

import com.unibuc.fmi.tripexpensetracker.model.Trip;
import com.unibuc.fmi.tripexpensetracker.model.User;
import com.unibuc.fmi.tripexpensetracker.model.UserTrip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserTripRepository extends JpaRepository<UserTrip, Long> {

    Optional<UserTrip> findByUserAndTrip(User user, Trip trip);

    @Query("SELECT ut.trip " +
            "FROM UserTrip ut " +
            "WHERE ut.user.id = ?1")
    List<Trip> findTripsByUserId(Long userId);
}
