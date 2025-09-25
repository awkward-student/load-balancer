package com.loadbalancer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootApplication
@RestController
public class LoadBalancer {

    private List<String> serviceInstances;
    private final RestTemplate restTemplate = new RestTemplate();
    private final String serviceRegistryUrl = "http://localhost:9004/services";

    // Map to store active connections for each service instance
    private final Map<String, AtomicInteger> activeConnections = new HashMap<>();

    public LoadBalancer() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        // Schedule a task to refresh the list of service instances
        scheduler.scheduleAtFixedRate(this::refreshServiceInstances, 0, 5, TimeUnit.SECONDS);
    }

    private void refreshServiceInstances() {
        try {
            String[] services = restTemplate.getForObject(serviceRegistryUrl, String[].class);
            if (services != null) {
                this.serviceInstances = List.of(services);
                // Initialize active connections map for new services
                for (String service : this.serviceInstances) {
                    activeConnections.putIfAbsent(service, new AtomicInteger(0));
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to get service instances from registry: " + e.toString());
            this.serviceInstances = List.of(); // Clear the list on failure
        }
    }

    @GetMapping("/hello")
    public String forwardRequest() {
        if (serviceInstances == null || serviceInstances.isEmpty()) {
            return "No backend services available.";
        }

        // Find the service with the least number of active connections
        String leastConnectedService = getLeastConnectedService();
        if (leastConnectedService == null) {
            return "Error: Could not find a suitable service.";
        }

        // Increment the connection count for the chosen service
        activeConnections.get(leastConnectedService).incrementAndGet();

        try {
            // Appending the correct endpoint to the service URL
            String response = restTemplate.getForObject(leastConnectedService + "/hello", String.class);
            System.out.println("Forwarding request to: " + leastConnectedService);
            return response;
        } catch (Exception e) {
            System.err.println("Failed to forward request to " + leastConnectedService + ": " + e.toString());
            return "Error forwarding request.";
        } finally {
            // Decrement the connection count regardless of success or failure
            activeConnections.get(leastConnectedService).decrementAndGet();
        }
    }

    private String getLeastConnectedService() {
        String leastConnectedService = null;
        int minConnections = Integer.MAX_VALUE;

        for (String service : serviceInstances) {
            int currentConnections = activeConnections.getOrDefault(service, new AtomicInteger(0)).get();
            if (currentConnections < minConnections) {
                minConnections = currentConnections;
                leastConnectedService = service;
            }
        }
        return leastConnectedService;
    }

    public static void main(String[] args) {
        // Explicitly set the port to 9000
        SpringApplication.run(LoadBalancer.class, "--server.port=9000");
    }
}
