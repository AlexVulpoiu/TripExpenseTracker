package com.unibuc.fmi.tripexpensetracker.controller;

import com.unibuc.fmi.tripexpensetracker.repository.TripRepository;
import com.unibuc.fmi.tripexpensetracker.dto.MessageResponseDto;
import com.unibuc.fmi.tripexpensetracker.dto.SignupRequestDto;
import com.unibuc.fmi.tripexpensetracker.dto.TripRequestDto;
import com.unibuc.fmi.tripexpensetracker.model.Trip;
import com.unibuc.fmi.tripexpensetracker.model.User;
import com.unibuc.fmi.tripexpensetracker.repository.UserRepository;
import com.unibuc.fmi.tripexpensetracker.service.TripService;
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
    private final TripService tripService;

    @Autowired
    public TripController(TripService tripService) {
        this.tripService = tripService;
    }

    @PostMapping("/add")
    public ResponseEntity<?> addTrip(@Valid @RequestBody TripRequestDto tripRequestDto) {
        return tripService.addTrip(tripRequestDto);
    }

    @DeleteMapping("/trips/{id}")
    public ResponseEntity<?> deleteTrip(@PathVariable Long id){
//        tripService.deleteById(2L);
        return ResponseEntity.ok().build();
    }
}
