package ui;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.ArrayList;
import java.util.List;

public class LeaderboardMenu extends VBox {

    private static final List<ScoreEntry> scores = new ArrayList<>();

    public static class ScoreEntry {
        String name;
        int score;

        public ScoreEntry(String name, int score) {
            this.name = name;
            this.score = score;
        }
    }

    private boolean submitted = false;

    public LeaderboardMenu(int currentScore, Runnable onCloseAction) {
        setAlignment(Pos.CENTER);
        setSpacing(20);
        setStyle("-fx-background-color: rgba(10, 10, 20, 0.95);");

        Label titleLabel = new Label("HIGH SCORES");
        titleLabel.setFont(Font.font("Consolas", FontWeight.BOLD, 32));
        titleLabel.setTextFill(Color.web("#00ffcc"));

        // Score List Container
        VBox scoreListContainer = new VBox(8);
        scoreListContainer.setAlignment(Pos.CENTER);
        updateScoreListView(scoreListContainer);

        // Registration Input Section
        VBox inputSection = new VBox(10);
        inputSection.setAlignment(Pos.CENTER);

        Label promptLabel = new Label("ENTER YOUR NAME (" + currentScore + " PTS):");
        promptLabel.setFont(Font.font("Consolas", FontWeight.BOLD, 14));
        promptLabel.setTextFill(Color.WHITE);

        TextField nameField = new TextField();
        nameField.setMaxWidth(260);
        nameField.setFont(Font.font("Consolas", FontWeight.NORMAL, 14));
        nameField.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-border-color: #00ffcc;" +
                        "-fx-border-width: 2px;" +
                        "-fx-text-fill: white;" +
                        "-fx-prompt-text-fill: #555577;" +
                        "-fx-alignment: center;"
        );
        nameField.setPromptText("PLAYER NAME");

        Button submitButton = createStyledButton("REGISTER SCORE", () -> {
            if (!submitted) {
                String name = nameField.getText().trim();
                if (!name.isEmpty()) {
                    scores.add(new ScoreEntry(name.toUpperCase(), currentScore));
                    // Sort descending by score
                    scores.sort((a, b) -> Integer.compare(b.score, a.score));
                    submitted = true;

                    inputSection.getChildren().clear();
                    Label successLabel = new Label("SCORE REGISTERED!");
                    successLabel.setFont(Font.font("Consolas", FontWeight.BOLD, 14));
                    successLabel.setTextFill(Color.web("#00ffcc"));
                    inputSection.getChildren().add(successLabel);

                    updateScoreListView(scoreListContainer);
                }
            }
        });

        inputSection.getChildren().addAll(promptLabel, nameField, submitButton);

        // Back Button
        Button backButton = createStyledButton("BACK", onCloseAction);

        getChildren().addAll(titleLabel, scoreListContainer, submitted ? new VBox() : inputSection, backButton);
    }

    private void updateScoreListView(VBox container) {
        container.getChildren().clear();
        if (scores.isEmpty()) {
            Label emptyLabel = new Label("NO SCORES REGISTERED YET");
            emptyLabel.setFont(Font.font("Consolas", FontWeight.NORMAL, 14));
            emptyLabel.setTextFill(Color.web("#8888aa"));
            container.getChildren().add(emptyLabel);
        } else {
            int rank = 1;
            for (ScoreEntry entry : scores) {
                if (rank > 5) break; // Display top 5
                Label item = new Label(String.format("%d.  %-10s |  %09d", rank++, entry.name, entry.score));
                item.setFont(Font.font("Consolas", FontWeight.NORMAL, 16));
                item.setTextFill(Color.WHITE);
                container.getChildren().add(item);
            }
        }
    }

    private Button createStyledButton(String text, Runnable action) {
        Button button = new Button(text);
        button.setFont(Font.font("Consolas", FontWeight.BOLD, 14));
        button.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-border-color: #ff0055;" +
                        "-fx-border-width: 2px;" +
                        "-fx-text-fill: white;" +
                        "-fx-padding: 8 20 8 20;"
        );

        button.setOnMouseEntered(e -> button.setStyle(
                "-fx-background-color: #ff0055;" +
                        "-fx-border-color: #ff0055;" +
                        "-fx-border-width: 2px;" +
                        "-fx-text-fill: white;" +
                        "-fx-padding: 8 20 8 20;"
        ));

        button.setOnMouseExited(e -> button.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-border-color: #ff0055;" +
                        "-fx-border-width: 2px;" +
                        "-fx-text-fill: white;" +
                        "-fx-padding: 8 20 8 20;"
        ));

        button.setOnAction(e -> {
            if (action != null) action.run();
        });

        return button;
    }
}