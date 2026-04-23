package com.ticketsystem.dto;

import com.ticketsystem.domain.Seat;
import com.ticketsystem.domain.SeatStatus;

public record SeatResponse(Long id, Integer number, SeatStatus status, Long zoneId, String zoneName) {

    public static SeatResponse from(Seat seat) {
        return new SeatResponse(seat.getId(), seat.getNumber(), seat.getStatus(),
                seat.getZone().getId(), seat.getZone().getName());
    }
}
