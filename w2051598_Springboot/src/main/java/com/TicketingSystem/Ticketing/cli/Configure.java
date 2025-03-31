package com.TicketingSystem.Ticketing.cli;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Configure {
    private int maxTicketCapacity;
    private int releaseCapacity;
    private long ticketReleaseRate;
    private long ticketRetrievalRate;

    // Constructor that prompts for loading or creating new configuration
    public Configure() {
        Scanner scanner = new Scanner(System.in);
        File configFile = new File("config.json");

        if (configFile.exists()) {
            System.out.print("Do you want to load the previous configuration (y/n)? ");
            String choice = scanner.nextLine().trim().toLowerCase();

            if (choice.equals("y")) {
                loadFromJson(configFile);
            } else {
                // Prompt user for new configuration
                promptForNewConfig(scanner);
                // Save new configuration to the file
                saveToJson(configFile);
            }
        } else {
            // If the config file doesn't exist, prompt for a new configuration
            System.out.println("No previous configuration found. Creating a new one.");
            promptForNewConfig(scanner);
            saveToJson(configFile);
        }
    }

    // Method to prompt user for new configuration
    private void promptForNewConfig(Scanner scanner) {
        // Handle maximum ticket capacity input
        boolean validInput = false;
        while (!validInput) {
            try {
                System.out.print("Enter maximum ticket capacity: ");
                this.maxTicketCapacity = scanner.nextInt();
                validInput = true; // Valid input, exit loop
            } catch (InputMismatchException e) {
                System.out.println("Error: Please enter a valid integer for maximum ticket capacity.");
                scanner.nextLine(); // Consume the invalid input
            }
        }

        // Handle release capacity input
        validInput = false;
        while (!validInput) {
            try {
                System.out.print("Enter total ticket capacity per release: ");
                this.releaseCapacity = scanner.nextInt();
                // Validate release capacity
                if (this.releaseCapacity > this.maxTicketCapacity) {
                    System.out.println("Error: Release capacity cannot exceed maximum ticket capacity. Please try again.");
                } else {
                    validInput = true; // Valid input, exit loop
                }
            } catch (InputMismatchException e) {
                System.out.println("Error: Please enter a valid integer for release capacity.");
                scanner.nextLine(); // Consume the invalid input
            }
        }

        // Handle ticket release rate input
        validInput = false;
        while (!validInput) {
            try {
                System.out.print("Enter Ticket release rate (in milliseconds): ");
                this.ticketReleaseRate = scanner.nextLong();
                validInput = true; // Valid input, exit loop
            } catch (InputMismatchException e) {
                System.out.println("Error: Please enter a valid long value for ticket release rate.");
                scanner.nextLine(); // Consume the invalid input
            }
        }

        // Handle ticket retrieval rate input
        validInput = false;
        while (!validInput) {
            try {
                System.out.print("Enter ticket retrieval rate (in milliseconds): ");
                this.ticketRetrievalRate = scanner.nextLong();
                validInput = true; // Valid input, exit loop
            } catch (InputMismatchException e) {
                System.out.println("Error: Please enter a valid long value for ticket retrieval rate.");
                scanner.nextLine(); // Consume the invalid input
            }
        }
    }


    // Save configuration to a JSON file
    private void saveToJson(File configFile) {
        Gson gson = new Gson();
        JsonObject jsonConfig = new JsonObject();
        jsonConfig.addProperty("maxTicketCapacity", maxTicketCapacity);
        jsonConfig.addProperty("releaseCapacity", releaseCapacity);
        jsonConfig.addProperty("ticketReleaseRate", ticketReleaseRate);
        jsonConfig.addProperty("ticketRetrievalRate", ticketRetrievalRate);

        try (FileWriter writer = new FileWriter(configFile)) {
            gson.toJson(jsonConfig, writer);
            System.out.println("Configuration saved to config.json");
        } catch (IOException e) {
            System.err.println("Error saving configuration: " + e.getMessage());
        }
    }

    // Load configuration from a JSON file
    private void loadFromJson(File configFile) {
        Gson gson = new Gson();
        try (FileReader reader = new FileReader(configFile)) {
            JsonObject jsonConfig = gson.fromJson(reader, JsonObject.class);
            this.maxTicketCapacity = jsonConfig.get("maxTicketCapacity").getAsInt();
            this.releaseCapacity = jsonConfig.get("releaseCapacity").getAsInt();
            this.ticketReleaseRate = jsonConfig.get("ticketReleaseRate").getAsLong();
            this.ticketRetrievalRate = jsonConfig.get("ticketRetrievalRate").getAsLong();
            System.out.println("Configuration loaded from config.json");
        } catch (IOException | JsonSyntaxException e) {
            System.err.println("Error loading configuration: " + e.getMessage());
        }
    }

    // Getters for the configuration values
    public int getMaxTicketCapacity() {
        return maxTicketCapacity;
    }

    public int getReleaseCapacity() {
        return releaseCapacity;
    }

    public long getTicketReleaseRate() {
        return ticketReleaseRate;
    }

    public long getTicketRetrievalRate() {
        return ticketRetrievalRate;
    }
}
