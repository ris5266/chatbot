package com.example.chatbot;

import org.json.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;

public class JSONReader {
    public static JSONObject readCharacters() {
        JSONObject config;
        try {
            config = new JSONObject(new String(Files.readAllBytes(Paths.get("config.json"))));
        } catch (IOException e) {
            config = new JSONObject();
        }

        // get all characters
        JSONObject characters;
        if (config.has("characters")) {
            characters = config.getJSONObject("characters");
            return characters;
        } else {
            characters = new JSONObject();
            return characters;
        }
    }

    public static void createCharacter(String name, String description, String gender) {
        JSONObject character = new JSONObject();
        name = name.substring(0, 1).toUpperCase() + name.substring(1);
        character.put("name", name);
        character.put("description", description);
        character.put("gender", gender);

        long currentTime = System.currentTimeMillis();

        // read out config.json
        JSONObject config;
        try {
            config = new JSONObject(new String(Files.readAllBytes(Paths.get("config.json"))));
        } catch (NoSuchFileException e) {
            // if file doesnt exist create one and call method again
            try (FileWriter file = new FileWriter("config.json")) {
                file.write(new JSONObject().toString(4));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            createCharacter(name, description, gender);
            return;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // try reading all characters or create new characters object
        JSONObject characters;
        if (config.has("characters")) {
            characters = config.getJSONObject("characters");
        } else {
            characters = new JSONObject();
        }

        // add new character
        characters.put(String.valueOf(currentTime), character);
        config.put("characters", characters);

        // overwrite json file
        try (FileWriter file = new FileWriter("config.json")) {
            file.write(config.toString(4));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void deleteCharacter(String key) {
        JSONObject config;
        try {
            config = new JSONObject(new String(Files.readAllBytes(Paths.get("config.json"))));
        } catch (IOException e) {
            config = new JSONObject();
        }

        // get all characters and delete specific character
        if (config.has("characters")) {
            JSONObject characters = config.getJSONObject("characters");
            characters.remove(key);
            config.put("characters", characters);

            try (FileWriter file = new FileWriter("config.json")) {
                file.write(config.toString(4));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void overwriteCharacter(String key, String name, String description, String gender) {
        JSONObject character = new JSONObject();
        name = name.substring(0, 1).toUpperCase() + name.substring(1);
        character.put("name", name);
        character.put("description", description);
        character.put("gender", gender);

        // read out config.json
        JSONObject config;
        try {
            config = new JSONObject(new String(Files.readAllBytes(Paths.get("config.json"))));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // try reading all characters or create new characters object
        JSONObject characters;
        if (config.has("characters")) {
            characters = config.getJSONObject("characters");
        } else {
            characters = new JSONObject();
        }

        // overwrite existing character or add new character
        characters.put(key, character);
        config.put("characters", characters);

        // overwrite json file
        try (FileWriter file = new FileWriter("config.json")) {
            file.write(config.toString(4));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void changeModel(String model) {
        String newModel = model;
        JSONObject config;
        try {
            config = new JSONObject(new String(Files.readAllBytes(Paths.get("config.json"))));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        config.put("model", newModel);
        try (FileWriter file = new FileWriter("config.json")) {
            file.write(config.toString(4));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void changeModelValue(String newModelValue) {
        JSONObject config;
        try {
            // Read the existing JSON file
            config = new JSONObject(new String(Files.readAllBytes(Paths.get("config.json"))));
        } catch (NoSuchFileException e) {
            System.out.println("File not found");
            return;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Change the value of the model
        if (config.has("model")) {
            JSONObject model = config.getJSONObject("model");
            model.put("name", newModelValue);
        } else {
            System.out.println("Model not found in the JSON file");
            return;
        }

        // Write the modified JSONObject back to the JSON file
        try (FileWriter file = new FileWriter("config.json")) {
            file.write(config.toString(4));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}
