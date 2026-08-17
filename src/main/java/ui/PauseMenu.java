package ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class PauseMenu extends VBox {

    private Runnable onResume;
    private Runnable onSave;
    private Runnable onRestart;
    private Runnable onQuit;

    public PauseMenu() {
        this(null, null, null, null);
    }

    public PauseMenu(Runnable onResume, Runnable onRestart, Runnable onQuit) {
        this(onResume, null, onRestart, onQuit);
    }

    public PauseMenu(Runnable onResume, Runnable onSave, Runnable onRestart, Runnable onQuit) {
        this.onResume = onResume;
        this.onSave = onSave;
        this.onRestart = onRestart;
        this.onQuit = onQuit;

        // Container setup
        setSpacing(12);
        setPadding(new Insets(25, 30, 25, 30));
        setAlignment(Pos.CENTER);
        setMaxWidth(280);
        setMaxHeight(370); // Expanded height to fit 4 buttons nicely

        // Styling matching game UI (neon/dark theme)
        setStyle("-fx-background-color: rgba(10, 10, 20, 0.95); " +
                "-fx-border-color: #00ffcc; " +
                "-fx-border-width: 2px; " +
                "-fx-border-radius: 8px; " +
                "-fx-background-radius: 8px; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0, 255, 204, 0.35), 15, 0, 0, 0);");

        // Title Label
        Label titleLabel = new Label("GAME PAUSED");
        titleLabel.setStyle("-fx-text-fill: #00ffcc; -fx-font-weight: bold; -fx-font-size: 20px;");

        // Action Buttons
        Button resumeBtn = createMenuButton("RESUME", "#00cc66", () -> {
            if (this.onResume != null) this.onResume.run();
        });

        Button saveBtn = createMenuButton("SAVE GAME", "#ffcc00", () -> {
            if (this.onSave != null) this.onSave.run();
        });

        Button restartBtn = createMenuButton("RESTART", "#0099ff", () -> {
            if (this.onRestart != null) this.onRestart.run();
        });

        Button quitBtn = createMenuButton("QUIT TO MENU", "#ff3366", () -> {
            if (this.onQuit != null) this.onQuit.run();
        });

        getChildren().addAll(titleLabel, resumeBtn, saveBtn, restartBtn, quitBtn);
    }

    private Button createMenuButton(String text, String accentColor, Runnable action) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setPrefHeight(38);

        String idleStyle = "-fx-background-color: transparent; " +
                "-fx-text-fill: white; " +
                "-fx-font-weight: bold; " +
                "-fx-font-size: 13px; " +
                "-fx-border-color: " + accentColor + "; " +
                "-fx-border-width: 1.5px; " +
                "-fx-border-radius: 4px; " +
                "-fx-background-radius: 4px;";

        String hoverStyle = "-fx-background-color: " + accentColor + "; " +
                "-fx-text-fill: #000000; " +
                "-fx-font-weight: bold; " +
                "-fx-font-size: 13px; " +
                "-fx-border-color: " + accentColor + "; " +
                "-fx-border-width: 1.5px; " +
                "-fx-border-radius: 4px; " +
                "-fx-background-radius: 4px;";

        btn.setStyle(idleStyle);
        btn.setOnMouseEntered(e -> btn.setStyle(hoverStyle));
        btn.setOnMouseExited(e -> btn.setStyle(idleStyle));

        btn.setOnAction(e -> {
            if (action != null) action.run();
        });

        return btn;
    }

    public void setOnResume(Runnable onResume) {
        this.onResume = onResume;
    }

    public void setOnSave(Runnable onSave) {
        this.onSave = onSave;
    }

    public void setOnRestart(Runnable onRestart) {
        this.onRestart = onRestart;
    }

    public void setOnQuit(Runnable onQuit) {
        this.onQuit = onQuit;
    }
}