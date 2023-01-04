package com.unibuc.fmi.tripexpensetracker.controller;

import com.unibuc.fmi.tripexpensetracker.repository.TripRepository;
import com.unibuc.fmi.tripexpensetracker.dto.MessageResponseDto;
import com.unibuc.fmi.tripexpensetracker.dto.SignupRequestDto;
import com.unibuc.fmi.tripexpensetracker.dto.TripRequestDto;
import com.unibuc.fmi.tripexpensetracker.model.Trip;
import com.unibuc.fmi.tripexpensetracker.model.User;
import com.unibuc.fmi.tripexpensetracker.repository.UserRepository;
import com.unibuc.fmi.tripexpensetracker.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/trips")
public class TripController {

    @Autowired
    TripRepository tripRepository;

    @PostMapping("/add")
    public ResponseEntity<?> addTrip(@Valid @RequestBody TripRequestDto tripRequestDto) {

        // Create new trip's account
        Trip trip = Trip.builder()
                .title(tripRequestDto.getTitle())
                .location(tripRequestDto.getLocation())
                .group_expense(tripRequestDto.getGroup_expense())
                .build();
        tripRepository.save(trip);

        return ResponseEntity.ok(new MessageResponseDto("Trip added successfully!"));
    }
}
