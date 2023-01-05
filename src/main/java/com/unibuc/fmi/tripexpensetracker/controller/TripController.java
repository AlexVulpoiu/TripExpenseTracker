package com.unibuc.fmi.tripexpensetracker.controller;

import com.unibuc.fmi.tripexpensetracker.dto.NewTripUsersDto;
import com.unibuc.fmi.tripexpensetracker.dto.TripRequestDto;
import com.unibuc.fmi.tripexpensetracker.service.TripService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTrip(@PathVariable Long id){
        return tripService.deleteTrip(id);
    }

    @PostMapping("/{tripId}/addUsers")
    public ResponseEntity<?> addUsersToTrip(@PathVariable Long tripId, @Valid @RequestBody NewTripUsersDto newTripUsersDto) {
        return tripService.addUsersToTrip(tripId, newTripUsersDto);
    }

    @GetMapping("/{tripId}")
    public ResponseEntity<?> getTripDetails(@PathVariable Long tripId) {
        return tripService.getTripDetails(tripId);
    }
}
