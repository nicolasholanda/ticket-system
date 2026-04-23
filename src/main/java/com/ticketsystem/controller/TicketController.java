package com.ticketsystem.controller;

import com.ticketsystem.dto.PurchaseRequest;
import com.ticketsystem.dto.TicketResponse;
import com.ticketsystem.service.TicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    @PostMapping("/purchase")
    public ResponseEntity<List<TicketResponse>> purchase(@Valid @RequestBody PurchaseRequest request) {
        List<TicketResponse> tickets = ticketService
                .purchase(request.getShowId(), request.getZoneId(), request.getSeatIds(), request.getBuyerEmail())
                .stream()
                .map(TicketResponse::from)
                .toList();
        return ResponseEntity.ok(tickets);
    }
}
