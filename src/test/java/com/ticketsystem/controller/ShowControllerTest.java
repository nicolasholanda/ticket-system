package com.ticketsystem.controller;

import com.ticketsystem.domain.Seat;
import com.ticketsystem.domain.SeatStatus;
import com.ticketsystem.domain.Show;
import com.ticketsystem.domain.Venue;
import com.ticketsystem.domain.Zone;
import com.ticketsystem.service.ShowService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ShowController.class)
class ShowControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ShowService showService;

    private Venue venue;
    private Show show;
    private Zone zone;
    private Seat seat;

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
        seat.setStatus(SeatStatus.AVAILABLE);
        seat.setZone(zone);
    }

    @Test
    void listShows_returnsAllShows() throws Exception {
        when(showService.findAll()).thenReturn(List.of(show));

        mockMvc.perform(get("/shows"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Rock Concert"))
                .andExpect(jsonPath("$[0].venueName").value("Main Arena"));
    }

    @Test
    void listShows_noShows_returnsEmptyList() throws Exception {
        when(showService.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/shows"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void listAvailableSeats_returnsSeats() throws Exception {
        Map<Zone, List<Seat>> seatsByZone = new LinkedHashMap<>();
        seatsByZone.put(zone, List.of(seat));

        when(showService.findAvailableSeatsByZone(1L)).thenReturn(seatsByZone);

        mockMvc.perform(get("/shows/1/seats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].number").value(1))
                .andExpect(jsonPath("$[0].status").value("AVAILABLE"))
                .andExpect(jsonPath("$[0].zoneName").value("VIP"));
    }

    @Test
    void listAvailableSeats_showNotFound_returns404() throws Exception {
        when(showService.findAvailableSeatsByZone(99L)).thenThrow(new NoSuchElementException("Show not found: 99"));

        mockMvc.perform(get("/shows/99/seats"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Show not found: 99"));
    }
}
