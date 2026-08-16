package ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class MainMenu extends StackPane {

    public MainMenu(Runnable onStart, Runnable onExit) {
        setPrefSize(1600, 900);
        setStyle("-fx-background-color: black;");

        Text title = new Text("Saga in Ephyra");
        title.setFill(Color.WHITE);
        title.setFont(Font.font("Arial", FontWeight.BOLD, 20));

        Button startButton = new Button("Start");
        styleButton(startButton);
        startButton.setOnAction(e -> {
            if (onStart != null) {
                onStart.run();
            }
        });

        Button exitButton = new Button("Exit");
        styleButton(exitButton);
        exitButton.setOnAction(e -> {
            if (onExit != null) {
                onExit.run();
            }
        });

        VBox layout = new VBox(30, title, startButton, exitButton);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(40));

        getChildren().add(layout);
    }

    private void styleButton(Button button) {
        button.setFont(Font.font("Arial",  FontWeight.BOLD, 20));
        button.setPrefWidth(240);
        button.setPrefHeight(50);
        button.setStyle("-fx-background-color: #222222; -fx-text-fill: white; -fx-border-color: white; -fx-border-width: 2;");
    }
}
