package com.TicketingSystem.Ticketing.Entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "config")
public class ConfigEntity {

    @Id
    private Long id = 1L; // Assuming a single row of config in the DB.

    private int maxTicketCapacity;
    private int totalTicketCapacity;
    private long ticketReleaseRate;
    private long ticketRetrievalRate;

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getMaxTicketCapacity() {
        return maxTicketCapacity;
    }

    public void setMaxTicketCapacity(int maxTicketCapacity) {
        this.maxTicketCapacity = maxTicketCapacity;
    }

    public int getTotalTicketCapacity() {
        return totalTicketCapacity;
    }

    public void setTotalTicketCapacity(int totalTicketCapacity) {
        this.totalTicketCapacity = totalTicketCapacity;
    }

    public long getTicketReleaseRate() {
        return ticketReleaseRate;
    }

    public void setTicketReleaseRate(long ticketReleaseRate) {
        this.ticketReleaseRate = ticketReleaseRate;
    }

    public long getTicketRetrievalRate() {
        return ticketRetrievalRate;
    }

    public void setTicketRetrievalRate(long ticketRetrievalRate) {
        this.ticketRetrievalRate = ticketRetrievalRate;
    }
}
