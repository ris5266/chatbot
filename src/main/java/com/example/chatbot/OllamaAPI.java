package com.example.chatbot;

import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class OllamaAPI {

    static String[] askOllama(String model, String message, String history, String systemPrompt) throws URISyntaxException, IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        boolean stream = false;
        String[] answer = new String[2];
        String systemMessage = systemPrompt + ". Act like your the bestfriend and just talk casually. Keep your responses short and to the point. Only response to the last sentence the user sends you. You are not an AI or an chatbot, so dont behave like one. Dont send any emojis";
        System.out.println(history);

        // build json and send request
        String json = "{"
                + "\"model\":\"" + model + "\","
                + "\"system\":\"" + systemMessage + "\","
                + "\"prompt\":\"" + history + "\\n" + "User: " + message + "\","
                + "\"stream\":" + stream
                + "}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:11434/api/generate"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // take json response and return the context and response
        JSONObject lastJsonObject = new JSONObject(response.body());
        System.out.println(response.body());

        if (lastJsonObject.has("response")) {
            Object answerBot = lastJsonObject.get("response");
            Object context = lastJsonObject.get("context");
            answer[1] = context.toString();
            answer[0] = answerBot.toString();
            return answer;
        } else {
            System.out.println("Error");
            return answer;
        }
    }
}
