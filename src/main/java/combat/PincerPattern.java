package combat;

import entities.PlayerCharacter;
import pools.BulletPool;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class PincerPattern implements FirePattern {
    private final int bulletsPerSide;
    private final double speed;
    private final double spreadAngle;
    private final Object target;

    public PincerPattern(int bulletsPerSide, double speed, double spreadAngle) {
        this(bulletsPerSide, speed, spreadAngle, null);
    }

    public PincerPattern(int bulletsPerSide, double speed, double spreadAngle, Object target) {
        this.bulletsPerSide = bulletsPerSide;
        this.speed = speed;
        this.spreadAngle = spreadAngle;
        this.target = target;
    }

    @Override
    public void execute(BulletPool pool, double originX, double originY, Image sprite, Color fallbackColor) {
        if (pool == null) return;

        // Determine target coordinates (default downward if no player target)
        double targetX = originX;
        double targetY = originY + 500.0;
        if (target instanceof PlayerCharacter player) {
            targetX = player.getX();
            targetY = player.getY();
        }

        // Flank spawn positions offset wide to the left and right
        double leftSpawnX = originX - 300.0;
        double rightSpawnX = originX + 300.0;

        // Base trajectory angles from each flank toward the target
        double baseLeftAngle = Math.atan2(targetY - originY, targetX - leftSpawnX);
        double baseRightAngle = Math.atan2(targetY - originY, targetX - rightSpawnX);

        double halfSpread = spreadAngle / 2.0;
        double angleStep = bulletsPerSide > 1 ? spreadAngle / (bulletsPerSide - 1) : 0;

        for (int i = 0; i < bulletsPerSide; i++) {
            // Left pincer arc
            double leftAngle = (baseLeftAngle - halfSpread) + (i * angleStep);
            double lvx = Math.cos(leftAngle) * speed;
            double lvy = Math.sin(leftAngle) * speed;
            pool.spawnBullet(leftSpawnX, originY, lvx, lvy, sprite, fallbackColor);

            // Right pincer arc
            double rightAngle = (baseRightAngle + halfSpread) - (i * angleStep);
            double rvx = Math.cos(rightAngle) * speed;
            double rvy = Math.sin(rightAngle) * speed;
            pool.spawnBullet(rightSpawnX, originY, rvx, rvy, sprite, fallbackColor);
        }
    }
}