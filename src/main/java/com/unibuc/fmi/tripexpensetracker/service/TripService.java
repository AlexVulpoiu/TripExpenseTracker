package com.unibuc.fmi.tripexpensetracker.service;

import com.unibuc.fmi.tripexpensetracker.dto.MessageResponseDto;
import com.unibuc.fmi.tripexpensetracker.dto.TripRequestDto;
import com.unibuc.fmi.tripexpensetracker.dto.UserDto;
import com.unibuc.fmi.tripexpensetracker.model.Trip;
import com.unibuc.fmi.tripexpensetracker.model.User;
import com.unibuc.fmi.tripexpensetracker.repository.TripRepository;
import com.unibuc.fmi.tripexpensetracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TripService {
    private final TripRepository tripRepository;

    @Autowired
    public TripService(TripRepository tripRepository) {
        this.tripRepository = tripRepository;
    }

    public ResponseEntity<?> addTrip(TripRequestDto tripRequestDto) {

        Trip trip = Trip.builder()
                .title(tripRequestDto.getTitle())
                .location(tripRequestDto.getLocation())
                .group_expense(tripRequestDto.getGroup_expense())
                .build();
        tripRepository.save(trip);

        return ResponseEntity.ok(new MessageResponseDto("Trip added successfully!"));
    }
}
