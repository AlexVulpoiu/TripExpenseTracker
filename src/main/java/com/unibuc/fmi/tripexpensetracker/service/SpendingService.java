package com.unibuc.fmi.tripexpensetracker.service;

import com.unibuc.fmi.tripexpensetracker.dto.*;
import com.unibuc.fmi.tripexpensetracker.model.*;
import com.unibuc.fmi.tripexpensetracker.notification.notifications.AddedToSpendingNotification;
import com.unibuc.fmi.tripexpensetracker.notification.notifications.RemovedFromSpendingNotification;
import com.unibuc.fmi.tripexpensetracker.notification.notifications.SpendingUpdatedNotification;
import com.unibuc.fmi.tripexpensetracker.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SpendingService {
    private final TripRepository tripRepository;

    private final UserRepository userRepository;

    private final UserTripRepository userTripRepository;

    private final SpendingRepository spendingRepository;

    private final SpendingGroupRepository spendingGroupRepository;

    @Autowired
    public SpendingService(TripRepository tripRepository,
                           UserRepository userRepository,
                           UserTripRepository userTripRepository,
                           SpendingRepository spendingRepository,
                           SpendingGroupRepository spendingGroupRepository) {
        this.tripRepository = tripRepository;
        this.userRepository = userRepository;
        this.userTripRepository = userTripRepository;
        this.spendingRepository = spendingRepository;
        this.spendingGroupRepository = spendingGroupRepository;
    }

    public ResponseEntity<?> addIndividualSpending(Long tripId, User user, IndividualSpendingRequestDto individualSpendingRequestDto) {

        Optional<Trip> trip = tripRepository.findById(tripId);
        Optional<UserTrip> userTrip = trip.isEmpty() ? Optional.empty() : userTripRepository.findByUserAndTrip(user, trip.get());

        if (trip.isEmpty() || userTrip.isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponseDto("Error: Trip does not exist or you can't access it!"));
        }

        Spending spending = Spending.builder()
                .userTrip(userTrip.get())
                .amount(individualSpendingRequestDto.getAmount())
                .type(Spending.TYPE_INDIVIDUAL)
                .build();

        spending = spendingRepository.save(spending);

        SpendingGroupId spendingGroupId = SpendingGroupId.builder()
                .userId(user.getId())
                .spendingId(spending.getId())
                .build();
        SpendingGroup spendingGroup = SpendingGroup.builder()
                .id(spendingGroupId)
                .spending(spending)
                .user(user)
                .build();
        spendingGroupRepository.save(spendingGroup);

        return ResponseEntity.ok(new MessageResponseDto("Individual spending for user added to trip!"));
    }

    public ResponseEntity<?> deleteIndividualSpending(Long spendingId) {

        if (!spendingRepository.existsById(spendingId)) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponseDto("Error: Spending does not exist!"));
        }

        spendingRepository.deleteById(spendingId);
        return ResponseEntity.ok(new MessageResponseDto("Spending deleted from trip successfully!"));
    }

    private void syncSpendingGroup(Spending spending, List<User> users) {
        List<User> participants = spending.getParticipants().stream().map(SpendingGroup::getUser).collect(Collectors.toList());

        Set<User> previous = new HashSet<>(participants);
        Set<User> current = new HashSet<>(users);
        previous.removeAll(current);

        for (User removedUser : previous) {
            new RemovedFromSpendingNotification(
                    spending.getUserTrip().getUser(),
                    removedUser,
                    spending
            );
        }

        previous = new HashSet<>(participants);
        current.removeAll(previous);

        for (User addedUser : current) {
            new AddedToSpendingNotification(
                    spending.getUserTrip().getUser(),
                    addedUser,
                    spending
            ).dispatch();
        }

        current = new HashSet<>(users);
        current.retainAll(previous);

        for (User user : current) {
            new SpendingUpdatedNotification(
                    spending.getUserTrip().getUser(),
                    user,
                    spending
            );
        }

        if (spending.getParticipants() != null) {
            spendingGroupRepository.deleteAllInBatch(spending.getParticipants());
        }

        for (User userInGroup : users) {
            SpendingGroupId spendingGroupId = SpendingGroupId.builder()
                    .spendingId(spending.getId())
                    .userId(userInGroup.getId())
                    .build();

            SpendingGroup spendingGroup = SpendingGroup.builder()
                    .id(spendingGroupId)
                    .spending(spending)
                    .user(userInGroup)
                    .build();

            spendingGroupRepository.save(spendingGroup);
        }
    }

    public ResponseEntity<?> addGroupSpending(Long tripId, User user, GroupSpendingRequestDto groupSpendingRequestDto) {

        Optional<Trip> trip = tripRepository.findById(tripId);
        Optional<UserTrip> userTrip = trip.isEmpty() ? Optional.empty() : userTripRepository.findByUserAndTrip(user, trip.get());

        if (trip.isEmpty() || userTrip.isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponseDto("Error: Trip does not exist or you can't access it!"));
        }

        List<Long> userIds = groupSpendingRequestDto.getUsers();

        if (userIds.size() == 0) {
            return this.addIndividualSpending(
                    tripId,
                    user,
                    IndividualSpendingRequestDto.builder()
                            .amount(groupSpendingRequestDto.getAmount())
                            .build()
            );
        }

        userIds.add(user.getId());
        List<User> users = userRepository.findByIdIn(userIds);

        if (users.size() != userIds.size()) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponseDto("Error: Some users selected do not exist!"));
        }

        Spending spending = Spending.builder()
                .userTrip(userTrip.get())
                .amount(groupSpendingRequestDto.getAmount())
                .type(Spending.TYPE_GROUP)
                .build();

        spending = spendingRepository.save(spending);
        List<SpendingGroup> spendingGroupList = new ArrayList<>();

        for (User user1 : users) {
            SpendingGroupId spendingGroupId = SpendingGroupId.builder()
                    .userId(user1.getId())
                    .spendingId(spending.getId())
                    .build();
            SpendingGroup spendingGroup = SpendingGroup.builder()
                    .id(spendingGroupId)
                    .user(user1)
                    .spending(spending)
                    .build();
            spendingGroupRepository.save(spendingGroup);
            spendingGroupList.add(spendingGroup);
        }

        spending.setParticipants(spendingGroupList);
        spendingRepository.save(spending);

        return ResponseEntity.ok(new MessageResponseDto("Group spending added to trip!"));
    }

    public ResponseEntity<?> updateSpending(Long spendingId, User user, GroupSpendingRequestDto groupSpendingRequestDto) {
        Optional<Spending> spendingProxy = spendingRepository.findById(spendingId);

        if (spendingProxy.isEmpty() || !Objects.equals(spendingProxy.get().getUserTrip().getUser().getId(), user.getId())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponseDto("Error: Spending does not exist or you can't access it!"));
        }

        String type = groupSpendingRequestDto.getUsers().size() == 0 ? Spending.TYPE_INDIVIDUAL : Spending.TYPE_GROUP;
        Spending spending = spendingProxy.get();
        spending.setType(type);
        spending.setAmount(groupSpendingRequestDto.getAmount());

        List<Long> userIds = groupSpendingRequestDto.getUsers();
        List<User> users = userRepository.findByIdIn(userIds);

        if (users.size() != userIds.size()) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponseDto("Error: Some users selected do not exist!"));
        }

        this.syncSpendingGroup(spending, users);

        return ResponseEntity.ok(new MessageResponseDto("Spending was successfully updated!"));
    }
}
