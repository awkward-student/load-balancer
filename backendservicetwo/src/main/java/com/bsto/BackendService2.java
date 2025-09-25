package com.bsto;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.http.ResponseEntity;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@SpringBootApplication
@RestController
public class BackendService2 implements ApplicationListener<WebServerInitializedEvent> {

    @Value("${server.port}")
    private String port;

    public static void main(String[] args) {
        // Set the server port for this instance
        SpringApplication app = new SpringApplication(BackendService2.class);
        app.setDefaultProperties(java.util.Collections.singletonMap("server.port", "9002"));
        app.run(args);
    }

    // Endpoint that handles requests
    @GetMapping("/hello")
    public String hello() {
        System.out.println("Received request on Backend Service 2 (Port " + port + ")");
        return "Hello from Backend Service 2!";
    }

    // Method to register this service with the Service Registry on application startup
    @Override
    public void onApplicationEvent(final WebServerInitializedEvent event) {
        String serviceUrl = "http://localhost:" + port;
        String registryUrl = "http://localhost:9004/register?serviceUrl=" + serviceUrl;

        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(registryUrl))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                System.out.println("Service registered successfully with the registry: " + serviceUrl);
            } else {
                System.err.println("Failed to register service. Registry responded with status code: " + response.statusCode());
            }
        } catch (Exception e) {
            System.err.println("Error while trying to register service with the registry: " + e.getMessage());
        }
    }
}
