package com.ticketsystem.dto;

import com.ticketsystem.domain.Show;

import java.time.LocalDateTime;

public record ShowResponse(Long id, String name, LocalDateTime date, String venueName) {

    public static ShowResponse from(Show show) {
        return new ShowResponse(show.getId(), show.getName(), show.getDate(), show.getVenue().getName());
    }
}
