package entities;

import java.awt.*;
import javax.imageio.ImageIO;
import java.io.IOException;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.image.Image;

public class PlayerCharacter {
    private double x, y;
    private double speed = 300;
    private int width = 20, height = 20;
    private Image playerSprite;

    public PlayerCharacter(double startX, double startY) {
        this.x = startX;
        this.y = startY;

        this.width = 40;
        this.height = 40;

        try {
            playerSprite = new Image(getClass().getResourceAsStream("/sprites/player/zany.jpg"));
        } catch (Exception e) {
            System.err.println("Could not load player sprite.");
        }
    }

    // Updates the position of the playable character.
    public void update(double delta, boolean up, boolean down, boolean left, boolean right, int panelWidth, int panelHeight, boolean slowDown) {
        if (slowDown) {
            this.speed = 200;
        } else {
            this.speed = 300;
        }
        if (up) {
            y -= speed * delta;
        }
        if (down) {
            y += speed * delta;
        }
        if (left) {
            x -= speed * delta;
        }
        if (right) {
            x += speed * delta;
        }

        x = Math.max(0, Math.min(x, 1600 - width));
        y = Math.max(0, Math.min(y, 900 - height));
    }

    // Renders the hitbox of the playable character in the window
    public void render(GraphicsContext gc) {
        if (playerSprite != null && !playerSprite.isError()) {
            gc.drawImage(playerSprite, (int) x, (int) y, width, height);
        } else {
            // Fallback if image cannot be found
            gc.setFill(Color.GREEN);
            gc.fillRect((int) x, (int) y, width, height);
        }
    }

    // Getters
    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getHeight() {
        return height;
    }

    public double getWidth() {
        return width;
    }
}