package com.demo.observability.controller;

import com.demo.observability.service.DemoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DemoController.class)
class DemoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DemoService demoService;
    
    @MockBean
    private MeterRegistry meterRegistry;

    @Autowired
    private ObjectMapper objectMapper;

    private Map<String, Object> sampleUser;

    @BeforeEach
    void setUp() {
        sampleUser = new HashMap<>();
        sampleUser.put("id", "test-user-123");
        sampleUser.put("name", "John Doe");
        sampleUser.put("email", "john@example.com");
        sampleUser.put("createdAt", System.currentTimeMillis());
        sampleUser.put("status", "active");
        
        // Set up MeterRegistry mocks
        Counter mockCounter = mock(Counter.class);
        Timer mockTimer = mock(Timer.class);
        
        // Mock different variations of counter method calls
        when(meterRegistry.counter(anyString())).thenReturn(mockCounter);
        when(meterRegistry.counter(anyString(), anyString(), anyString())).thenReturn(mockCounter);
        when(meterRegistry.counter(anyString(), anyString(), anyString(), anyString(), anyString())).thenReturn(mockCounter);
        when(meterRegistry.counter(anyString(), any(String[].class))).thenReturn(mockCounter);
        
        // Mock timer method calls
        when(meterRegistry.timer(anyString())).thenReturn(mockTimer);
        when(meterRegistry.timer(anyString(), any(String[].class))).thenReturn(mockTimer);
        
        // Mock gauge method
        when(meterRegistry.gauge(anyString(), any(Double.class))).thenReturn(null);
        
        // Configure counter and timer behavior - these methods are void
        doNothing().when(mockCounter).increment();
        doNothing().when(mockTimer).record(anyLong(), any());
    }

    @Test
    void testHealthEndpoint() throws Exception {
        mockMvc.perform(get("/api/demo/health"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.service").value("observability-demo"))
                .andExpect(jsonPath("$.requestId").exists())
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void testGetUser_Success() throws Exception {
        // Given
        String userId = "test-user-123";
        when(demoService.getUser(userId)).thenReturn(sampleUser);

        // When & Then
        mockMvc.perform(get("/api/demo/users/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john@example.com"))
                .andExpect(jsonPath("$.status").value("active"));
    }

    @Test
    void testGetUser_NotFound() throws Exception {
        // Given
        String userId = "non-existent-user";
        when(demoService.getUser(userId)).thenThrow(new RuntimeException("User not found: " + userId));

        // When & Then
        mockMvc.perform(get("/api/demo/users/{userId}", userId))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("User not found"))
                .andExpect(jsonPath("$.userId").value(userId))
                .andExpect(jsonPath("$.requestId").exists());
    }

    @Test
    void testCreateUser_Success() throws Exception {
        // Given
        Map<String, Object> userData = new HashMap<>();
        userData.put("name", "Jane Doe");
        userData.put("email", "jane@example.com");

        when(demoService.createUser(anyString(), any(Map.class))).thenReturn(sampleUser);

        // When & Then
        mockMvc.perform(post("/api/demo/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userData)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").exists())
                .andExpect(jsonPath("$.status").value("active"));
    }

    @Test
    void testCreateUser_Error() throws Exception {
        // Given
        Map<String, Object> userData = new HashMap<>();
        userData.put("name", "Jane Doe");
        userData.put("email", "jane@example.com");

        when(demoService.createUser(anyString(), any(Map.class)))
                .thenThrow(new RuntimeException("Database error occurred"));

        // When & Then
        mockMvc.perform(post("/api/demo/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userData)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Failed to create user"))
                .andExpect(jsonPath("$.requestId").exists());
    }

    @Test
    void testSlowEndpoint() throws Exception {
        mockMvc.perform(get("/api/demo/slow"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("This was a slow operation"))
                .andExpect(jsonPath("$.processingTime").exists())
                .andExpect(jsonPath("$.requestId").exists());
    }

    @Test
    void testErrorEndpoint_Success() throws Exception {
        mockMvc.perform(get("/api/demo/error")
                .param("forceError", "false"))
                .andExpectAll(
                        result -> {
                            // Since the error endpoint randomly succeeds or fails,
                            // we just check that it returns either 200 or 500
                            int status = result.getResponse().getStatus();
                            assert status == 200 || status == 500;
                        }
                );
    }

    @Test
    void testErrorEndpoint_ForceError() throws Exception {
        mockMvc.perform(get("/api/demo/error")
                .param("forceError", "true"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Simulated error occurred"))
                .andExpect(jsonPath("$.requestId").exists())
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void testCustomMetrics() throws Exception {
        mockMvc.perform(get("/api/demo/metrics"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Custom metrics generated"))
                .andExpect(jsonPath("$.requestId").exists())
                .andExpect(jsonPath("$.availableMetrics").exists());
    }
}
