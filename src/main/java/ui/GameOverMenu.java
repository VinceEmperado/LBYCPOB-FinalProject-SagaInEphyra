package ui;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.function.Supplier;

public class GameOverMenu extends VBox {
    private final Label titleLabel;
    private final Button restartButton;
    private final Button leaderboardButton;
    private final Button menuButton;
    private final Supplier<Integer> scoreSupplier;

    public GameOverMenu(Runnable onRestart, Runnable onMenu) {
        this(onRestart, onMenu, () -> 0);
    }

    public GameOverMenu(Runnable onRestart, Runnable onMenu, Supplier<Integer> scoreSupplier) {
        this.scoreSupplier = scoreSupplier;

        setAlignment(Pos.CENTER);
        setSpacing(20);
        setStyle("-fx-background-color: rgba(0, 0, 0, 0.85);");
        setVisible(false);

        titleLabel = new Label();
        titleLabel.setFont(Font.font("Consolas", FontWeight.BOLD, 40));

        restartButton = createStyledButton("PLAY AGAIN", onRestart);

        leaderboardButton = createStyledButton("VIEW LEADERBOARD", () -> {
            if (getParent() instanceof StackPane root) {
                int currentScore = scoreSupplier != null ? scoreSupplier.get() : 0;

                final LeaderboardMenu[] menuHolder = new LeaderboardMenu[1];
                menuHolder[0] = new LeaderboardMenu(currentScore, () -> {
                    root.getChildren().remove(menuHolder[0]);
                });
                root.getChildren().add(menuHolder[0]);
            }
        });

        menuButton = createStyledButton("MAIN MENU", onMenu);

        getChildren().addAll(titleLabel, restartButton, leaderboardButton, menuButton);
    }

    public void show(boolean victory) {
        if (victory) {
            titleLabel.setText("VICTORY");
            titleLabel.setTextFill(Color.web("#00ffcc"));
        } else {
            titleLabel.setText("GAME OVER");
            titleLabel.setTextFill(Color.web("#ff2a2a"));
        }
        setVisible(true);
        toFront();
    }

    private Button createStyledButton(String text, Runnable action) {
        Button button = new Button(text);
        button.setFont(Font.font("Consolas", FontWeight.BOLD, 16));
        button.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-border-color: #00ffcc;" +
                        "-fx-border-width: 2px;" +
                        "-fx-text-fill: white;" +
                        "-fx-padding: 10 25 10 25;"
        );

        button.setOnMouseEntered(e -> button.setStyle(
                "-fx-background-color: #00ffcc;" +
                        "-fx-border-color: #00ffcc;" +
                        "-fx-border-width: 2px;" +
                        "-fx-text-fill: black;" +
                        "-fx-padding: 10 25 10 25;"
        ));

        button.setOnMouseExited(e -> button.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-border-color: #00ffcc;" +
                        "-fx-border-width: 2px;" +
                        "-fx-text-fill: white;" +
                        "-fx-padding: 10 25 10 25;"
        ));

        button.setOnAction(e -> {
            if (action != null) action.run();
        });

        return button;
    }
}