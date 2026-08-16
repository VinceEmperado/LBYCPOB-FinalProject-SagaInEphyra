package combat;

import pools.BulletPool;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class PatternSpawner {
    private BulletPool bulletPool;

    public PatternSpawner(BulletPool bulletPool) {
        this.bulletPool = bulletPool;
    }


    public void fire(FirePattern pattern, double originX, double originY, Image sprite, Color fallbackColor) {
        if (bulletPool != null && pattern != null) {
            pattern.execute(bulletPool, originX, originY, sprite, fallbackColor);
        }
    }


    public void spawnPincer(double x, double y, double offset, int bulletsPerSide, double speed, double inwardAngleRad, Image sprite, Color color) {
        fire(new PincerPattern((int) (offset * 2), bulletsPerSide, speed, inwardAngleRad), x, y, sprite, color);
    }

    public BulletPool getBulletPool() { return bulletPool; }
    public void setBulletPool(BulletPool bulletPool) { this.bulletPool = bulletPool; }
}