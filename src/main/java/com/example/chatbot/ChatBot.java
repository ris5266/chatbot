package com.example.chatbot;

import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

public class ChatBot extends Application {

    private TextArea conversationArea;
    private TextField userInputField;
    private Button sendButton;
    private ScrollPane scrollPane;
    private BorderPane root;
    private Image chatbotImage;
    private HBox chatBox;
    private Label promptLabel;
    private ImageView imageView;
    private HBox userInputBox;
    private VBox inputBox;
    private Scene scene;
    private StackPane chatArea;
    private String conversationHistory;
    private List<String> characterInformation;
    private String systemPrompt;

    public ChatBot() {
        this.characterInformation = new ArrayList<>();
    }

    public ChatBot(List<String> characterInformation) {
        this.characterInformation = characterInformation;
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        root = new BorderPane();
        root.setStyle("-fx-background-color: #fdfdfb;");
        root.setCenter(chatBox);
        HBox vbox2 = new HBox();
        conversationArea = new TextArea();
        conversationArea.setEditable(false);
        conversationArea.setPrefHeight(700);
        conversationArea.setPrefHeight(700);
        conversationArea.setFont(Font.font(16));
        conversationArea.setStyle("-fx-background-color: white; -fx-border-color: #cfe8f3; -fx-border-width: 3px;");
        conversationArea.setWrapText(true);

        scrollPane = new ScrollPane(conversationArea);
        scrollPane.setFitToWidth(true);
        scrollPane.setVvalue(1.0);
        chatArea = new StackPane(scrollPane);

        // Add vertical image on the right side
        chatbotImage = new Image("chatbot.png");
        imageView = new ImageView(chatbotImage);
        imageView.setPreserveRatio(true);
        imageView.setFitHeight(400);
        imageView.setFitWidth(400);

        chatBox = new HBox(10);
        chatBox.setAlignment(Pos.BOTTOM_CENTER);
        chatBox.getChildren().addAll(conversationArea, imageView);

        // User input
        promptLabel = new Label();
        userInputField = new TextField();
        userInputField.setPrefHeight(30);
        userInputField.setFont(Font.font(16));
        userInputField.setPrefWidth(820);

        sendButton = new Button("Send");
        sendButton.setDefaultButton(true);
        sendButton.setPrefHeight(35);
        sendButton.setPrefWidth(150);
        sendButton.setStyle("-fx-background-color: lightgrey");
        sendButton.setDisable(true);


        userInputField.setOnKeyReleased(e -> {
            if (userInputField.getText().isEmpty()) {
                sendButton.setDisable(true);
            } else {
                sendButton.setDisable(false);
            }
        });


        sendButton.setOnMouseEntered(e -> {
            sendButton.setStyle("-fx-background-color: #d38383");

        });

        sendButton.setOnMouseExited(e -> {
            sendButton.setStyle("-fx-background-color: lightgray");

        });

        userInputBox = new HBox(10);
        userInputBox.getChildren().addAll(promptLabel, userInputField, sendButton);

        inputBox = new VBox(10, chatBox, userInputBox);

        root.setCenter(inputBox);
        VBox menu = new VBox();
        vbox2.setSpacing(10);
        menu.setAlignment(Pos.CENTER);
        Button newCharacter = new Button("Create Character");
        newCharacter.setStyle("-fx-background-color: lightblue;");
        Button test = new Button("Peter");
        test.setStyle("-fx-background-color: white;");
        Button test1 = new Button("Klaus");
        test1.setStyle("-fx-background-color: white;");

        Button test2 = new Button("Tim");
        test2.setStyle("-fx-background-color: white;");


        newCharacter.setPrefWidth(250);
        test.setPrefWidth(250);
        test1.setPrefWidth(250);
        test2.setPrefWidth(250);
        VBox charactervbox = new VBox();
        charactervbox.getChildren().addAll(newCharacter, test, test1, test2);
        charactervbox.setPrefHeight(500);
        menu.getChildren().addAll(charactervbox);


        vbox2.setPrefWidth(500);
        menu.setStyle("-fx-background-color: white;");

        // settings button
        FlowPane settingspane = new FlowPane();
        settingspane.setAlignment(Pos.CENTER);
        Button settings = new Button("Settings");
        settings.setStyle("-fx-background-color: white");
        settingspane.getChildren().add(settings);
        settingspane.setPrefHeight(33);
        settingspane.setPrefWidth(201);
        settings.setPrefWidth(201);


        settings.setOnMouseEntered(e -> {
            settings.setStyle("-fx-background-color: lightblue");
        });
        settings.setOnMouseExited(e -> {
            settings.setStyle("-fx-background-color: white");
        });

        newCharacter.setOnMouseEntered(e -> {
            newCharacter.setStyle("-fx-background-color: #69bed9");
        });
        newCharacter.setOnMouseExited(e -> {
            newCharacter.setStyle("-fx-background-color: lightblue");
        });

        menu.getChildren().add(settingspane);

        vbox2.getChildren().addAll(menu, inputBox);

        scene = new Scene(vbox2, 1000, 750);

        primaryStage.setTitle("Chatbot");
        primaryStage.setResizable(false);
        primaryStage.getIcons().add(new Image("icon.jpg"));
        primaryStage.setScene(scene);
        primaryStage.show();

        sendButton.setOnAction(e -> {
            if (userInputField.getText().isEmpty()) {
                try {
                    sendMessage();
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
    }

    private void sendMessage() throws Exception {
        String userMessage = userInputField.getText();

        systemPrompt = "You are " + characterInformation.get(0) + ". " + characterInformation.get(2);

        // Add user message to history
        conversationHistory += ". User: " + userMessage;

        conversationArea.appendText("You: " + userMessage + "\n");

        // Get bot response and add it to history
        String[] botResponse = OllamaAPI.askOllama(userMessage, conversationHistory, systemPrompt);
        conversationHistory += ". ChatBot: " + botResponse[0];

        conversationArea.appendText(characterInformation.get(0).substring(0, 1).toUpperCase() + characterInformation.get(0).substring(1) + ": " + botResponse[0] + "\n");

        conversationArea.appendText("\n");

        scrollPane.setVvalue(1.0);

        userInputField.clear();
    }
}


