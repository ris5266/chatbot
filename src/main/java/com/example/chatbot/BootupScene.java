package com.example.chatbot;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;

public class BootupScene extends Application {
    private Button submit;
    private Text downloadLabel;
    private VBox vbox;
    private VBox submitVbox;
    private VBox mainVbox;
    private Scene scene;
    private Image icon;
    private ImageView iconView;
    private String name;
    private String description;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // check if config.json exists
        File configFile = new File("config.json");
        if (configFile.exists() && !configFile.isDirectory()) {
            // if yes skip the bootup scene
            ChatBot chatBot = new ChatBot();
            chatBot.start(primaryStage);
        } else {
            vbox = new VBox();
            mainVbox = new VBox();
            submitVbox = new VBox();

            submit = new Button("I already installed everything. Continue.");
            icon = new Image("bootup.jpg");
            iconView = new ImageView(icon);
            downloadLabel = new Text("Welcome! Before you can start chatting, you need to have \n followed the install instructions from the READ.ME file. ");
            downloadLabel.setStyle("-fx-fill: #e5e5e5;");
            TextField textfield = new TextField();
            textfield.setPrefWidth(200);
            ProgressBar progressBar = new ProgressBar();
            Button nameSubmit = new Button("Submit");
            Button descriptionSubmit = new Button("Submit");
            descriptionSubmit.setStyle("-fx-background-color: white");
            RadioButton femaleButton = new RadioButton("Female");
            femaleButton.setStyle("-fx-text-fill: #e5e5e5");
            RadioButton maleButton = new RadioButton("Male");
            maleButton.setStyle("-fx-text-fill: #e5e5e5");
            ToggleGroup genderGroup = new ToggleGroup();
            femaleButton.setToggleGroup(genderGroup);
            maleButton.setToggleGroup(genderGroup);
            Button finishCharacter = new Button("Create Character");
            finishCharacter.setStyle("-fx-background-color: white");

            HBox hbox = new HBox();
            hbox.setAlignment(Pos.CENTER);
            hbox.getChildren().addAll(textfield, nameSubmit);
            hbox.setSpacing(10);
            nameSubmit.setStyle("-fx-background-color: white");
            submit.setStyle("-fx-background-color: white");

            // event handlers to change questions and show or hide inputs
            finishCharacter.setOnAction(e -> {
                if (genderGroup.getSelectedToggle() != null) {
                    String gender = ((RadioButton) genderGroup.getSelectedToggle()).getText();
                    hbox.getChildren().remove(femaleButton);
                    hbox.getChildren().remove(maleButton);
                    hbox.getChildren().remove(finishCharacter);
                    hbox.getChildren().add(progressBar);
                    downloadLabel.setText("Hold on, your character is being created...");
                    JSONReader.createCharacter(name, description, gender);

                    JSONObject config;
                    try {
                        // Read the existing JSON file
                        config = new JSONObject(new String(Files.readAllBytes(Paths.get("config.json"))));
                    } catch (NoSuchFileException ex) {
                        // If the file does not exist, create a new one with an empty JSON object
                        config = new JSONObject();
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }

                    // Add default model value
                    config.put("model", "llama3");

                    // Write the modified JSONObject back to the JSON file
                    try (FileWriter file = new FileWriter("config.json")) {
                        file.write(config.toString(4));
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }

                    // thread to simulate a progress bar
                    new Thread(() -> {
                        try {
                            Thread.sleep(6000);
                            Platform.runLater(() -> {
                                ChatBot chatBot = new ChatBot();
                                try {
                                    chatBot.start(new Stage());
                                    primaryStage.close();
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                            });
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                    }).start();
                }
            });

            descriptionSubmit.setOnAction(e -> {
                description = textfield.getText();
                downloadLabel.setText("Very good! Would you consider " + name.substring(0, 1).toUpperCase() + name.substring(1) + " a male or a female?");
                hbox.getChildren().remove(descriptionSubmit);
                hbox.getChildren().remove(textfield);
                hbox.getChildren().addAll(femaleButton, maleButton);
                hbox.getChildren().add(finishCharacter);
            });

            nameSubmit.setOnAction(e -> {
                name = textfield.getText();
                downloadLabel.setText("Great name! How would " + name.substring(0, 1).toUpperCase() + name.substring(1) + " personality look like?");
                textfield.clear();
                hbox.getChildren().remove(nameSubmit);
                hbox.getChildren().add(descriptionSubmit);
            });

            submit.setOnAction(e -> {
                mainVbox.getChildren().remove(submitVbox);
                mainVbox.getChildren().add(progressBar);
                downloadLabel.setText("Great! Lets continue to creating your first chatbot...");

                // thread to simulate a progress bar
                new Thread(() -> {
                    try {
                        Thread.sleep(6000);
                        Platform.runLater(() -> {
                            downloadLabel.setText("What do you want to name your new chatbot?");
                            mainVbox.getChildren().remove(progressBar);
                            mainVbox.getChildren().add(hbox);
                        });
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }).start();
            });

            iconView.setFitHeight(300);
            iconView.setFitWidth(300);
            submitVbox.getChildren().add(submit);
            submitVbox.setAlignment(Pos.CENTER);

            vbox.getChildren().addAll(iconView, downloadLabel);
            vbox.setAlignment(Pos.CENTER);
            vbox.setSpacing(5);
            vbox.setStyle("-fx-background-color: #121212");

            mainVbox.getChildren().addAll(vbox, submitVbox);
            mainVbox.setSpacing(30);
            mainVbox.setAlignment(Pos.CENTER);
            mainVbox.setStyle("-fx-background-color: #121212");

            scene = new Scene(mainVbox, 800, 600);
            primaryStage.setTitle("Chatbot");
            primaryStage.getIcons().add(new Image("bootup.jpg"));
            primaryStage.setScene(scene);
            primaryStage.show();
        }
    }
}
