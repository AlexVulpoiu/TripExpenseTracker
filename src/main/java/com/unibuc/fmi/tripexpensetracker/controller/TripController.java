package com.unibuc.fmi.tripexpensetracker.controller;

import com.unibuc.fmi.tripexpensetracker.dto.GroupSpendingRequestDto;
import com.unibuc.fmi.tripexpensetracker.dto.IndividualSpendingRequestDto;
import com.unibuc.fmi.tripexpensetracker.dto.NewTripUsersDto;
import com.unibuc.fmi.tripexpensetracker.dto.TripRequestDto;
import com.unibuc.fmi.tripexpensetracker.model.User;
import com.unibuc.fmi.tripexpensetracker.security.services.UserDetailsImpl;
import com.unibuc.fmi.tripexpensetracker.service.SpendingService;
import com.unibuc.fmi.tripexpensetracker.service.TripService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;

@RestController
@RequestMapping("/trips")
public class TripController {

    @Autowired
    private final TripService tripService;

    @Autowired
    private final SpendingService spendingService;

    @Autowired
    public TripController(TripService tripService, SpendingService spendingService) {
        this.tripService = tripService;
        this.spendingService = spendingService;
    }

    @PostMapping("/add")
    public ResponseEntity<?> addTrip(Principal principal, @Valid @RequestBody TripRequestDto tripRequestDto) {
        return tripService.addTrip(((UserDetailsImpl) ((UsernamePasswordAuthenticationToken)principal).getPrincipal()).getId(), tripRequestDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTrip(@PathVariable Long id) {
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

    @DeleteMapping("/{tripId}/deleteUser/{userId}")
    public ResponseEntity<?> deleteUserFromTrip(@PathVariable Long tripId, @PathVariable Long userId) {
        return tripService.deleteUserFromTrip(tripId, userId);
    }

    @PostMapping("/{tripId}/addIndividualSpending")
    public ResponseEntity<?> addIndividualSpending(@PathVariable Long tripId, Principal principal, @Valid @RequestBody IndividualSpendingRequestDto individualSpendingRequestDto) {

        return spendingService.addIndividualSpending(tripId, User.build((UserDetailsImpl) ((UsernamePasswordAuthenticationToken)principal).getPrincipal()), individualSpendingRequestDto);
    }

    @PostMapping("/{tripId}/addGroupSpending")
    public ResponseEntity<?> addGroupSpending(@PathVariable Long tripId, Principal principal, @Valid @RequestBody GroupSpendingRequestDto groupSpendingRequestDto) {
        return spendingService.addGroupSpending(tripId, User.build((UserDetailsImpl) ((UsernamePasswordAuthenticationToken)principal).getPrincipal()), groupSpendingRequestDto);
    }

    @PatchMapping("/{spendingId}/update")
    public ResponseEntity<?> updateSpending(@PathVariable Long spendingId, Principal principal, @Valid @RequestBody GroupSpendingRequestDto groupSpendingRequestDto) {
        return spendingService.updateSpending(spendingId, User.build((UserDetailsImpl) ((UsernamePasswordAuthenticationToken)principal).getPrincipal()), groupSpendingRequestDto);
    }

    @DeleteMapping("/deleteIndividualSpending/{spendingId}")
    public ResponseEntity<?> deleteIndividualSpending(@PathVariable Long spendingId) {
        return spendingService.deleteIndividualSpending(spendingId);
    }

}
