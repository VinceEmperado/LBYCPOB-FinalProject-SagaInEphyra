package combat;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class Bullet {
    private double x, y;
    private double vx, vy;
    private double angle;
    private boolean active = false;
    private BufferedImage sprite;
    private Color fallbackColor = Color.RED;

    public Bullet() {}

    public void spawn(double x, double y, double vx, double vy, BufferedImage sprite, Color fallbackColor) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.sprite = sprite;
        this.fallbackColor = fallbackColor;
        this.angle = Math.atan2(vy, vx); // Rotates bullet to face velocity direction
        this.active = true;
    }

    public void update(double delta, int panelWidth, int panelHeight) {
        if (!active) return;

        x += vx * delta;
        y += vy * delta;

        // Despawn bullet if it leaves screen boundaries
        if (x < -50 || x > panelWidth + 50 || y < -50 || y > panelHeight + 50) {
            active = false;
        }
    }

    public void render(Graphics2D g2d) {
        if (!active) return;

        AffineTransform oldTransform = g2d.getTransform();

        if (sprite != null) {
            int w = sprite.getWidth();
            int h = sprite.getHeight();

            // Center image at (x, y) and rotate according to direction
            g2d.translate(x, y);
            g2d.rotate(angle);
            g2d.drawImage(sprite, -w / 2, -h / 2, null);
        } else {
            // Fallback render for testing before image files exist
            int radius = 6;
            g2d.setColor(fallbackColor);
            g2d.fillOval((int) (x - radius), (int) (y - radius), radius * 2, radius * 2);
        }

        g2d.setTransform(oldTransform);
    }

    // Getters & Setters
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public double getX() { return x; }
    public double getY() { return y; }
}