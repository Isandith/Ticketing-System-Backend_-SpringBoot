package com.TicketingSystem.Ticketing.Repository;

import com.TicketingSystem.Ticketing.Entities.ConfigEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfigRepo extends JpaRepository<ConfigEntity, Long> {
    // Additional query methods can be defined here if needed
}
