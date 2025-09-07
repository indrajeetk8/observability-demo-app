package com.demo.observability.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(Model model) {
        // Add current timestamp to the model
        model.addAttribute("timestamp", new java.util.Date().toString());
        model.addAttribute("status", "Running");
        return "welcome";
    }
    
    @GetMapping("/api")
    @ResponseBody
    public Map<String, Object> apiInfo() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Welcome to Observability Demo API!");
        response.put("status", "UP");
        response.put("timestamp", System.currentTimeMillis());
        
        Map<String, String> endpoints = new HashMap<>();
        endpoints.put("health", "/api/demo/health");
        endpoints.put("users", "/api/demo/users");
        endpoints.put("slow", "/api/demo/slow");
        endpoints.put("error", "/api/demo/error");
        endpoints.put("metrics", "/api/demo/metrics");
        endpoints.put("actuator-health", "/actuator/health");
        endpoints.put("prometheus-metrics", "/actuator/prometheus");
        endpoints.put("h2-console", "/h2-console");
        
        response.put("available_endpoints", endpoints);
        
        return response;
    }
}
