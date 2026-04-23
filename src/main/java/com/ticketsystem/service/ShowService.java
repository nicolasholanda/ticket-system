package com.ticketsystem.service;

import com.ticketsystem.domain.Seat;
import com.ticketsystem.domain.SeatStatus;
import com.ticketsystem.domain.Show;
import com.ticketsystem.domain.Zone;
import com.ticketsystem.repository.SeatRepository;
import com.ticketsystem.repository.ShowRepository;
import com.ticketsystem.repository.ZoneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class ShowService {

    private final ShowRepository showRepository;
    private final ZoneRepository zoneRepository;
    private final SeatRepository seatRepository;

    @Transactional(readOnly = true)
    public List<Show> findAll() {
        return showRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Show findById(Long id) {
        return showRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Show not found: " + id));
    }

    @Transactional(readOnly = true)
    public Map<Zone, List<Seat>> findAvailableSeatsByZone(Long showId) {
        Show show = findById(showId);
        List<Zone> zones = zoneRepository.findByVenueId(show.getVenue().getId());

        Map<Zone, List<Seat>> result = new LinkedHashMap<>();
        for (Zone zone : zones) {
            List<Seat> available = seatRepository.findByZoneIdAndStatus(zone.getId(), SeatStatus.AVAILABLE);
            result.put(zone, available);
        }
        return result;
    }
}
