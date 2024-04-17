package com.example.chatbot;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
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
    private List<String> characterInformation;
    // components
    private Button submit;
    private GridPane pane;
    private Text question;
    private TextField textfield;
    private VBox vbox;
    private GridPane grid;
    private VBox submitVbox;
    private VBox mainVbox;
    private Scene scene;
    private Image icon;
    private ImageView iconView;
    private ProgressBar progressBar;
    private int counter = 0;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws InterruptedException {
        pane = new GridPane();
        vbox = new VBox();
        mainVbox = new VBox();
        submitVbox = new VBox();
        textfield = new TextField();

        grid = new GridPane();
        submit = new Button("Submit");
        icon = new Image("bootup.jpeg");
        iconView = new ImageView(icon);
        question = new Text("What is the name of your chat partner?");
        characterInformation = new ArrayList<>();
        // styling
        iconView.setFitHeight(300);
        iconView.setFitWidth(300);
        pane.setAlignment(Pos.CENTER);


        RadioButton female = new RadioButton();
        female.setText("Female");
        RadioButton male = new RadioButton();
        male.setText("Male");
        female.setTextAlignment(TextAlignment.CENTER);
        male.setTextAlignment(TextAlignment.CENTER);
        progressBar = new ProgressBar();

        female.setOnAction(e-> {
            male.setSelected(false);
            submit.setDisable(false);

        });

        male.setOnAction(e-> {
            female.setSelected(false);
            submit.setDisable(false);
        });

        // disable submit button
        submit.setDisable(true);

        textfield.setOnKeyReleased(e -> {
            if (textfield.getText().isEmpty()) {
                submit.setDisable(true);
            } else {
                submit.setDisable(false);
            }
        });
        submit.setOnAction(e -> {
            if(counter == 0) {
                characterInformation.add(textfield.getText());
                question.setText("How would you describe your chat partner?");
                textfield.clear();
                submit.setDisable(true);

                counter++;
            } else if(counter == 1) {
                characterInformation.add(textfield.getText());
                textfield.clear();
                submit.setDisable(true);

                question.setText("Is your chat partner a female or male?");
                vbox.getChildren().addAll(female, male);
                grid.getChildren().removeIf( node -> textfield.equals(node));
                counter++;
        } else if(counter == 2) {
                submit.setDisable(true);

            characterInformation.add(textfield.getText());
            if(female.isSelected()) {
                characterInformation.add("Female");
            } else {
                characterInformation.add("Male");
            }

            submit.setVisible(false);
            question.setText("Thank you, your chat partner is being created...");
            vbox.getChildren().add(progressBar);
            female.setVisible(false);
            male.setVisible(false);

            new Thread(() -> {
                try {
                    TimeUnit.SECONDS.sleep(8);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }

                Platform.runLater(() -> {
                    ChatBot chatBot = new ChatBot(characterInformation);
                    try {
                        chatBot.start(primaryStage);
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                });
            }).start();
        }
    });

        // field + button
        grid.add(textfield, 0, 0);
        grid.setAlignment(Pos.CENTER);

        // submit button
        submitVbox.getChildren().add(submit);
        submitVbox.setAlignment(Pos.CENTER);

        // combine layouts
        vbox.getChildren().addAll(iconView, question);
        vbox.setAlignment(Pos.CENTER);
        vbox.setSpacing(3);

        pane.add(vbox, 0, 0);
        pane.add(grid, 0, 1);
        pane.add(submitVbox, 0, 5);
        pane.setVgap(15);
        mainVbox.getChildren().addAll(pane, submitVbox);
        mainVbox.setSpacing(30);
        mainVbox.setAlignment(Pos.CENTER);

        scene = new Scene(mainVbox, 800, 600);
        pane.setStyle("-fx-background-color: #eeeff1");
        vbox.setStyle("-fx-background-color: #eeeff1");
        mainVbox.setStyle("-fx-background-color: #eeeff1");

        primaryStage.setTitle("Chatbot");
        primaryStage.getIcons().add(new Image("bootup.jpeg"));
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
