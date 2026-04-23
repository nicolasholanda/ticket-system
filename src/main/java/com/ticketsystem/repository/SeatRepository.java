package com.ticketsystem.repository;

import com.ticketsystem.domain.Seat;
import com.ticketsystem.domain.SeatStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, Long> {

    List<Seat> findByZoneIdAndStatus(Long zoneId, SeatStatus status);

    List<Seat> findByIdInAndStatus(List<Long> ids, SeatStatus status);
}
