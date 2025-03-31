package com.TicketingSystem.Ticketing.Services;

import com.TicketingSystem.Ticketing.cli.Customer;
import com.TicketingSystem.Ticketing.cli.TicketPool;
import com.TicketingSystem.Ticketing.cli.Vendor;
import com.TicketingSystem.Ticketing.Entities.ConfigEntity;
import com.TicketingSystem.Ticketing.Repository.ConfigRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ConfigService {

    private int maxTicketCapacity;
    private int totalTicketCapacity;
    private long ticketReleaseRate;
    private long ticketRetrievalRate;
    private final Logger LOGGER = Logger.getLogger(ConfigService.class.getName());
    private final AtomicBoolean systemRunning = new AtomicBoolean(false);
    private final ConfigRepo configRepo;
    private TicketPool ticketPool;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    // Reference to the main system thread
    private Thread systemThread;

    @Autowired
    public ConfigService(ConfigRepo configRepo) {
        this.configRepo = configRepo;
    }

    public void saveConfig(ConfigEntity newConfig) {
        newConfig.setId(1L);
        configRepo.save(newConfig);
        this.maxTicketCapacity = newConfig.getMaxTicketCapacity();
        this.totalTicketCapacity = newConfig.getTotalTicketCapacity();
        this.ticketReleaseRate = newConfig.getTicketReleaseRate();
        this.ticketRetrievalRate = newConfig.getTicketRetrievalRate();
        LOGGER.log(Level.INFO, "Configuration updated and saved.");
    }

    /**
     * Retrieves the current configuration.
     *
     * @return The current ConfigEntity or null if not found.
     */
    public ConfigEntity getConfig() {
        Optional<ConfigEntity> optionalConfig = configRepo.findById(1L);
        if (optionalConfig.isPresent()) {
            ConfigEntity config = optionalConfig.get();
            // Set the properties just like in saveConfig
            config.setMaxTicketCapacity(this.maxTicketCapacity);
            config.setTotalTicketCapacity(this.totalTicketCapacity);
            config.setTicketReleaseRate(this.ticketReleaseRate);
            config.setTicketRetrievalRate(this.ticketRetrievalRate);
            return config;
        }
        return null;
    }

    /**
     * Starts the ticketing system asynchronously.
     * If the system is already running, an exception is thrown.
     */
    public void startSystem() {
        if (systemRunning.compareAndSet(false, true)) {
            LOGGER.log(Level.INFO, "Starting the ticketing system.");

            // Initialize the system thread
            systemThread = new Thread(() -> {
                try {
                    setupThreads();
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Error while starting the system: ", e);
                    systemRunning.set(false);
                }
            }, "System-Thread");

            systemThread.start();
            LOGGER.log(Level.INFO, "System thread started successfully.");
        } else {
            LOGGER.log(Level.WARNING, "Attempted to start the system, but it is already running.");
            throw new IllegalStateException("The system is already running.");
        }
    }

    /**
     * Stops the ticketing system gracefully.
     * If the system is not running, an exception is thrown.
     */
    public void stopSystem() {
        if (systemRunning.get()) {
            systemRunning.set(false);

            // Stop the TicketPool and notify all threads to stop
            ticketPool.stop();

            // Interrupt all Customer threads
            if (systemThread != null && systemThread.isAlive()) {
                systemThread.interrupt();
            }

            // Ensure Vendor thread is stopped by setting vendorActive to false in TicketPool
            if (ticketPool.isVendorActive()) {
                ticketPool.stop();  // This stops the vendor
            }

            LOGGER.info("Ticketing system stopped successfully. All threads are interrupted.");
        } else {
            throw new RuntimeException("The system is not running.");
        }
    }

    /**
     * Sets up the vendor and customer threads.
     * This method is called asynchronously by the system thread.
     */
    private void setupThreads() {
        this.ticketPool = new TicketPool(maxTicketCapacity, messagingTemplate);
        LOGGER.log(Level.INFO, "TicketPool initialized with max capacity: " + maxTicketCapacity);

        // Initialize and start the Vendor thread
        Vendor vendor = new Vendor(ticketPool, totalTicketCapacity, ticketReleaseRate);
        Thread vendorThread = new Thread(vendor, "Vendor");
        vendorThread.start();
        LOGGER.log(Level.INFO, "Vendor thread started.");

        // Initialize and start Customer threads
        int numberOfCustomers = 2;
        Thread[] customerThreads = new Thread[numberOfCustomers];
        for (int i = 0; i < numberOfCustomers; i++) {
            String customerName = "Customer-" + (i + 1);
            Customer customer = new Customer(ticketPool, ticketRetrievalRate, customerName, 1);
            customerThreads[i] = new Thread(customer, customerName);
            customerThreads[i].start();
            LOGGER.log(Level.INFO, customerName + " thread started.");
        }

        // Wait for Vendor thread to finish
        try {
            vendorThread.join();
            LOGGER.log(Level.INFO, "Vendor thread has terminated.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOGGER.log(Level.SEVERE, "Interrupted while waiting for Vendor thread to finish.", e);
        }

        // Wait for Customer threads to finish
        for (Thread t : customerThreads) {
            try {
                t.join();
                LOGGER.log(Level.INFO, t.getName() + " thread has terminated.");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                LOGGER.log(Level.SEVERE, "Interrupted while waiting for " + t.getName() + " to finish.", e);
            }
        }

        LOGGER.log(Level.INFO, "All vendor and customer threads have terminated.");
    }
}