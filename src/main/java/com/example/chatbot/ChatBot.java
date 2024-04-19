package com.example.chatbot;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.json.JSONObject;

public class ChatBot extends Application {
    private String currentName;
    private String currentDescription;
    private TextArea chat;
    private TextField chatinput;
    private ScrollPane scrollPane;
    private String conversationHistory;
    private String systemPrompt;
    private VBox charactervbox;

    public static void main(String[] args) {
        launch(args);
    }

    public ChatBot() {
        JSONObject characters = JSONReader.readCharacters();

        // Get the first character from the characters object
        String firstKey = characters.keys().next();
        JSONObject firstCharacter = characters.getJSONObject(firstKey);

        // Get the name and description of the first character
        currentName = firstCharacter.getString("name");
        currentDescription = firstCharacter.getString("description");
    }

    private void loadCharacters() {
        JSONObject characters = JSONReader.readCharacters();

        // create button for each character
        for (String key : characters.keySet()) {
            JSONObject character = characters.getJSONObject(key);
            String name = character.getString("name").substring(0, 1).toUpperCase() + character.getString("name").substring(1);

            Button characterButton = new Button(name);
            characterButton.setStyle("-fx-background-color: #1d1d1d;");
            characterButton.setPrefHeight(33);
            characterButton.setPrefWidth(107);

            characterButton.setOnMouseEntered(e -> {
                characterButton.setStyle("-fx-background-color: #1d1d1d; -fx-text-fill: #e5e5e5;");
            });
            characterButton.setOnMouseExited(e -> {
                characterButton.setStyle("-fx-background-color: #1d1d1d;");
            });

            characterButton.setOnAction(e -> {
                chat.clear();
                conversationHistory = "";
                systemPrompt = "You are " + name + ". " + character.getString("description");
                currentDescription = character.getString("description");
                currentName = name;
            });

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

            // Show the context menu when the character button is right-clicked
            characterButton.setOnContextMenuRequested(e -> {
                contextMenu.show(characterButton, e.getScreenX(), e.getScreenY());
            });
            charactervbox.getChildren().add(characterButton);
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        HBox splitter = new HBox();
        splitter.setStyle("-fx-background-color: #121212;");
        splitter.setPrefHeight(750);
        splitter.setPrefWidth(1000);
        splitter.setSpacing(20);
        VBox left = new VBox();
        left.setStyle("-fx-background-color:  #1d1d1d;");
        left.setPrefHeight(571);
        left.setPrefWidth(107);
        left.setPadding(new Insets(10, 10, 10, 10));

        FlowPane placeholder = new FlowPane();
        placeholder.setPrefHeight(510);
        placeholder.setPrefWidth(107);
        left.getChildren().add(placeholder);
        charactervbox = new VBox();
        charactervbox.setPrefHeight(742);
        charactervbox.setPrefWidth(107);
        Button newCharacter = new Button("Create Character");
        newCharacter.setStyle("-fx-background-color: #1d1d1d; -fx-text-fill: #e5e5e5");
        newCharacter.setPrefHeight(33);
        newCharacter.setPrefWidth(107);
        newCharacter.setOnAction(e -> {
            Stage newCharacterStage = new Stage();
            newCharacterStage.setTitle("Create Character");
            newCharacterStage.getIcons().add(new Image("icon.jpeg"));
            newCharacterStage.initOwner(primaryStage);
            newCharacterStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);

            // input & submit
            Label nameLabel = new Label("Character name:");
            TextField nameField = new TextField();
            Label descriptionLabel = new Label("Character description:");
            TextArea descriptionField = new TextArea();

            RadioButton femaleButton = new RadioButton("Female");
            RadioButton maleButton = new RadioButton("Male");
            ToggleGroup genderGroup = new ToggleGroup();
            femaleButton.setToggleGroup(genderGroup);
            maleButton.setToggleGroup(genderGroup);

            Button submitButton = new Button("Submit");

            // submit action
            submitButton.setOnAction(event -> {
                String name = nameField.getText();
                String description = descriptionField.getText();
                String gender = ((RadioButton) genderGroup.getSelectedToggle()).getText();

                JSONReader.writeCharacter(name, description, gender);

                // update current scene
                ChatBot chatBot = new ChatBot();
                try {
                    chatBot.start(primaryStage);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
                newCharacterStage.close();
            });

            VBox formLayout = new VBox(10, nameLabel, nameField, descriptionLabel, descriptionField, femaleButton, maleButton, submitButton);
            formLayout.setPadding(new Insets(10));

            newCharacterStage.setScene(new Scene(formLayout));
            newCharacterStage.setResizable(false);
            newCharacterStage.show();
        });

        charactervbox.getChildren().addAll(newCharacter);
        FlowPane settingspane = new FlowPane();
        settingspane.setPadding(new Insets(0, 0, 5, 0));
        Button settings = new Button("Settings");
        settings.setStyle("-fx-background-color: #1d1d1d; -fx-text-fill:  #e5e5e5;");
        settingspane.getChildren().add(settings);
        settings.setPrefHeight(33);
        settings.setPrefWidth(107);
        left.getChildren().addAll(charactervbox, settingspane);

        VBox right = new VBox();
        right.setPrefHeight(689);
        right.setPrefWidth(855);

        chat = new TextArea();
        chat.setPrefHeight(794);
        chat.setPrefWidth(855);
        chat.setWrapText(true);
        chat.setEditable(false);
        chat.setStyle("-fx-font-size: 15px; -fx-font-family: 'Arial'");

        scrollPane = new ScrollPane(chat);
        scrollPane.setFitToWidth(true);
        scrollPane.setVvalue(1.0);

        VBox container = new VBox(chat);
        VBox.setMargin(chat, new Insets(20, 50, 20, 0)); // top, right, bottom, left margins

        HBox chatinputbox = new HBox();
        chatinputbox.setSpacing(10);
        chatinputbox.setPadding(new Insets(5, 5, 10, 5));
        chatinputbox.setPrefHeight(35);
        chatinputbox.setPrefWidth(895);

        chatinput = new TextField();
        chatinput.setPrefHeight(31);
        chatinput.setPrefWidth(788);
        Button send = new Button("Send");
        send.setPrefHeight(38);
        send.setPrefWidth(105);

        send.setDisable(true);

        send.setStyle("-fx-background-color: white;");
        chatinputbox.getChildren().addAll(chatinput, send);
        right.getChildren().addAll(container, chatinputbox);

        chatinput.setOnKeyReleased(e -> {
            if (chatinput.getText().isEmpty()) {
                send.setDisable(true);
            } else {
                send.setDisable(false);
            }
        });

        send.setOnAction(e -> {
            System.out.println("hi");
            if (!chatinput.getText().isEmpty()) {
                try {
                    sendMessage();
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

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

        chatinput.setOnAction(e -> {
            if (!chatinput.getText().isEmpty()) {
                send.fire();
            }
        });

        loadCharacters();

        splitter.getChildren().addAll(left, right);
        Scene scene = new Scene(splitter, 1000, 750);
        primaryStage.setTitle("ChatBot");
        primaryStage.setResizable(false);
        primaryStage.getIcons().add(new Image("icon.jpeg"));

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void sendMessage() throws Exception {
        JSONObject characters = JSONReader.readCharacters();
        String userMessage = chatinput.getText();

        systemPrompt = "You are " + currentName + ". " + currentDescription;

        // add user message to history
        conversationHistory += ". User: " + userMessage;

        chat.appendText("You: " + userMessage + "\n");

        // get chatbot response and add it to history
        String[] botResponse = OllamaAPI.askOllama(userMessage, conversationHistory, systemPrompt);
        conversationHistory += ". ChatBot: " + botResponse[0];
        if (botResponse[0] == null) {
            conversationHistory = "";
        }

        chat.appendText(currentName.substring(0, 1).toUpperCase() + currentName.substring(1) + ": " + botResponse[0] + "\n");
        chat.appendText("\n");

        scrollPane.setVvalue(1.0);
        chatinput.clear();
    }
}


