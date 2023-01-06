package com.unibuc.fmi.tripexpensetracker.repository;

import com.unibuc.fmi.tripexpensetracker.model.Spending;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SpendingRepository extends JpaRepository<Spending, Long> {

    Optional<Spending> findById(Long id);
}

