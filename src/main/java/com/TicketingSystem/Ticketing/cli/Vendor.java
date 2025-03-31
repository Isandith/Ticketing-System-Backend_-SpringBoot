package com.TicketingSystem.Ticketing.cli;

public class Vendor implements Runnable {
    private final TicketPool ticketPool;
    private final int TotalTickets;
    private final long releaseRate;

    public Vendor(TicketPool ticketPool, int releaseCapacity, long releaseRate) {
        this.ticketPool = ticketPool;
        this.TotalTickets = releaseCapacity;
        this.releaseRate = releaseRate;
    }

    @Override
    public void run() {
        while (ticketPool.isVendorActive() && !Thread.currentThread().isInterrupted()) {
            ticketPool.addTickets(TotalTickets);
            try {
                Thread.sleep(releaseRate);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}
