package combat;

import pools.BulletPool;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class PatternSpawner {
    private BulletPool bulletPool;

    public PatternSpawner(BulletPool bulletPool) {
        this.bulletPool = bulletPool;
    }

    /**
     * Executes any FirePattern strategy implementation.
     */
    public void fire(FirePattern pattern, double originX, double originY, Image sprite, Color fallbackColor) {
        if (bulletPool != null && pattern != null) {
            pattern.execute(bulletPool, originX, originY, sprite, fallbackColor);
        }
    }

    /**
     * Convenience method to spawn a pincer pattern.
     */
    public void spawnPincer(double x, double y, double offset, int bulletsPerSide, double speed, double inwardAngleRad, Image sprite, Color color) {
        fire(new PincerPattern(offset, bulletsPerSide, speed, inwardAngleRad), x, y, sprite, color);
    }

    // Getters & Setters
    public BulletPool getBulletPool() { return bulletPool; }
    public void setBulletPool(BulletPool bulletPool) { this.bulletPool = bulletPool; }
}