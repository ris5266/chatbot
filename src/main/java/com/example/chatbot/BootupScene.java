package com.example.chatbot;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class BootupScene extends Application {
    private Button submit;
    private GridPane pane;
    private Text downloadLabel;
    private VBox vbox;
    private GridPane grid;
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
        File configFile = new File("config.json");
        if (configFile.exists() && !configFile.isDirectory()) {
            // If config.json exists, start the ChatBot scene
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
            HBox hbox = new HBox();
            hbox.setAlignment(Pos.CENTER);

            ProgressBar progressBar = new ProgressBar();
            Button nameSubmit = new Button("Submit");
            hbox.getChildren().addAll(textfield, nameSubmit);
            hbox.setSpacing(10);
            nameSubmit.setStyle("-fx-background-color: white");
            submit.setStyle("-fx-background-color: white");
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

            finishCharacter.setOnAction(e -> {
                if (genderGroup.getSelectedToggle() != null) {
                    String gender = ((RadioButton) genderGroup.getSelectedToggle()).getText();
                    hbox.getChildren().remove(femaleButton);
                    hbox.getChildren().remove(maleButton);
                    hbox.getChildren().remove(finishCharacter);
                    hbox.getChildren().add(progressBar);
                    downloadLabel.setText("Hold on, your character is being created...");
                    JSONReader.createCharacter(name, description, gender);
                    new Thread(() -> {
                        try {
                            // Sleep for 5 seconds
                            Thread.sleep(6000);

                            // Update the UI on the JavaFX Application Thread
                            Platform.runLater(() -> {
                                ChatBot chatBot = new ChatBot();
                                try {
                                    chatBot.start(new Stage());
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

            // styling
            iconView.setFitHeight(300);
            iconView.setFitWidth(300);

            // submit button
            submitVbox.getChildren().add(submit);
            submitVbox.setAlignment(Pos.CENTER);

            // combine layouts
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
