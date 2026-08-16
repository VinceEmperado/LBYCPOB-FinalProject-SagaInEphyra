package ui;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class GameOverMenu extends VBox {
    private final Label titleLabel;
    private final Label subtitleLabel;
    private final Button restartButton;
    private final Button exitButton;

    public GameOverMenu(Runnable onRestart, Runnable onExit) {
        // Overlay styling
        this.setSpacing(20);
        this.setAlignment(Pos.CENTER);
        this.setStyle("-fx-background-color: rgba(0, 0, 0, 0.85); -fx-padding: 40;");
        this.setVisible(false); // Hidden during active gameplay

        // Title text
        titleLabel = new Label("GAME OVER");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 48));
        titleLabel.setTextFill(Color.RED);

        // Subtitle text
        subtitleLabel = new Label("You ran out of lives!");
        subtitleLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 20));
        subtitleLabel.setTextFill(Color.WHITE);

        // Buttons
        restartButton = createStyledButton("Try Again");
        restartButton.setOnAction(e -> {
            hide();
            if (onRestart != null) onRestart.run();
        });

        exitButton = createStyledButton("Exit");
        exitButton.setOnAction(e -> {
            if (onExit != null) onExit.run();
        });

        this.getChildren().addAll(titleLabel, subtitleLabel, restartButton, exitButton);
    }

    public void show(boolean victory) {
        if (victory) {
            titleLabel.setText("VICTORY!");
            titleLabel.setTextFill(Color.GOLD);
            subtitleLabel.setText("You defeated the boss!");
        } else {
            titleLabel.setText("GAME OVER");
            titleLabel.setTextFill(Color.RED);
            subtitleLabel.setText("You ran out of lives!");
        }
        this.setVisible(true);
    }

    public void hide() {
        this.setVisible(false);
    }

    private Button createStyledButton(String text) {
        Button btn = new Button(text);
        btn.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        btn.setPrefWidth(200);
        btn.setStyle(
                "-fx-background-color: #222222;" +
                        "-fx-text-fill: white;" +
                        "-fx-border-color: #ffffff;" +
                        "-fx-border-radius: 5;" +
                        "-fx-background-radius: 5;" +
                        "-fx-cursor: hand;"
        );
        btn.setOnMouseEntered(e -> btn.setStyle(
                "-fx-background-color: #444444;" +
                        "-fx-text-fill: white;" +
                        "-fx-border-color: #ffffff;" +
                        "-fx-border-radius: 5;" +
                        "-fx-background-radius: 5;" +
                        "-fx-cursor: hand;"
        ));
        btn.setOnMouseExited(e -> btn.setStyle(
                "-fx-background-color: #222222;" +
                        "-fx-text-fill: white;" +
                        "-fx-border-color: #ffffff;" +
                        "-fx-border-radius: 5;" +
                        "-fx-background-radius: 5;" +
                        "-fx-cursor: hand;"
        ));
        return btn;
    }
}