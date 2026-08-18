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
    private DamagePacket damagePacket; // Damage payload field
    private double hitRadius = 3.0;
    private boolean grazed = false;

    public Bullet() {
        this.damagePacket = new DamagePacket(10.0); // Default fallback damage amount
    }

    public void spawn(double x, double y, double vx, double vy, Image sprite, Color fallbackColor) {
        spawn(x, y, vx, vy, 10.0, sprite, fallbackColor); // Overload with default damage
    }

    public void spawn(double x, double y, double vx, double vy, double damage, Image sprite, Color fallbackColor) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.setDamage(damage); // Assign or update damage value
        this.sprite = sprite;
        this.fallbackColor = fallbackColor;
        this.angle = Math.atan2(vy, vx); // Rotates bullet to face velocity direction
        this.active = true;
        this.setDamage(damage);
        this.damagePacket.setSourceX(x);
        this.damagePacket.setSourceY(y);
        this.grazed = false;
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
            gc.rotate(Math.toDegrees(angle));
            gc.drawImage(sprite, -w / 2, -h / 2);
        } else {
            // Fallback render for testing before image files exist
            int radius = 6;
            gc.setFill(fallbackColor);
            gc.fillOval((int) (x - radius), (int) (y - radius), radius * 2, radius * 2);
        }

        gc.restore();
    }

    public DamagePacket getDamagePacket() {
        return damagePacket;
    }

    public void setDamagePacket(DamagePacket damagePacket) {
        this.damagePacket = damagePacket;
    }

    public void setDamage(double amount) {
        if (this.damagePacket == null) {
            this.damagePacket = new DamagePacket(amount);
        } else {
            this.damagePacket.setAmount(amount);
        }
    }

    public double getDamage() {
        return damagePacket != null ? damagePacket.getAmount() : 0.0;
    }

    public double getRadius() {
        if (sprite != null) {
            return Math.max(sprite.getWidth(), sprite.getHeight()) / 2.0;
        }
        return 6.0; // Fallback radius
    }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public double getX() { return x; }
    public double getY() { return y; }
    public double getHitRadius() { return hitRadius; }
    public void setHitRadius(double hitRadius) { this.hitRadius = hitRadius; }
    public boolean isGrazed() {
        return grazed;
    }
    public void setGrazed(boolean grazed) {
        this.grazed = grazed;
    }
}