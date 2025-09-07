package com.demo.observability.controller;

import com.demo.observability.service.DemoService;
import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.observation.annotation.Observed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

@RestController
@RequestMapping("/api/demo")
public class DemoController {
    
    private static final Logger logger = LoggerFactory.getLogger(DemoController.class);
    private final DemoService demoService;
    private final MeterRegistry meterRegistry;
    private final Random random = new Random();
    
    @Autowired
    public DemoController(DemoService demoService, MeterRegistry meterRegistry) {
        this.demoService = demoService;
        this.meterRegistry = meterRegistry;
    }
    
    @GetMapping("/health")
    @Timed(name = "demo.health.timer", description = "Timer for health endpoint")
    @Counted(name = "demo.health.counter", description = "Counter for health endpoint calls")
    public ResponseEntity<Map<String, Object>> health() {
        String requestId = UUID.randomUUID().toString();
        MDC.put("requestId", requestId);
        
        logger.info("Health check requested with requestId: {}", requestId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("timestamp", System.currentTimeMillis());
        response.put("requestId", requestId);
        response.put("service", "observability-demo");
        
        // Custom metric
        meterRegistry.counter("demo.health.requests", "status", "success").increment();
        
        MDC.clear();
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/users/{userId}")
    @Observed(name = "demo.user.get", contextualName = "get-user")
    @Timed(name = "demo.user.get.timer")
    public ResponseEntity<Map<String, Object>> getUser(@PathVariable String userId) {
        String requestId = UUID.randomUUID().toString();
        MDC.put("requestId", requestId);
        MDC.put("userId", userId);
        
        logger.info("Getting user with ID: {} and requestId: {}", userId, requestId);
        
        try {
            Map<String, Object> user = demoService.getUser(userId);
            
            logger.info("Successfully retrieved user: {} for requestId: {}", userId, requestId);
            meterRegistry.counter("demo.user.requests", "status", "success", "userId", userId).increment();
            
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            logger.error("Error retrieving user: {} for requestId: {}", userId, requestId, e);
            meterRegistry.counter("demo.user.requests", "status", "error", "userId", userId).increment();
            
            Map<String, Object> error = new HashMap<>();
            error.put("error", "User not found");
            error.put("userId", userId);
            error.put("requestId", requestId);
            
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } finally {
            MDC.clear();
        }
    }
    
    @PostMapping("/users")
    @Observed(name = "demo.user.create")
    @Timed(name = "demo.user.create.timer")
    public ResponseEntity<Map<String, Object>> createUser(@RequestBody Map<String, Object> userData) {
        String requestId = UUID.randomUUID().toString();
        String userId = UUID.randomUUID().toString();
        
        MDC.put("requestId", requestId);
        MDC.put("userId", userId);
        
        logger.info("Creating new user with requestId: {} and userId: {}", requestId, userId);
        logger.debug("User data: {}", userData);
        
        try {
            Map<String, Object> createdUser = demoService.createUser(userId, userData);
            
            logger.info("Successfully created user: {} for requestId: {}", userId, requestId);
            meterRegistry.counter("demo.user.created", "status", "success").increment();
            
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
        } catch (Exception e) {
            logger.error("Error creating user for requestId: {}", requestId, e);
            meterRegistry.counter("demo.user.created", "status", "error").increment();
            
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to create user");
            error.put("requestId", requestId);
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        } finally {
            MDC.clear();
        }
    }
    
    @GetMapping("/slow")
    @Timed(name = "demo.slow.timer", description = "Timer for slow endpoint")
    public ResponseEntity<Map<String, Object>> slowEndpoint() {
        String requestId = UUID.randomUUID().toString();
        MDC.put("requestId", requestId);
        
        logger.info("Slow endpoint called with requestId: {}", requestId);
        
        // Simulate slow processing
        int delay = 1000 + random.nextInt(3000); // 1-4 seconds
        try {
            logger.debug("Simulating slow processing for {} ms", delay);
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            logger.warn("Slow processing interrupted for requestId: {}", requestId);
            Thread.currentThread().interrupt();
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "This was a slow operation");
        response.put("processingTime", delay);
        response.put("requestId", requestId);
        
        meterRegistry.timer("demo.slow.processing.time").record(delay, java.util.concurrent.TimeUnit.MILLISECONDS);
        
        logger.info("Slow endpoint completed for requestId: {} in {} ms", requestId, delay);
        MDC.clear();
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/error")
    @Counted(name = "demo.error.counter")
    public ResponseEntity<Map<String, Object>> errorEndpoint(@RequestParam(defaultValue = "false") boolean forceError) {
        String requestId = UUID.randomUUID().toString();
        MDC.put("requestId", requestId);
        
        logger.info("Error endpoint called with forceError: {} and requestId: {}", forceError, requestId);
        
        if (forceError || random.nextBoolean()) {
            logger.error("Simulated error occurred for requestId: {}", requestId);
            meterRegistry.counter("demo.errors", "type", "simulated").increment();
            
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Simulated error occurred");
            error.put("requestId", requestId);
            error.put("timestamp", System.currentTimeMillis());
            
            MDC.clear();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
        
        logger.info("Error endpoint completed successfully for requestId: {}", requestId);
        meterRegistry.counter("demo.errors", "type", "success").increment();
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "No error this time!");
        response.put("requestId", requestId);
        
        MDC.clear();
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/metrics")
    public ResponseEntity<Map<String, Object>> customMetrics() {
        String requestId = UUID.randomUUID().toString();
        MDC.put("requestId", requestId);
        
        logger.info("Custom metrics endpoint called with requestId: {}", requestId);
        
        // Generate some custom metrics
        meterRegistry.gauge("demo.random.value", random.nextDouble() * 100);
        meterRegistry.counter("demo.metrics.requests").increment();
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Custom metrics generated");
        response.put("requestId", requestId);
        response.put("availableMetrics", "Check /actuator/prometheus for all metrics");
        
        MDC.clear();
        return ResponseEntity.ok(response);
    }
}
