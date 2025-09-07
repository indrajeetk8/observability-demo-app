package com.demo.observability.service;

import io.micrometer.observation.annotation.Observed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class DemoService {
    
    private static final Logger logger = LoggerFactory.getLogger(DemoService.class);
    private final Map<String, Map<String, Object>> userDatabase = new ConcurrentHashMap<>();
    private final Random random = new Random();
    
    @Observed(name = "demo.service.get.user")
    public Map<String, Object> getUser(String userId) {
        logger.debug("Looking up user in service layer: {}", userId);
        
        // Simulate database lookup delay
        simulateDelay(50, 200);
        
        Map<String, Object> user = userDatabase.get(userId);
        if (user == null) {
            logger.warn("User not found in database: {}", userId);
            throw new RuntimeException("User not found: " + userId);
        }
        
        logger.debug("User found in service layer: {}", userId);
        return new HashMap<>(user);
    }
    
    @Observed(name = "demo.service.create.user")
    public Map<String, Object> createUser(String userId, Map<String, Object> userData) {
        logger.debug("Creating user in service layer: {}", userId);
        
        // Simulate database save delay
        simulateDelay(100, 300);
        
        // Validate required fields
        if (!userData.containsKey("name")) {
            logger.error("Missing required field 'name' for user: {}", userId);
            throw new RuntimeException("Missing required field: name");
        }
        
        Map<String, Object> user = new HashMap<>();
        user.put("id", userId);
        user.put("name", userData.get("name"));
        user.put("email", userData.getOrDefault("email", ""));
        user.put("createdAt", System.currentTimeMillis());
        user.put("status", "active");
        
        // Simulate occasional creation failure
        if (random.nextInt(10) == 0) {
            logger.error("Simulated database error while creating user: {}", userId);
            throw new RuntimeException("Database error occurred");
        }
        
        userDatabase.put(userId, user);
        logger.info("User created successfully in service layer: {}", userId);
        
        return new HashMap<>(user);
    }
    
    @Observed(name = "demo.service.process.data")
    public void processData(String data) {
        logger.debug("Processing data in service layer: {}", data);
        
        // Simulate complex data processing
        simulateDelay(200, 500);
        
        logger.info("Data processing completed in service layer");
    }
    
    private void simulateDelay(int minMs, int maxMs) {
        try {
            int delay = minMs + random.nextInt(maxMs - minMs);
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.warn("Service operation interrupted");
        }
    }
    
    public Map<String, Object> getServiceStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", userDatabase.size());
        stats.put("timestamp", System.currentTimeMillis());
        return stats;
    }
}
