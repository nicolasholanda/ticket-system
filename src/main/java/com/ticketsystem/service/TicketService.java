package com.ticketsystem.service;

import com.ticketsystem.domain.Seat;
import com.ticketsystem.domain.SeatStatus;
import com.ticketsystem.domain.Show;
import com.ticketsystem.domain.Ticket;
import com.ticketsystem.domain.Zone;
import com.ticketsystem.exception.CapacityExceededException;
import com.ticketsystem.exception.SeatNotAvailableException;
import com.ticketsystem.repository.SeatRepository;
import com.ticketsystem.repository.ShowRepository;
import com.ticketsystem.repository.TicketRepository;
import com.ticketsystem.repository.ZoneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final ShowRepository showRepository;
    private final ZoneRepository zoneRepository;
    private final SeatRepository seatRepository;
    private final TicketRepository ticketRepository;

    @Transactional
    public List<Ticket> purchase(Long showId, Long zoneId, List<Long> seatIds, String buyerEmail) {
        Show show = showRepository.findById(showId)
                .orElseThrow(() -> new NoSuchElementException("Show not found: " + showId));

        Zone zone = zoneRepository.findById(zoneId)
                .orElseThrow(() -> new NoSuchElementException("Zone not found: " + zoneId));

        if (!zone.getVenue().getId().equals(show.getVenue().getId())) {
            throw new IllegalArgumentException("Zone does not belong to the show's venue");
        }

        long soldCount = ticketRepository.countByShowIdAndSeatZoneId(showId, zoneId);
        if (soldCount + seatIds.size() > zone.getCapacity()) {
            throw new CapacityExceededException("Purchase exceeds zone capacity for zone: " + zone.getName());
        }

        List<Seat> availableSeats = seatRepository.findByIdInAndStatus(seatIds, SeatStatus.AVAILABLE);
        if (availableSeats.size() != seatIds.size()) {
            throw new SeatNotAvailableException("One or more selected seats are not available");
        }

        for (Seat seat : availableSeats) {
            if (!seat.getZone().getId().equals(zoneId)) {
                throw new IllegalArgumentException("Seat " + seat.getId() + " does not belong to zone: " + zoneId);
            }
        }

        LocalDateTime now = LocalDateTime.now();
        List<Ticket> tickets = availableSeats.stream().map(seat -> {
            seat.setStatus(SeatStatus.SOLD);
            Ticket ticket = new Ticket();
            ticket.setShow(show);
            ticket.setSeat(seat);
            ticket.setPrice(zone.getPricePerSeat());
            ticket.setBuyerEmail(buyerEmail);
            ticket.setPurchasedAt(now);
            return ticket;
        }).toList();

        seatRepository.saveAll(availableSeats);
        return ticketRepository.saveAll(tickets);
    }
}
