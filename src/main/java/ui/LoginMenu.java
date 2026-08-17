package ui;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.function.BiConsumer;

public class LoginMenu extends VBox {

    private final BiConsumer<String, String> onLoginSubmit;
    private final Runnable onCloseAction;

    public LoginMenu(BiConsumer<String, String> onLoginSubmit, Runnable onCloseAction) {
        this.onLoginSubmit = onLoginSubmit;
        this.onCloseAction = onCloseAction;

        setAlignment(Pos.CENTER);
        setSpacing(20);
        setStyle("-fx-background-color: rgba(10, 10, 20, 0.95);");

        // Title Header
        Label titleLabel = new Label("PLAYER LOGIN");
        titleLabel.setFont(Font.font("Consolas", FontWeight.BOLD, 32));
        titleLabel.setTextFill(Color.web("#00ffcc"));

        // Form Container
        VBox formContainer = new VBox(10);
        formContainer.setAlignment(Pos.CENTER);

        // Username Field
        Label userLabel = new Label("USERNAME");
        userLabel.setFont(Font.font("Consolas", FontWeight.BOLD, 12));
        userLabel.setTextFill(Color.web("#8888aa"));

        TextField usernameField = new TextField();
        usernameField.setMaxWidth(260);
        usernameField.setFont(Font.font("Consolas", FontWeight.NORMAL, 14));
        usernameField.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-border-color: #00ffcc;" +
                        "-fx-border-width: 2px;" +
                        "-fx-text-fill: white;" +
                        "-fx-prompt-text-fill: #555577;" +
                        "-fx-alignment: center;"
        );
        usernameField.setPromptText("ENTER USERNAME");

        // Password Field
        Label passLabel = new Label("PASSWORD");
        passLabel.setFont(Font.font("Consolas", FontWeight.BOLD, 12));
        passLabel.setTextFill(Color.web("#8888aa"));

        PasswordField passwordField = new PasswordField();
        passwordField.setMaxWidth(260);
        passwordField.setFont(Font.font("Consolas", FontWeight.NORMAL, 14));
        passwordField.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-border-color: #00ffcc;" +
                        "-fx-border-width: 2px;" +
                        "-fx-text-fill: white;" +
                        "-fx-prompt-text-fill: #555577;" +
                        "-fx-alignment: center;"
        );
        passwordField.setPromptText("ENTER PASSWORD");

        // Status / Error Label
        Label statusLabel = new Label();
        statusLabel.setFont(Font.font("Consolas", FontWeight.NORMAL, 12));
        statusLabel.setTextFill(Color.web("#ff2a2a"));

        formContainer.getChildren().addAll(userLabel, usernameField, passLabel, passwordField, statusLabel);

        // Submit Login Button
        Button loginButton = createStyledButton("LOGIN", "#00ffcc", "#00ffcc", true, e -> {
            String username = usernameField.getText().trim();
            String password = passwordField.getText();

            if (username.isEmpty() || password.isEmpty()) {
                statusLabel.setText("PLEASE FILL IN ALL FIELDS");
            } else {
                statusLabel.setText("");
                if (this.onLoginSubmit != null) {
                    this.onLoginSubmit.accept(username, password);
                }
            }
        });

        // Back / Return Button
        Button backButton = createStyledButton("BACK", "#ff0055", "#ff0055", false, e -> {
            if (this.onCloseAction != null) {
                this.onCloseAction.run();
            }
        });

        getChildren().addAll(titleLabel, formContainer, loginButton, backButton);
    }

    private Button createStyledButton(String text, String borderColor, String hoverBgColor, boolean fillOnHover, javafx.event.EventHandler<javafx.event.ActionEvent> action) {
        Button button = new Button(text);
        button.setFont(Font.font("Consolas", FontWeight.BOLD, 14));

        String normalStyle =
                "-fx-background-color: transparent;" +
                        "-fx-border-color: " + borderColor + ";" +
                        "-fx-border-width: 2px;" +
                        "-fx-text-fill: white;" +
                        "-fx-padding: 8 25 8 25;";

        String hoverStyle = fillOnHover ?
                "-fx-background-color: " + hoverBgColor + ";" +
                "-fx-border-color: " + borderColor + ";" +
                "-fx-border-width: 2px;" +
                "-fx-text-fill: black;" +
                "-fx-padding: 8 25 8 25;" :
                "-fx-background-color: " + hoverBgColor + ";" +
                "-fx-border-color: " + borderColor + ";" +
                "-fx-border-width: 2px;" +
                "-fx-text-fill: white;" +
                "-fx-padding: 8 25 8 25;";

        button.setStyle(normalStyle);
        button.setOnMouseEntered(e -> button.setStyle(hoverStyle));
        button.setOnMouseExited(e -> button.setStyle(normalStyle));
        button.setOnAction(action);

        return button;
    }
}