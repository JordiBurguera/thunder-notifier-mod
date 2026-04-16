package com.example.thundernotifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

public class NotificationManager {
    private static final Logger LOGGER = LoggerFactory.getLogger("ThunderNotifier");
    private final ConfigManager configManager;
    private final HttpClient httpClient;

    public NotificationManager(ConfigManager configManager) {
        this.configManager = configManager;
        this.httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    public void sendNotification() {
        if (!configManager.isWebhookConfigured()) {
            LOGGER.warn("Webhook URL not configured. Fill in 'discord_webhook.txt'.");
            return;
        }

        String jsonPayload = "{\"content\": \"⚡ Thunderstorm detected in your Minecraft world!\"}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(configManager.getWebhookUrl()))
                .timeout(Duration.ofMinutes(1))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .build();

        CompletableFuture<HttpResponse<String>> responseFuture = httpClient.sendAsync(request,
                HttpResponse.BodyHandlers.ofString());

        responseFuture.thenAccept(response -> {
            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                LOGGER.info("Notification sent successfully!");
            } else {
                LOGGER.error("Failed to send notification. Status code: " + response.statusCode());
            }
        }).exceptionally(e -> {
            LOGGER.error("Error sending notification", e);
            return null;
        });
    }
}
