package com.unibuc.fmi.tripexpensetracker.service;

import com.unibuc.fmi.tripexpensetracker.dto.*;
import com.unibuc.fmi.tripexpensetracker.model.Trip;
import com.unibuc.fmi.tripexpensetracker.model.User;
import com.unibuc.fmi.tripexpensetracker.model.UserTrip;
import com.unibuc.fmi.tripexpensetracker.model.UserTripId;
import com.unibuc.fmi.tripexpensetracker.repository.TripRepository;
import com.unibuc.fmi.tripexpensetracker.repository.UserRepository;
import com.unibuc.fmi.tripexpensetracker.repository.UserTripRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TripService {
    private final TripRepository tripRepository;

    private final UserRepository userRepository;

    private final UserTripRepository userTripRepository;

    @Autowired
    public TripService(TripRepository tripRepository, UserRepository userRepository, UserTripRepository userTripRepository) {
        this.tripRepository = tripRepository;
        this.userRepository = userRepository;
        this.userTripRepository = userTripRepository;
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
        if (!tripRepository.existsById(tripId)) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponseDto("Error: Trip does not exist!"));
        }

        List<Long> userIds = newTripUsersDto.getUsers();

        for (Long userId : userIds) {
            if (!userRepository.existsById(userId)) {
                return ResponseEntity
                        .badRequest()
                        .body(new MessageResponseDto("Error: User does not exist!"));
            }
        }



        for (Long userId : userIds) {
            UserTripId userTripId = UserTripId.builder()
                            .tripId(tripId)
                            .userId(userId)
                            .build();

            Optional<User> user = userRepository.findById(userId);
            Optional<Trip> trip = tripRepository.findById(tripId);

            if(!userTripRepository.existsById(userTripId)) {
                UserTrip userTrip = UserTrip.builder()
                        .id(userTripId)
                        .user(user.get())
                        .trip(trip.get())
                        .build();
                userTripRepository.save(userTrip);
                trip.get().addUserToTrip(user.get(), userTrip);
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

        return new ResponseEntity<>( tripResponseDto, HttpStatus.OK);
    }

}
