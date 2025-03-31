package com.TicketingSystem.Ticketing.Controllers;

import com.TicketingSystem.Ticketing.Entities.ConfigEntity;
import com.TicketingSystem.Ticketing.Services.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/config")
@CrossOrigin(origins = "http://localhost:4200")
public class ConfigController {

    private final ConfigService configService;

    @Autowired
    public ConfigController(ConfigService configService) {
        this.configService = configService;
    }

    // Endpoint to load configuration from the database
    @GetMapping("/load")
    public ResponseEntity<ConfigEntity> loadConfig() {
        try {
            ConfigEntity config = configService.getConfig();
            if (config == null) {
                return ResponseEntity.status(404).body(null);  // Config not found
            }
            return ResponseEntity.ok(config);  // Return ConfigEntity as JSON
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);  // Handle error gracefully
        }
    }

    // Endpoint to start the ticketing system
    @PostMapping("/start")
    public ResponseEntity<String> startSystem() {
        try {
            configService.startSystem();
            return ResponseEntity.ok("Ticketing system started successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error starting the system: " + e.getMessage());
        }
    }

    // Endpoint to stop the ticketing system
    @PostMapping("/stop")
    public ResponseEntity<String> stopSystem() {
        try {
            configService.stopSystem();
            return ResponseEntity.ok("Ticketing system stopped successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error stopping the system: " + e.getMessage());
        }
    }

    // Endpoint to set the configuration values using a ConfigEntity object and save to the database
    @PostMapping("/setEntity")
    public ResponseEntity<String> setEntityConfig(@RequestBody ConfigEntity newConfig) {
        try {
            // Save the configuration to the database
            configService.saveConfig(newConfig);
            return ResponseEntity.ok("Configuration updated successfully in the database.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error saving updated configuration: " + e.getMessage());
        }
    }
}
