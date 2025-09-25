package com.serviceregistry;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@SpringBootApplication
@RestController
public class ServiceRegistry {

    // In-memory list to store registered service URLs
    private final List<String> registeredServices = new ArrayList<>();

    // Flag to ensure a consistent message after startup
    private final AtomicBoolean isInitialMessagePrinted = new AtomicBoolean(false);

    public static void main(String[] args) {
        // Set the server port for this instance
        SpringApplication app = new SpringApplication(ServiceRegistry.class);
        app.setDefaultProperties(java.util.Collections.singletonMap("server.port", "9004"));
        app.run(args);
    }

    // Endpoint for services to register themselves
    @GetMapping("/register")
    public String registerService(@RequestParam String serviceUrl) {
        if (!registeredServices.contains(serviceUrl)) {
            registeredServices.add(serviceUrl);
            System.out.println("Service registered: " + serviceUrl);
        }
        return "Registration successful.";
    }

    // Endpoint for the Load Balancer to fetch the list of available services
    @GetMapping("/services")
    public List<String> getServices() {
        if (!isInitialMessagePrinted.getAndSet(true)) {
            System.out.println("Service Registry is ready and listening on port 9004.");
        }
        return registeredServices;
    }
}
