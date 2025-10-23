package com.example.mcp.cli;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Utility to list available Gemini models
 * Run this to see which models your API key has access to
 */
public class ListGeminiModels {

    public static void main(String[] args) {
        String apiKey = System.getenv("GEMINI_API_KEY");

        if (apiKey == null || apiKey.isEmpty()) {
            System.err.println("ERROR: GEMINI_API_KEY environment variable is not set!");
            System.err.println("Set it with: set GEMINI_API_KEY=your-key");
            System.exit(1);
        }

        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║           Gemini Available Models                         ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        System.out.println();

        try {
            String url = "https://generativelanguage.googleapis.com/v1beta/models?key=" + apiKey;

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                String body = response.body();

                System.out.println("Available Models:");
                System.out.println("════════════════════════════════════════════════════════════");

                // Parse and display model names
                String[] lines = body.split("\n");
                int modelCount = 0;

                for (String line : lines) {
                    if (line.contains("\"name\":")) {
                        String modelName = line.split("\"name\": \"")[1].split("\"")[0];

                        // Remove "models/" prefix for display
                        String displayName = modelName.replace("models/", "");

                        modelCount++;
                        System.out.printf("%d. %s\n", modelCount, displayName);
                        System.out.printf("   Full name: %s\n", modelName);

                        // Check if it supports generateContent
                        if (line.contains("generateContent") ||
                            body.substring(body.indexOf(modelName),
                                         body.indexOf(modelName) + 500).contains("generateContent")) {
                            System.out.println("   ✓ Supports generateContent (chat)");
                        }
                        System.out.println();
                    }
                }

                if (modelCount == 0) {
                    System.out.println("No models found. Your API key might not have access.");
                    System.out.println("\nFull response:");
                    System.out.println(body);
                } else {
                    System.out.println("════════════════════════════════════════════════════════════");
                    System.out.printf("Total models found: %d\n", modelCount);
                    System.out.println("\nRECOMMENDED: Use the model name WITHOUT 'models/' prefix");
                    System.out.println("Example: gemini-1.5-flash-001 (not models/gemini-1.5-flash-001)");
                }

            } else {
                System.err.println("Error: HTTP " + response.statusCode());
                System.err.println(response.body());
            }

        } catch (Exception e) {
            System.err.println("Error calling Gemini API: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
