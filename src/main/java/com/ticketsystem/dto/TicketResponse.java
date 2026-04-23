package com.ticketsystem.dto;

import com.ticketsystem.domain.Ticket;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TicketResponse(Long id, Long showId, String showName, Integer seatNumber,
                              String zoneName, BigDecimal price, String buyerEmail, LocalDateTime purchasedAt) {

    public static TicketResponse from(Ticket ticket) {
        return new TicketResponse(
                ticket.getId(),
                ticket.getShow().getId(),
                ticket.getShow().getName(),
                ticket.getSeat().getNumber(),
                ticket.getSeat().getZone().getName(),
                ticket.getPrice(),
                ticket.getBuyerEmail(),
                ticket.getPurchasedAt()
        );
    }
}
