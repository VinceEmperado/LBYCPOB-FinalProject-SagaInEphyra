package combat;

import entities.PlayerCharacter;
import pools.BulletPool;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class JawBitePattern implements FirePattern {
    private final int bulletsPerJaw;
    private final double speed;
    private final Object target;

    public JawBitePattern(int bulletsPerJaw, double speed) {
        this(bulletsPerJaw, speed, null);
    }

    public JawBitePattern(int bulletsPerJaw, double speed, Object target) {
        this.bulletsPerJaw = bulletsPerJaw;
        this.speed = speed;
        this.target = target;
    }

    @Override
    public void execute(BulletPool pool, double originX, double originY, Image sprite, Color fallbackColor) {
        if (pool == null) return;

        // Default target coordinates if no player is passed
        double targetX = originX;
        double targetY = originY + 300.0;
        if (target instanceof PlayerCharacter player) {
            targetX = player.getX();
            targetY = player.getY();
        }

        double jawWidth = 140.0;          // Horizontal span of the bite
        double spawnOffsetDistance = 180.0; // Distance above and below the player where jaws spawn

        double step = bulletsPerJaw > 1 ? jawWidth / (bulletsPerJaw - 1) : 0;

        for (int i = 0; i < bulletsPerJaw; i++) {
            double xOffset = -jawWidth / 2.0 + (i * step);

            // Upper jaw: spawns above the target and snaps downward
            double upperSpawnX = targetX + xOffset;
            double upperSpawnY = targetY - spawnOffsetDistance;
            // Slight inward angle toward the center
            double upperVx = -xOffset * 0.2;
            pool.spawnBullet(upperSpawnX, upperSpawnY, upperVx, speed, sprite, fallbackColor);

            // Lower jaw: spawns below the target and snaps upward
            double lowerSpawnX = targetX + xOffset;
            double lowerSpawnY = targetY + spawnOffsetDistance;
            double lowerVx = -xOffset * 0.2;
            pool.spawnBullet(lowerSpawnX, lowerSpawnY, lowerVx, -speed, sprite, fallbackColor);
        }
    }
}