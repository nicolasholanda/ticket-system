package com.ticketsystem.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ticketsystem.domain.Seat;
import com.ticketsystem.domain.SeatStatus;
import com.ticketsystem.domain.Show;
import com.ticketsystem.domain.Ticket;
import com.ticketsystem.domain.Venue;
import com.ticketsystem.domain.Zone;
import com.ticketsystem.dto.PurchaseRequest;
import com.ticketsystem.exception.CapacityExceededException;
import com.ticketsystem.exception.SeatNotAvailableException;
import com.ticketsystem.service.TicketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TicketController.class)
class TicketControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TicketService ticketService;

    private Venue venue;
    private Show show;
    private Zone zone;
    private Seat seat;
    private Ticket ticket;
    private PurchaseRequest validRequest;

    @BeforeEach
    void setUp() {
        venue = new Venue();
        venue.setId(1L);
        venue.setName("Main Arena");

        show = new Show();
        show.setId(1L);
        show.setName("Rock Concert");
        show.setDate(LocalDateTime.of(2026, 6, 15, 20, 0));
        show.setVenue(venue);

        zone = new Zone();
        zone.setId(1L);
        zone.setName("VIP");
        zone.setCapacity(100);
        zone.setPricePerSeat(new BigDecimal("150.00"));
        zone.setVenue(venue);

        seat = new Seat();
        seat.setId(1L);
        seat.setNumber(1);
        seat.setStatus(SeatStatus.SOLD);
        seat.setZone(zone);

        ticket = new Ticket();
        ticket.setId(1L);
        ticket.setShow(show);
        ticket.setSeat(seat);
        ticket.setPrice(new BigDecimal("150.00"));
        ticket.setBuyerEmail("buyer@email.com");
        ticket.setPurchasedAt(LocalDateTime.now());

        validRequest = new PurchaseRequest();
        validRequest.setShowId(1L);
        validRequest.setZoneId(1L);
        validRequest.setSeatIds(List.of(1L));
        validRequest.setBuyerEmail("buyer@email.com");
    }

    @Test
    void purchase_validRequest_returnsTickets() throws Exception {
        when(ticketService.purchase(1L, 1L, List.of(1L), "buyer@email.com")).thenReturn(List.of(ticket));

        mockMvc.perform(post("/tickets/purchase")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].showName").value("Rock Concert"))
                .andExpect(jsonPath("$[0].seatNumber").value(1))
                .andExpect(jsonPath("$[0].zoneName").value("VIP"))
                .andExpect(jsonPath("$[0].price").value(150.00))
                .andExpect(jsonPath("$[0].buyerEmail").value("buyer@email.com"));
    }

    @Test
    void purchase_invalidEmail_returns400() throws Exception {
        validRequest.setBuyerEmail("not-an-email");

        mockMvc.perform(post("/tickets/purchase")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void purchase_missingSeatIds_returns400() throws Exception {
        validRequest.setSeatIds(List.of());

        mockMvc.perform(post("/tickets/purchase")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void purchase_seatNotAvailable_returns409() throws Exception {
        when(ticketService.purchase(1L, 1L, List.of(1L), "buyer@email.com"))
                .thenThrow(new SeatNotAvailableException("One or more selected seats are not available"));

        mockMvc.perform(post("/tickets/purchase")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("One or more selected seats are not available"));
    }

    @Test
    void purchase_capacityExceeded_returns409() throws Exception {
        when(ticketService.purchase(1L, 1L, List.of(1L), "buyer@email.com"))
                .thenThrow(new CapacityExceededException("Purchase exceeds zone capacity for zone: VIP"));

        mockMvc.perform(post("/tickets/purchase")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("Purchase exceeds zone capacity for zone: VIP"));
    }
}
