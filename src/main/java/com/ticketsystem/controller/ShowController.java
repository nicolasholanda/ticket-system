package com.ticketsystem.controller;

import com.ticketsystem.dto.SeatResponse;
import com.ticketsystem.dto.ShowResponse;
import com.ticketsystem.service.ShowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/shows")
@RequiredArgsConstructor
public class ShowController {

    private final ShowService showService;

    @GetMapping
    public ResponseEntity<List<ShowResponse>> listShows() {
        List<ShowResponse> shows = showService.findAll().stream()
                .map(ShowResponse::from)
                .toList();
        return ResponseEntity.ok(shows);
    }

    @GetMapping("/{id}/seats")
    public ResponseEntity<List<SeatResponse>> listAvailableSeats(@PathVariable Long id) {
        List<SeatResponse> seats = showService.findAvailableSeatsByZone(id).entrySet().stream()
                .flatMap(entry -> entry.getValue().stream().map(SeatResponse::from))
                .toList();
        return ResponseEntity.ok(seats);
    }
}
