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
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
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
//                trip.get().addUserToTrip(user.get(), userTrip);
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

    public ResponseEntity<?> deleteUserFromTrip(@PathVariable Long tripId, @PathVariable Long userId) {
        
        if (!tripRepository.existsById(tripId)) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponseDto("Error: Trip does not exist!"));
        }

        if (!userRepository.existsById(userId)) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponseDto("Error: User does not exist!"));
        }

        UserTripId userTripId = UserTripId.builder()
                .userId(userId)
                .tripId(tripId)
                .build();
        Optional<UserTrip> optionalUserTrip = userTripRepository.getUserTripById(userTripId);

        if (optionalUserTrip.isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponseDto("Error: The user is not in this trip!"));
        }

        userTripRepository.deleteById(optionalUserTrip.get().getId());


        return ResponseEntity.ok(new MessageResponseDto("User deleted from trip successfully!"));
    }


    public ResponseEntity<?> addIndividualSpending(@PathVariable Long tripId, @PathVariable Long userId, @Valid @RequestBody IndividualSpendingRequestDto individualSpendingRequestDto) {

        UserTripId userTripId = UserTripId.builder()
                .tripId(tripId)
                .userId(userId)
                .build();

        Optional<User> user = userRepository.findById(userId);
        Optional<Trip> trip = tripRepository.findById(tripId);

        UserTrip userTrip = UserTrip.builder()
                .id(userTripId)
                .user(user.get())
                .trip(trip.get())
                .build();

        Spending spending = Spending.builder()
                .userTrip(userTrip)
                .amount(individualSpendingRequestDto.getAmount())
                .type(individualSpendingRequestDto.getType())
                .build();

        spendingRepository.save(spending);
        return ResponseEntity.ok(new MessageResponseDto("Individual spending for user added to trip!"));
    }

}
