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

    static String[] askOllama(String message, String history, String systemPrompt) throws URISyntaxException, IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        String model = "llama2";
        boolean stream = false;
        String[] antwort = new String[2];


        System.out.println(history);

        String systemMessage = systemPrompt + ". Act like your the bestfriend and just talk casually. Keep your responses short and to the point. Only response to the last sentence the user sends you. You are not an AI or an chatbot, so dont behave like one. Dont send any emojis";
        String json = "{"
                + "\"model\":\"" + model + "\","
                + "\"system\":\"" + systemMessage + "\","
                + "\"prompt\":\"" + history + "\\n" + "User: " + message + "\","
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
