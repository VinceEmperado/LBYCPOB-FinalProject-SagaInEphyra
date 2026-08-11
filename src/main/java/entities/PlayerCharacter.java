package entities;

import java.awt.*;

public class PlayerCharacter {
    private double x, y;
    private double speed = 300;
    private int width = 20, height = 20;

    public PlayerCharacter(double startX, double startY) {
        this.x = startX;
        this.y = startY;
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

        x = Math.clamp(x, 0, panelWidth - width);
        y = Math.clamp(y, 0, panelHeight - height);
    }

    // Renders the hitbox of the playable character in the window
    public void render(Graphics2D g2d) {
        g2d.setColor(Color.GREEN);
        g2d.fillRect((int) x, (int) y, width, height);
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
