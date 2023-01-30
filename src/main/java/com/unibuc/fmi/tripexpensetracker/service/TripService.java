package com.unibuc.fmi.tripexpensetracker.service;

import com.unibuc.fmi.tripexpensetracker.dto.*;
import com.unibuc.fmi.tripexpensetracker.model.*;
import com.unibuc.fmi.tripexpensetracker.repository.SpendingRepository;
import com.unibuc.fmi.tripexpensetracker.repository.TripRepository;
import com.unibuc.fmi.tripexpensetracker.repository.UserRepository;
import com.unibuc.fmi.tripexpensetracker.repository.UserTripRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TripService {
    private final TripRepository tripRepository;

    private final UserRepository userRepository;

    private final UserTripRepository userTripRepository;

    private final SpendingRepository spendingRepository;

    @Autowired
    public TripService(TripRepository tripRepository, UserRepository userRepository, UserTripRepository userTripRepository, SpendingRepository spendingRepository) {
        this.tripRepository = tripRepository;
        this.userRepository = userRepository;
        this.userTripRepository = userTripRepository;
        this.spendingRepository = spendingRepository;
    }

    public ResponseEntity<?> addTrip(TripRequestDto tripRequestDto) {

        Trip trip = Trip.builder()
                .title(tripRequestDto.getTitle())
                .location(tripRequestDto.getLocation())
                .startDate(tripRequestDto.getStartDate())
                .endDate(tripRequestDto.getEndDate())
                .build();
        tripRepository.save(trip);

        return ResponseEntity.ok(new MessageResponseDto("Trip added successfully!"));
    }

    public ResponseEntity<?> deleteTrip(Long id) {
        if (!tripRepository.existsById(id)) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponseDto("Error: Trip does not exist!"));
        }

        tripRepository.deleteById(id);
        return ResponseEntity.ok(new MessageResponseDto("Trip deleted successfully!"));
    }

    public ResponseEntity<?> addUsersToTrip(Long tripId, NewTripUsersDto newTripUsersDto) {
        Optional<Trip> trip = tripRepository.findById(tripId);

        if (trip.isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponseDto("Error: Trip does not exist!"));
        }

        List<Long> userIds = newTripUsersDto.getUsers();
        List<User> users = new ArrayList<>();

        for (Long userId : userIds) {
            Optional<User> user = userRepository.findById(userId);

            if (user.isEmpty()) {
                return ResponseEntity
                        .badRequest()
                        .body(new MessageResponseDto("Error: User does not exist!"));
            }

            users.add(user.get());
        }

        for (User user : users) {

            if (userTripRepository.findByUserAndTrip(user, trip.get()).isEmpty()) {
                UserTrip userTrip = UserTrip.builder()
                        .user(user)
                        .trip(trip.get())
                        .build();
                userTripRepository.save(userTrip);
            }
        }

        return ResponseEntity.ok(new MessageResponseDto("User added to trip successfully!"));
    }

    public ResponseEntity<?> getTripDetails(Long tripId) {

        Optional<Trip> optionalTrip = tripRepository.findById(tripId);

        if (optionalTrip.isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponseDto("Error: Trip does not exist!"));
        }

        List<UserDto> usersDto = new ArrayList<>();
        Trip trip = optionalTrip.get();
        for (UserTrip userTrip : trip.getUsers()) {
            User user = userTrip.getUser();
            UserDto userDto = UserDto.builder()
                    .email(user.getEmail())
                    .username(user.getUsername())
                    .build();
            usersDto.add(userDto);
        }

        TripResponseDto tripResponseDto = TripResponseDto.builder()
                .title(trip.getTitle())
                .location(trip.getLocation())
                .startDate(trip.getStartDate())
                .endDate(trip.getEndDate())
                .usersDto(usersDto)
                .build();

        return new ResponseEntity<>(tripResponseDto, HttpStatus.OK);
    }

    public ResponseEntity<?> deleteUserFromTrip(@PathVariable Long tripId, @PathVariable Long userId) {
        Optional<Trip> trip = tripRepository.findById(tripId);
        Optional<User> user = userRepository.findById(userId);

        if (trip.isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponseDto("Error: Trip does not exist!"));
        }

        if (user.isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponseDto("Error: User does not exist!"));
        }

        Optional<UserTrip> optionalUserTrip = userTripRepository.findByUserAndTrip(user.get(), trip.get());

        if (optionalUserTrip.isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponseDto("Error: The user is not in this trip!"));
        }

        userTripRepository.deleteById(optionalUserTrip.get().getId());

        return ResponseEntity.ok(new MessageResponseDto("User deleted from trip successfully!"));
    }
}
