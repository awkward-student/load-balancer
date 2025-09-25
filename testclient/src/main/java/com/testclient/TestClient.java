package com.testclient;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TestClient {
    public static void main(String[] args) {
        HttpClient client = HttpClient.newHttpClient();
        String loadBalancerUrl = "http://localhost:9000/hello";
        int totalRequests = 1000;

        System.out.println("Starting test with " + totalRequests + " requests to the Load Balancer...");

        // Using a fixed thread pool to send requests concurrently
        ExecutorService executor = Executors.newFixedThreadPool(10);

        for (int i = 0; i < totalRequests; i++) {
            final int requestNumber = i + 1;
            executor.submit(() -> {
                try {
                    HttpRequest request = HttpRequest.newBuilder()
                            .uri(new URI(loadBalancerUrl))
                            .GET()
                            .build();

                    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                    System.out.println("Request #" + requestNumber + " received response: " + response.body());
                } catch (Exception e) {
                    System.err.println("Request #" + requestNumber + " failed: " + e.getMessage());
                }
            });
        }

        executor.shutdown();
        System.out.println("All requests submitted. Waiting for completion.");
        try {
            executor.awaitTermination(60, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            System.err.println("Test was interrupted.");
        }
        System.out.println("Test complete.");
    }
}
