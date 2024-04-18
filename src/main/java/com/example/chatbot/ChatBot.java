package com.example.chatbot;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.List;

public class ChatBot extends Application {

    private TextArea chat;
    private TextField chatinput;
    private ScrollPane scrollPane;
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
        VBox charactervbox = new VBox();
        charactervbox.setPrefHeight(742);
        charactervbox.setPrefWidth(107);
        Button newCharacter = new Button("Create Character");
        newCharacter.setStyle("-fx-background-color: #1d1d1d; -fx-text-fill: #e5e5e5");
        newCharacter.setPrefHeight(33);
        newCharacter.setPrefWidth(107);
        Button testcharacter = new Button("Peter");
        testcharacter.setStyle("-fx-background-color: #1d1d1d;");
        testcharacter.setPrefHeight(33);
        testcharacter.setPrefWidth(107);
        Button testcharacter1 = new Button("Klaus");
        testcharacter1.setStyle("-fx-background-color: #1d1d1d;");
        testcharacter1.setPrefHeight(33);
        testcharacter1.setPrefWidth(107);
        charactervbox.getChildren().addAll(newCharacter, testcharacter, testcharacter1);
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

        testcharacter.setOnMouseEntered(e -> {
            testcharacter.setStyle("-fx-background-color: #1d1d1d; -fx-text-fill: #e5e5e5;");
        });
        testcharacter.setOnMouseExited(e -> {
            testcharacter.setStyle("-fx-background-color: #1d1d1d;");
        });

        testcharacter1.setOnMouseEntered(e -> {
            testcharacter1.setStyle("-fx-background-color: #1d1d1d; -fx-text-fill: #e5e5e5;");
        });
        testcharacter1.setOnMouseExited(e -> {
            testcharacter1.setStyle("-fx-background-color: #1d1d1d;");
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

        splitter.getChildren().addAll(left, right);
        Scene scene = new Scene(splitter, 1000, 750);
        primaryStage.setTitle("ChatBot");
        primaryStage.getIcons().add(new Image("icon.jpeg"));

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void sendMessage() throws Exception {
        String userMessage = chatinput.getText();

//        systemPrompt = "You are Peter a virtual chatbot";
        systemPrompt = "You are " + characterInformation.get(0) + ". " + characterInformation.get(2);

        // Add user message to history
        conversationHistory += ". User: " + userMessage;

        chat.appendText("You: " + userMessage + "\n");

        // Get bot response and add it to history
        String[] botResponse = OllamaAPI.askOllama(userMessage, conversationHistory, systemPrompt);
        conversationHistory += ". ChatBot: " + botResponse[0];
        if(botResponse[0] == null) {
            conversationHistory = "";
        }


        chat.appendText(characterInformation.get(0).substring(0, 1).toUpperCase() + characterInformation.get(0).substring(1) + ": " + botResponse[0] + "\n");

        chat.appendText("\n");

        scrollPane.setVvalue(1.0);

        chatinput.clear();
    }
}


