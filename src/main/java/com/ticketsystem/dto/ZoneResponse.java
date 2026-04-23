package com.ticketsystem.dto;

import com.ticketsystem.domain.Zone;

import java.math.BigDecimal;

public record ZoneResponse(Long id, String name, Integer capacity, BigDecimal pricePerSeat) {

    public static ZoneResponse from(Zone zone) {
        return new ZoneResponse(zone.getId(), zone.getName(), zone.getCapacity(), zone.getPricePerSeat());
    }
}
