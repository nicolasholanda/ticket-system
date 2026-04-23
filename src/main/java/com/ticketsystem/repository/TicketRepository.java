package com.ticketsystem.repository;

import com.ticketsystem.domain.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    long countByShowIdAndSeatZoneId(Long showId, Long zoneId);
}
