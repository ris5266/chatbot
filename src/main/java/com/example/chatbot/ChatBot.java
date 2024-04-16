package com.example.chatbot;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

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
    private List<String> conversationHistory = new ArrayList<>();
    private List<String> characterInformation;
    private String systemPrompt;

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
        imageView.setFitHeight(700);
        imageView.setFitWidth(500);

        chatBox = new HBox(10);
        chatBox.getChildren().addAll(conversationArea, imageView);

        // User input
        promptLabel = new Label();
        userInputField = new TextField();
        userInputField.setPrefHeight(30);
        userInputField.setFont(Font.font(16));
        userInputField.setPrefWidth(920);

        sendButton = new Button("Send");
        sendButton.setDefaultButton(true);
        sendButton.setPrefHeight(35);
        sendButton.setPrefWidth(55);
        sendButton.setStyle("-fx-background-color: lightgrey");


        userInputBox = new HBox(10);
        userInputBox.getChildren().addAll(promptLabel, userInputField, sendButton);

        inputBox = new VBox(10, chatBox, userInputBox);

        root.setCenter(inputBox);

        scene = new Scene(root, 1000, 750);

        primaryStage.setTitle("Chatbot");
        primaryStage.setResizable(false);
        primaryStage.getIcons().add(new Image("icon.jpg"));
        primaryStage.setScene(scene);
        primaryStage.show();

        sendButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    sendMessage();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });

        userInputField.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    sendMessage();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private void sendMessage() throws Exception {
        String userMessage = userInputField.getText();

        systemPrompt = "You are " + characterInformation.get(0) + ". " + characterInformation.get(2);

        // Add user message to history
        conversationHistory.add(userMessage);

        conversationArea.appendText("You: " + userMessage + "\n");

        // Get bot response and add it to history
        String[] botResponse = OllamaAPI.askOllama(userMessage, conversationHistory, systemPrompt);
        conversationHistory.add(botResponse[0]);



        conversationArea.appendText(characterInformation.get(0).substring(0, 1).toUpperCase() + characterInformation.get(0).substring(1) + ": " + botResponse[0] + "\n");

        conversationArea.appendText("\n");

        scrollPane.setVvalue(1.0);

        // Clear user input
        userInputField.clear();
    }
}


