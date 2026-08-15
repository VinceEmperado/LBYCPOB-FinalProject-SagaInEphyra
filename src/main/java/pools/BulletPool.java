package pools;

import combat.Bullet;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import java.util.ArrayList;
import java.util.List;

public class BulletPool {
    private final List<Bullet> pool = new ArrayList<>();

    public BulletPool(int initialCapacity) {
        for (int i = 0; i < initialCapacity; i++) {
            pool.add(new Bullet());
        }
    }

    public Bullet spawnBullet(double x, double y, double vx, double vy, Image sprite, Color fallbackColor) {
        for (Bullet b : pool) {
            if (!b.isActive()) {
                b.spawn(x, y, vx, vy, sprite, fallbackColor);
                return b;
            }
        }
        // Expand pool if capacity is exceeded
        Bullet newBullet = new Bullet();
        newBullet.spawn(x, y, vx, vy, sprite, fallbackColor);
        pool.add(newBullet);
        return newBullet;
    }

    public void update(double delta, double panelWidth, double panelHeight) {
        for (Bullet b : pool) {
            if (b.isActive()) {
                b.update(delta, panelWidth, panelHeight);
            }
        }
    }

    public void render(GraphicsContext gc) {
        for (Bullet b : pool) {
            if (b.isActive()) {
                b.render(gc);
            }
        }
    }

    public List<Bullet> getActiveBullets() {
        List<Bullet> active = new ArrayList<>();
        for (Bullet b : pool) {
            if (b.isActive()) active.add(b);
        }
        return active;
    }
}