package combat;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class Bullet {
    private double x, y;
    private double vx, vy;
    private double angle;
    private boolean active = false;
    private Image sprite;
    private Color fallbackColor = Color.RED;

    public Bullet() {}

    public void spawn(double x, double y, double vx, double vy, Image sprite, Color fallbackColor) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.sprite = sprite;
        this.fallbackColor = fallbackColor;
        this.angle = Math.atan2(vy, vx); // Rotates bullet to face velocity direction
        this.active = true;
    }

    public void update(double delta, double panelWidth, double panelHeight) {
        if (!active) return;

        x += vx * delta;
        y += vy * delta;

        // Despawn bullet if it leaves screen boundaries
        if (x < -50 || x > panelWidth + 50 || y < -50 || y > panelHeight + 50) {
            active = false;
        }
    }

    public void render(GraphicsContext gc) {
        if (!active) return;

        gc.save();

        if (sprite != null) {
            double w = sprite.getWidth();
            double h = sprite.getHeight();

            // Center image at (x, y) and rotate according to direction
            gc.translate(x, y);
            gc.rotate(angle);
            gc.drawImage(sprite, -w / 2, -h / 2);
        } else {
            // Fallback render for testing before image files exist
            int radius = 6;
            gc.setFill(fallbackColor);
            gc.fillOval((int) (x - radius), (int) (y - radius), radius * 2, radius * 2);
        }

        gc.restore();
    }

    // Getters & Setters
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public double getX() { return x; }
    public double getY() { return y; }
}