package combat;

import entities.PlayerCharacter;
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

    public void spawnPincer(double x, double y, int bulletsPerSide, double speed, double spreadAngle, PlayerCharacter target, Image sprite, Color color) {
        fire(new PincerPattern(bulletsPerSide, speed, spreadAngle, target), x, y, sprite, color);
    }

    public void spawnClaw(double x, double y, int bulletCount, double speed, double arcAngle, PlayerCharacter target, Image sprite, Color color) {
        fire(new ClawPattern(bulletCount, speed, arcAngle, target), x, y, sprite, color);
    }

    public BulletPool getBulletPool() { return bulletPool; }
    public void setBulletPool(BulletPool bulletPool) { this.bulletPool = bulletPool; }
}