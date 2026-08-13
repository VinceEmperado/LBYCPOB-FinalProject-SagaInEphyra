package core;

import entities.EnemyController;
import entities.PlayerCharacter;

import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class GamePanel {
    private boolean up, down, left, right, slowDown;
    private PlayerCharacter playerCharacter;
    private EnemyController enemy;
    private final Canvas canvas;
    private final GraphicsContext graphicsContext;

    public GamePanel(PlayerCharacter playerCharacter, EnemyController enemy) {
        this.playerCharacter = playerCharacter;
        this.enemy = enemy;
        this.canvas = new Canvas(1600, 900);
        this.graphicsContext = canvas.getGraphicsContext2D();
        canvas.setFocusTraversable(true);
    }

    // This method takes the key input of the user and performs an action depending on which key is pressed or released.
    public void inputHandling(Scene scene) {
        scene.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case W -> up = true;
                case S -> down = true;
                case A -> left = true;
                case D -> right = true;
                default -> {}
            }
        });
        scene.setOnKeyReleased(e -> {
            switch (e.getCode()) {
                case W -> up = false;
                case S -> down = false;
                case A -> left = false;
                case D -> right = false;
                default -> {}
            }
        });
    }

    public void render() {
        graphicsContext.setFill(Color.BLACK);
        graphicsContext.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        playerCharacter.render(graphicsContext);
        enemy.render(graphicsContext);
    }

    // Getters for the methods that help the player entity move in the PlayerCharacter class
    public boolean isUp() {
        return up;
    }

    public boolean isDown() {
        return down;
    }

    public boolean isLeft() {
        return left;
    }

    public boolean isRight() {
        return right;
    }

    public boolean isSlowDown() {
        return slowDown;
    }

    public Canvas getCanvas() {
        return canvas;
    }

    public int getPanelWidth() {
        return (int) canvas.getWidth();
    }

    public int getPanelHeight() {
        return (int) canvas.getHeight();
    }
}
