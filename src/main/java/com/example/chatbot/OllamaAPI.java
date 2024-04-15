package com.example.chatbot;

import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;


public class OllamaAPI {

    private static String cleanString(String str) {
        return str.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n");
    }

    static String[] askOllama(String message, List<String> history) throws URISyntaxException, IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        String model = "mayflowergmbh/wiedervereinigung";
        boolean stream = false;
        String[] antwort = new String[2];

        // Convert history list to a single string
        List<String> cleanedHistory = new ArrayList<>();
        for (String str : history) {
            cleanedHistory.add(cleanString(str));
        }
        String historyString = String.join("\\n", cleanedHistory);
        System.out.println(historyString);

        String systemMessage = "Halte deine Antworten kurz";
        String json = "{"
                + "\"model\":\"" + model + "\","
                + "\"system\":\"" + systemMessage + "\","
                + "\"prompt\":\"" + historyString + "\\n" + message + "\","
                + "\"stream\":" + stream
                + "}";

        System.out.println(json);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:11434/api/generate"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        String responseBody = response.body();
        JSONObject lastJsonObject = new JSONObject(response.body());
        System.out.println(response.body());

        if (lastJsonObject.has("response")) {
            Object antwortBot = lastJsonObject.get("response");
            Object context = lastJsonObject.get("context");
            antwort[1] = context.toString();
            antwort[0] = antwortBot.toString();
            return antwort;
        } else {
            System.out.println("Error");
            return antwort;
        }
    }
}
