package com.TicketingSystem.Ticketing.cli;

import java.util.logging.Level;
import java.util.logging.Logger;
import com.TicketingSystem.Ticketing.util.Message;
import org.springframework.messaging.simp.SimpMessagingTemplate;

public class TicketPool {
    private int availableTickets = 0;
    private final int maxCapacity;
    private boolean vendorActive = true;
    private int totalTicketsReleased = 0;
    private int totalTicketsSold = 0;
    private boolean allTicketsSold = false;
    public final Logger LOGGER = Logger.getLogger(TicketPool.class.getName());
    private SimpMessagingTemplate template;
    private boolean running = true;  // Flag to indicate whether the system is running

    public TicketPool(int maxCapacity, SimpMessagingTemplate template) {
        this.maxCapacity = maxCapacity;
        this.template = template;
    }

    public synchronized void addTickets(int number) {
        if (!running || !vendorActive || totalTicketsReleased >= maxCapacity) {
            vendorActive = false;  // Deactivate vendor if already at capacity or system is stopped
            return;
        }

        // Wait until all tickets are sold, then vendor can release more if there is space
        while (availableTickets > 0 && totalTicketsReleased < maxCapacity && vendorActive && running) {
            LOGGER.info("Vendor is waiting for tickets to be sold...");
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }

        // Check if we have reached the maximum ticket capacity
        if (totalTicketsReleased >= maxCapacity) {
            vendorActive = false;
            // Notify customers that no more tickets will be released
            template.convertAndSend("/topic/messages", new Message("Vendor", "No more tickets will be released."));
            notifyAll();
            return;
        }

        int ticketsRemaining = maxCapacity - totalTicketsReleased;
        int ticketsToRelease = Math.min(number, ticketsRemaining);
        availableTickets += ticketsToRelease;
        totalTicketsReleased += ticketsToRelease;
        allTicketsSold = false;  // There are still tickets available

        LOGGER.info("Vendor released " + ticketsToRelease + " tickets. Available tickets: " + availableTickets);
        // Send structured message with sender and content
        template.convertAndSend("/topic/messages", new Message("Vendor", "added " + ticketsToRelease + " tickets"));
        notifyAll();
    }

    public synchronized boolean getTickets(int number, String customerName) {
        while (availableTickets < number && vendorActive && running) {
            // If all tickets are sold out, don't allow customers to wait
            if (availableTickets == 0 && totalTicketsReleased >= maxCapacity) {
                LOGGER.log(Level.WARNING, customerName + " cannot buy tickets. No tickets available.");
                // Notify customers that no tickets are available
                template.convertAndSend("/topic/messages", new Message("Customer", customerName + " cannot buy tickets. No tickets available."));
                return false;
            }

            LOGGER.info(customerName + " waiting for more tickets. Available: " + availableTickets);
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }

        int ticketsToBuy = Math.min(number, availableTickets);  // Buy whatever is lesser, the request or what's available
        availableTickets -= ticketsToBuy;
        totalTicketsSold += ticketsToBuy;
        LOGGER.info(customerName + " bought " + ticketsToBuy + " tickets. Available tickets: " + availableTickets);
        // Send structured message with sender and content
        template.convertAndSend("/topic/messages", new Message("Customer", customerName + " booked " + ticketsToBuy + " tickets"));

        if (availableTickets == 0) {
            allTicketsSold = true;
            LOGGER.info("All tickets sold, notifying vendor.");
            notifyAll();  // Notify vendor that all tickets have been sold
        }

        return true;
    }

    public synchronized boolean isVendorActive() {
        return vendorActive;
    }

    public synchronized int getAvailableTickets() {
        return availableTickets;
    }

    public synchronized int getTotalTicketsSold() {
        return totalTicketsSold;
    }

    // Method to stop the system, disable the vendor and interrupt waiting customers
    public synchronized void stop() {
        running = false;  // Stop the system
        vendorActive = false;  // Deactivate the vendor
        notifyAll();  // Notify all threads (both vendor and customers) to stop
        LOGGER.info("TicketPool has been stopped.");
        // Notify all users that the system has been stopped
        template.convertAndSend("/topic/messages", new Message("System", "Ticketing system has been stopped.Customers can by left tickets."));
    }
}
