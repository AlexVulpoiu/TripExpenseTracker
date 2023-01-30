package com.unibuc.fmi.tripexpensetracker.repository;

import com.unibuc.fmi.tripexpensetracker.model.SpendingGroup;
import com.unibuc.fmi.tripexpensetracker.model.SpendingGroupId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpendingGroupRepository extends JpaRepository<SpendingGroup, SpendingGroupId> {
}

