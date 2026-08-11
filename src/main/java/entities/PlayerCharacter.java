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

    public void update(double delta, boolean up, boolean down) {
        if (up) {
            y -= speed * delta;
        }
        if (down) {
            y += speed * delta;
        }

        x = Math.clamp(x, 0, 800 - width);
        y = Math.clamp(y, 0, 600 - height);
    }

    public void render(Graphics2D g2d) {
        g2d.setColor(Color.GREEN);
        g2d.fillRect((int) x, (int) y, width, height);
    }

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
