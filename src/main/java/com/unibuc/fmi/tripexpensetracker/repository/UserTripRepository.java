package com.unibuc.fmi.tripexpensetracker.repository;

import com.unibuc.fmi.tripexpensetracker.model.User;
import com.unibuc.fmi.tripexpensetracker.model.UserTrip;
import com.unibuc.fmi.tripexpensetracker.model.UserTripId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserTripRepository extends JpaRepository<UserTrip, UserTripId> {


    Optional<UserTrip> getUserTripById(UserTripId userTripId);
}
