package pools;

import combat.Bullet;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import java.util.ArrayList;
import java.util.List;

public class BulletPool {
    private final List<Bullet> pool = new ArrayList<>();
    private final List<Bullet> activeBulletsCache = new ArrayList<>();

    public BulletPool(int initialCapacity) {
        for (int i = 0; i < initialCapacity; i++) {
            pool.add(new Bullet());
        }
    }

    public Bullet spawnBullet(double x, double y, double vx, double vy, Image sprite, Color fallbackColor) {
        return spawnBullet(x, y, vx, vy, 10.0, sprite, fallbackColor);
    }

    public Bullet spawnBullet(double x, double y, double vx, double vy, double damage, Image sprite, Color fallbackColor) {
        for (int i = 0; i < pool.size(); i++) {
            Bullet b = pool.get(i);
            if (!b.isActive()) {
                b.spawn(x, y, vx, vy, damage, sprite, fallbackColor);
                return b;
            }
        }
        // Expand pool if capacity is exceeded
        Bullet newBullet = new Bullet();
        newBullet.spawn(x, y, vx, vy, damage, sprite, fallbackColor);
        pool.add(newBullet);
        return newBullet;
    }

    public void update(double delta, double panelWidth, double panelHeight) {
        for (int i = 0; i < pool.size(); i++) {
            Bullet b = pool.get(i);
            if (b.isActive()) {
                b.update(delta, panelWidth, panelHeight);
            }
        }
    }

    public void render(GraphicsContext gc) {
        for (int i = 0; i < pool.size(); i++) {
            Bullet b = pool.get(i);
            if (b.isActive()) {
                b.render(gc);
            }
        }
    }

    // Call this when a boss dies or stage resets
    public void clearAllBullets() {
        for (int i = 0; i < pool.size(); i++) {
            pool.get(i).setActive(false);
        }
        activeBulletsCache.clear();
    }

    public List<Bullet> getActiveBullets() {
        activeBulletsCache.clear();
        for (int i = 0; i < pool.size(); i++) {
            Bullet b = pool.get(i);
            if (b.isActive()) {
                activeBulletsCache.add(b);
            }
        }
        return activeBulletsCache;
    }
}