package com.ticketsystem.repository;

import com.ticketsystem.domain.Zone;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ZoneRepository extends JpaRepository<Zone, Long> {

    List<Zone> findByVenueId(Long venueId);
}
