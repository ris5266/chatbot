package com.example.chatbot;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import org.json.JSONObject;

import javax.sound.sampled.*;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

public class ChatBot extends Application {
    private String currentName;
    private String currentDescription;
    private String currentGender;
    private TextFlow chat;
    private TextField chatinput;
    private ScrollPane scrollPane;
    private String conversationHistory;
    private String systemPrompt;
    private VBox charactervbox;
    private Button currentCharacterButton = null;
    private String currentModel;

    public static void main(String[] args) {
        launch(args);
    }

    public ChatBot() {
        JSONObject characters = JSONReader.readCharacters();

        // select the first character
        String firstKey = characters.keys().next();
        JSONObject firstCharacter = characters.getJSONObject(firstKey);
        currentName = firstCharacter.getString("name");
        currentDescription = firstCharacter.getString("description");
        currentGender = firstCharacter.getString("gender");

        JSONObject config;
        try {
            config = new JSONObject(new String(Files.readAllBytes(Paths.get("config.json"))));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        currentModel = config.getString("model");
    }

    private void loadCharacters() {
        JSONObject characters = JSONReader.readCharacters();
        boolean isFirstCharacter = true;

        // create button for each character
        for (String key : characters.keySet()) {
            JSONObject character = characters.getJSONObject(key);
            String name = character.getString("name").substring(0, 1).toUpperCase() + character.getString("name").substring(1);

            Button characterButton = new Button(name);
            characterButton.setStyle("-fx-background-color: #1d1d1d;");
            characterButton.setPrefHeight(33);
            characterButton.setPrefWidth(107);

            characterButton.setOnMouseEntered(e -> {
                if (characterButton == currentCharacterButton) {
                    characterButton.setStyle("-fx-background-color: #1d1d1d; -fx-text-fill: lightblue;");
                } else {
                    characterButton.setStyle("-fx-background-color: #1d1d1d; -fx-text-fill: #e5e5e5;");
                }
            });

            characterButton.setOnMouseExited(e -> {
                if (characterButton == currentCharacterButton) {
                    characterButton.setStyle("-fx-background-color: #1d1d1d; -fx-text-fill: lightblue;");
                } else {
                    characterButton.setStyle("-fx-background-color: #1d1d1d;");
                }
            });

            characterButton.setOnAction(e -> {
                chat.getChildren().clear();
                conversationHistory = "";
                systemPrompt = "You are " + name + ". " + character.getString("description");
                currentDescription = character.getString("description");
                currentGender = character.getString("gender");
                currentName = name;

                // changes font color to the one who is currently selected
                if (currentCharacterButton != null) {
                    currentCharacterButton.setStyle("-fx-background-color: #1d1d1d;");
                }
                characterButton.setStyle("-fx-background-color: #1d1d1d; -fx-text-fill: lightblue;");
                currentCharacterButton = characterButton;
            });

            if (isFirstCharacter) {
                characterButton.setStyle("-fx-background-color: #1d1d1d; -fx-text-fill: lightblue;");
                currentCharacterButton = characterButton;
                isFirstCharacter = false;
            }

            // delete character on right-click
            ContextMenu contextMenu = new ContextMenu();
            MenuItem deleteItem = new MenuItem("Delete Character");
            deleteItem.setOnAction(e -> {
                JSONReader.deleteCharacter(key);
                ChatBot chatBot = new ChatBot();
                try {
                    chatBot.start((Stage) characterButton.getScene().getWindow());
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            });

            contextMenu.getItems().add(deleteItem);
            characterButton.setOnContextMenuRequested(e -> {
                contextMenu.show(characterButton, e.getScreenX(), e.getScreenY());
            });
            charactervbox.getChildren().add(characterButton);
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // splitting the scene in two parts
        HBox splitter = new HBox();
        splitter.setStyle("-fx-background-color: #121212;");
        splitter.setPrefHeight(750);
        splitter.setPrefWidth(1000);
        splitter.setSpacing(20);

        // left side for the characters, settings and model
        VBox left = new VBox();
        left.setStyle("-fx-background-color:  #1d1d1d;");
        left.setPrefHeight(571);
        left.setPrefWidth(107);
        left.setPadding(new Insets(10, 10, 10, 10));

        // textfield for changing the model
        FlowPane placeholder = new FlowPane();
        placeholder.setPrefHeight(510);
        placeholder.setPrefWidth(107);

        TextField placeholderField = new TextField();
        placeholderField.setText(currentModel);
        placeholderField.setPrefWidth(85);
        placeholder.setAlignment(Pos.TOP_CENTER);
        Button placeholderButton = new Button("+");

        HBox hboxmodel = new HBox();
        hboxmodel.setAlignment(Pos.CENTER);
        hboxmodel.getChildren().addAll(placeholderField, placeholderButton);

        placeholderButton.setOnAction(e -> {
            JSONReader.changeModel(placeholderField.getText());
            currentModel = placeholderField.getText();
        });

        placeholder.getChildren().addAll(hboxmodel);

        // characters
        left.getChildren().add(placeholder);
        charactervbox = new VBox();
        charactervbox.setPrefHeight(742);
        charactervbox.setPrefWidth(107);
        Button newCharacter = new Button("Create Character");
        newCharacter.setStyle("-fx-background-color: #1d1d1d; -fx-text-fill: #e5e5e5");
        newCharacter.setPrefHeight(33);
        newCharacter.setPrefWidth(107);

        // modal window to create a new character
        newCharacter.setOnAction(e -> {
            Stage newCharacterStage = new Stage();
            newCharacterStage.setTitle("Create Character");
            newCharacterStage.getIcons().add(new Image("icon.jpeg"));
            newCharacterStage.initOwner(primaryStage);
            newCharacterStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);

            Label nameLabel = new Label("Character name:");
            TextField nameField = new TextField();
            Label descriptionLabel = new Label("Character description:");
            TextArea descriptionField = new TextArea();
            RadioButton femaleButton = new RadioButton("Female");
            RadioButton maleButton = new RadioButton("Male");
            Label submitLabel = new Label("Character Gender:");
            ToggleGroup genderGroup = new ToggleGroup();
            femaleButton.setToggleGroup(genderGroup);
            maleButton.setToggleGroup(genderGroup);
            Button submitButton = new Button("Submit");
            submitButton.setStyle("-fx-background-color: black; -fx-text-fill: white;");

            FlowPane submitPane = new FlowPane();
            submitPane.setPadding(new Insets(0, 0, 5, 0));
            submitPane.getChildren().add(submitButton);
            submitPane.setAlignment(Pos.CENTER);

            // add character to config.json and reload scene to show the character in the list
            submitButton.setOnAction(event -> {
                String name = nameField.getText();
                String description = descriptionField.getText();
                String gender = ((RadioButton) genderGroup.getSelectedToggle()).getText();
                JSONReader.createCharacter(name, description, gender);

                // reload current scene
                ChatBot chatBot = new ChatBot();
                try {
                    chatBot.start(primaryStage);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
                newCharacterStage.close();
            });

            VBox formLayout = new VBox(10, nameLabel, nameField, descriptionLabel, descriptionField, submitLabel, femaleButton, maleButton, submitPane);
            formLayout.setPadding(new Insets(10));
            formLayout.setStyle("-fx-background-color: white;");

            newCharacterStage.setScene(new Scene(formLayout));
            newCharacterStage.setResizable(false);
            newCharacterStage.show();
        });
        charactervbox.getChildren().addAll(newCharacter);

        // settings
        FlowPane settingspane = new FlowPane();
        settingspane.setPadding(new Insets(0, 0, 5, 0));
        Button settings = new Button("Edit Character");
        settings.setStyle("-fx-background-color: #1d1d1d; -fx-text-fill:  #e5e5e5;");
        settingspane.getChildren().add(settings);
        settings.setPrefHeight(33);
        settings.setPrefWidth(107);
        left.getChildren().addAll(charactervbox, settingspane);

        // styling
        settings.setOnMouseEntered(e -> {
            settings.setStyle("-fx-border-color: white; -fx-background-color: #1d1d1d; -fx-text-fill: #e5e5e5;");
        });
        settings.setOnMouseExited(e -> {
            settings.setStyle("-fx-background-color: #1d1d1d; -fx-text-fill: #e5e5e5;");
        });

        newCharacter.setOnMouseEntered(e -> {
            newCharacter.setStyle("-fx-border-color: white; -fx-background-color: #1d1d1d; -fx-text-fill: #e5e5e5;");
        });
        newCharacter.setOnMouseExited(e -> {
            newCharacter.setStyle("-fx-background-color: #1d1d1d; -fx-text-fill: #e5e5e5;");
        });

        // settings modal window to change information about the current character
        settings.setOnAction(e -> {
            Stage editCharacterStage = new Stage();
            editCharacterStage.setTitle("Edit Character");
            editCharacterStage.getIcons().add(new Image("icon.jpeg"));
            editCharacterStage.initOwner(primaryStage);
            editCharacterStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);

            Label nameLabel = new Label("Character name:");
            TextField nameField = new TextField(currentName);
            Label descriptionLabel = new Label("Character description:");
            TextArea descriptionField = new TextArea(currentDescription);
            RadioButton femaleButton = new RadioButton("Female");
            RadioButton maleButton = new RadioButton("Male");
            ToggleGroup genderGroup = new ToggleGroup();
            femaleButton.setToggleGroup(genderGroup);
            maleButton.setToggleGroup(genderGroup);

            if ("Female".equals(currentGender)) {
                femaleButton.setSelected(true);
            } else if ("Male".equals(currentGender)) {
                maleButton.setSelected(true);
            }
            Label submitLabel = new Label("Character Gender:");

            Button submitButton = new Button("Submit");

            submitButton.setStyle("-fx-background-color: black; -fx-text-fill: white;");

            // get all the information and overwrite the current character
            submitButton.setOnAction(event -> {
                String name = nameField.getText();
                String description = descriptionField.getText();
                String gender = ((RadioButton) genderGroup.getSelectedToggle()).getText();

                String currentCharacterKey = null;
                JSONObject characters = JSONReader.readCharacters();
                for (String key : characters.keySet()) {
                    JSONObject character = characters.getJSONObject(key);
                    if (character.getString("name").equals(currentName)) {
                        currentCharacterKey = key;
                        break;
                    }
                }

                if (currentCharacterKey != null) {
                    JSONReader.overwriteCharacter(currentCharacterKey, name, description, gender);
                }

                // reload current scene
                ChatBot chatBot = new ChatBot();
                try {
                    chatBot.start(primaryStage);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
                editCharacterStage.close();
            });
            FlowPane submitPane = new FlowPane();

            submitPane.setPadding(new Insets(0, 0, 5, 0));
            submitPane.getChildren().add(submitButton);
            submitPane.setAlignment(Pos.CENTER);

            VBox formLayout = new VBox(10, nameLabel, nameField, descriptionLabel, descriptionField, submitLabel, femaleButton, maleButton, submitPane);
            formLayout.setPadding(new Insets(10));
            formLayout.setStyle("-fx-background-color: white;");

            editCharacterStage.setScene(new Scene(formLayout));
            editCharacterStage.setResizable(false);
            editCharacterStage.show();
        });

        // right side for the chat and textfield
        VBox right = new VBox();
        right.setPrefHeight(689);
        right.setPrefWidth(855);

        chat = new TextFlow();
        chat.setPrefHeight(794);
        chat.setPrefWidth(855);
        chat.setStyle("-fx-font-size: 15px; -fx-font-family: 'Arial'");

        // add a scrollpane to be able to scroll in the chat
        scrollPane = new ScrollPane(chat);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setVvalue(1.0);
        scrollPane.setStyle("-fx-background: #121212; -fx-border-color: #121212; -fx-background-color: transparent");
        scrollPane.setPadding(new Insets(10, 10, 10, 10));

        VBox container = new VBox(chat);
        container.getChildren().add(scrollPane);
        VBox.setMargin(chat, new Insets(20, 50, 20, 0));

        // textfield and button to send messages
        HBox chatinputbox = new HBox();
        chatinputbox.setSpacing(10);
        chatinputbox.setPadding(new Insets(5, 5, 10, 5));
        chatinputbox.setPrefHeight(35);
        chatinputbox.setPrefWidth(895);

        chatinput = new TextField();
        chatinput.setPrefHeight(31);
        chatinput.setPrefWidth(788);
        // preselect the textfield
        Platform.runLater(() -> chatinput.requestFocus());

        Button send = new Button("Send");
        send.setPrefHeight(38);
        send.setPrefWidth(105);
        send.setDisable(true);
        send.setStyle("-fx-background-color: white;");

        chatinputbox.getChildren().addAll(chatinput, send);
        right.getChildren().addAll(container, chatinputbox);

        // prevent the user from sending an empty message
        chatinput.setOnKeyReleased(e -> {
            if (chatinput.getText().isEmpty()) {
                send.setDisable(true);
            } else {
                send.setDisable(false);
            }
        });

        send.setOnAction(e -> {
            if (!chatinput.getText().isEmpty()) {
                try {
                    sendMessage();
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        chatinput.setOnAction(e -> {
            if (!chatinput.getText().isEmpty()) {
                send.fire();
            }
        });
        loadCharacters();

        splitter.getChildren().addAll(left, right);
        Scene scene = new Scene(splitter, 1000, 750);
        primaryStage.setTitle("Chatbot");
        primaryStage.setResizable(false);
        primaryStage.getIcons().add(new Image("icon.jpeg"));
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void sendMessage() throws Exception {
        String userMessage = chatinput.getText();
        systemPrompt = "You are " + currentName + ". " + currentDescription; // describes the character behavior

        // add user message to history
        conversationHistory += ". User: " + userMessage;

        Text userPrompt = new Text("You: ");
        userPrompt.setFill(Color.rgb(230, 214, 173));

        Text userMessageText = new Text(userMessage + "\n");
        userMessageText.setFill(Color.WHITE);

        chat.getChildren().addAll(userPrompt, userMessageText);

        // get chatbot response and add it to history
        String[] botResponse = OllamaAPI.askOllama(currentModel, userMessage, conversationHistory, systemPrompt);
        if (botResponse[0] != null) {
            conversationHistory += ". ChatBot: " + botResponse[0];

            Text botPrompt = new Text(currentName.substring(0, 1).toUpperCase() + currentName.substring(1) + ": ");
            botPrompt.setFill(Color.rgb(173, 216, 230));

            Text botResponseText = new Text(botResponse[0] + "\n");
            botResponseText.setFill(Color.WHITE);

            chat.getChildren().addAll(botPrompt, botResponseText);
            chat.getChildren().add(new Text("\n"));

            // get audio response from chatbot through query string
            String text = URLEncoder.encode(botResponse[0], StandardCharsets.UTF_8);
            String speaker = "";

            // select voice depending on the gender
            if (currentGender.equals("Female")) {
                speaker = "en_0";
            } else {
                speaker = "en_1";
            }

            HttpClient client = HttpClient.newHttpClient();
            String url = "http://localhost:8000/generate?text=" + text + "&speaker=" + speaker;
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .build();

            Path outputPath = Paths.get("response.wav");
            HttpResponse<Path> response = client.send(request, HttpResponse.BodyHandlers.ofFile(outputPath));

            // play audio in a new thread so that the answer can be shown in the chat while the audio is playing
            new Thread(() -> {
                try (AudioInputStream audioIn = AudioSystem.getAudioInputStream(outputPath.toFile())) {
                    Clip clip = AudioSystem.getClip();
                    clip.open(audioIn);
                    clip.start();
                    Thread.sleep(clip.getMicrosecondLength() / 1000);
                } catch (UnsupportedAudioFileException | LineUnavailableException | InterruptedException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }).start();
            Platform.runLater(() -> scrollPane.setVvalue(1.0));
        } else {
            conversationHistory = "";
        }
        scrollPane.setVvalue(1.0);
        chatinput.clear();
    }
}