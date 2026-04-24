package com.ticketsystem.service;

import com.ticketsystem.domain.Seat;
import com.ticketsystem.domain.SeatStatus;
import com.ticketsystem.domain.Show;
import com.ticketsystem.domain.Ticket;
import com.ticketsystem.domain.Venue;
import com.ticketsystem.domain.Zone;
import com.ticketsystem.exception.CapacityExceededException;
import com.ticketsystem.exception.SeatNotAvailableException;
import com.ticketsystem.repository.SeatRepository;
import com.ticketsystem.repository.ShowRepository;
import com.ticketsystem.repository.TicketRepository;
import com.ticketsystem.repository.ZoneRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TicketServiceTest {

    @Mock
    private ShowRepository showRepository;

    @Mock
    private ZoneRepository zoneRepository;

    @Mock
    private SeatRepository seatRepository;

    @Mock
    private TicketRepository ticketRepository;

    @InjectMocks
    private TicketService ticketService;

    private Venue venue;
    private Show show;
    private Zone zone;
    private Seat seat1;
    private Seat seat2;

    @BeforeEach
    void setUp() {
        venue = new Venue();
        venue.setId(1L);
        venue.setName("Main Arena");

        show = new Show();
        show.setId(1L);
        show.setName("Rock Concert");
        show.setVenue(venue);

        zone = new Zone();
        zone.setId(1L);
        zone.setName("VIP");
        zone.setCapacity(100);
        zone.setPricePerSeat(new BigDecimal("150.00"));
        zone.setVenue(venue);

        seat1 = new Seat();
        seat1.setId(1L);
        seat1.setNumber(1);
        seat1.setStatus(SeatStatus.AVAILABLE);
        seat1.setZone(zone);

        seat2 = new Seat();
        seat2.setId(2L);
        seat2.setNumber(2);
        seat2.setStatus(SeatStatus.AVAILABLE);
        seat2.setZone(zone);
    }

    @Test
    void purchase_happyPath_returnsTickets() {
        List<Long> seatIds = List.of(1L, 2L);
        List<Seat> availableSeats = List.of(seat1, seat2);

        when(showRepository.findById(1L)).thenReturn(Optional.of(show));
        when(zoneRepository.findById(1L)).thenReturn(Optional.of(zone));
        when(ticketRepository.countByShowIdAndSeatZoneId(1L, 1L)).thenReturn(0L);
        when(seatRepository.findByIdInAndStatus(seatIds, SeatStatus.AVAILABLE)).thenReturn(availableSeats);
        when(ticketRepository.saveAll(anyList())).thenAnswer(inv -> inv.getArgument(0));

        List<Ticket> tickets = ticketService.purchase(1L, 1L, seatIds, "buyer@email.com");

        assertThat(tickets).hasSize(2);
        assertThat(tickets).allMatch(t -> t.getBuyerEmail().equals("buyer@email.com"));
        assertThat(tickets).allMatch(t -> t.getPrice().equals(new BigDecimal("150.00")));
        assertThat(seat1.getStatus()).isEqualTo(SeatStatus.SOLD);
        assertThat(seat2.getStatus()).isEqualTo(SeatStatus.SOLD);
        verify(seatRepository).saveAll(availableSeats);
    }

    @Test
    void purchase_showNotFound_throwsNoSuchElementException() {
        when(showRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> ticketService.purchase(99L, 1L, List.of(1L), "buyer@email.com"))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("Show not found");
    }

    @Test
    void purchase_zoneNotFound_throwsNoSuchElementException() {
        when(showRepository.findById(1L)).thenReturn(Optional.of(show));
        when(zoneRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> ticketService.purchase(1L, 99L, List.of(1L), "buyer@email.com"))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("Zone not found");
    }

    @Test
    void purchase_capacityExceeded_throwsCapacityExceededException() {
        zone.setCapacity(2);
        List<Long> seatIds = List.of(1L, 2L);

        when(showRepository.findById(1L)).thenReturn(Optional.of(show));
        when(zoneRepository.findById(1L)).thenReturn(Optional.of(zone));
        when(ticketRepository.countByShowIdAndSeatZoneId(1L, 1L)).thenReturn(1L);

        assertThatThrownBy(() -> ticketService.purchase(1L, 1L, seatIds, "buyer@email.com"))
                .isInstanceOf(CapacityExceededException.class)
                .hasMessageContaining("VIP");
    }

    @Test
    void purchase_seatAlreadySold_throwsSeatNotAvailableException() {
        List<Long> seatIds = List.of(1L, 2L);

        when(showRepository.findById(1L)).thenReturn(Optional.of(show));
        when(zoneRepository.findById(1L)).thenReturn(Optional.of(zone));
        when(ticketRepository.countByShowIdAndSeatZoneId(1L, 1L)).thenReturn(0L);
        when(seatRepository.findByIdInAndStatus(seatIds, SeatStatus.AVAILABLE)).thenReturn(List.of(seat1));

        assertThatThrownBy(() -> ticketService.purchase(1L, 1L, seatIds, "buyer@email.com"))
                .isInstanceOf(SeatNotAvailableException.class)
                .hasMessageContaining("not available");
    }

    @Test
    void purchase_seatBelongsToDifferentZone_throwsIllegalArgumentException() {
        Zone otherZone = new Zone();
        otherZone.setId(99L);
        otherZone.setName("General");
        otherZone.setVenue(venue);

        seat1.setZone(otherZone);
        List<Long> seatIds = List.of(1L);

        when(showRepository.findById(1L)).thenReturn(Optional.of(show));
        when(zoneRepository.findById(1L)).thenReturn(Optional.of(zone));
        when(ticketRepository.countByShowIdAndSeatZoneId(1L, 1L)).thenReturn(0L);
        when(seatRepository.findByIdInAndStatus(seatIds, SeatStatus.AVAILABLE)).thenReturn(List.of(seat1));

        assertThatThrownBy(() -> ticketService.purchase(1L, 1L, seatIds, "buyer@email.com"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("does not belong to zone");
    }
}
