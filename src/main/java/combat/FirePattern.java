package combat;

import entities.PlayerCharacter;
import pools.BulletPool;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

@FunctionalInterface
public interface FirePattern {

    /**
     * Core functional method for executing any bullet pattern strategy.
     */
    void execute(BulletPool pool, double originX, double originY, Image sprite, Color fallbackColor);

    /**
     * Calculates the trajectory angle (in radians) toward a target player.
     * Defaults to facing straight down (PI / 2) if target is null or not a player.
     */
    default double getAngleToTarget(double originX, double originY, Object target) {
        if (target instanceof PlayerCharacter player) {
            return Math.atan2(player.getY() - originY, player.getX() - originX);
        }
        return Math.PI / 2;
    }

    /**
     * Spawns a full 360-degree ring of bullets from an origin point.
     */
    default void spawnRing(BulletPool pool, double originX, double originY, int count, double speed, Image sprite, Color fallbackColor) {
        if (pool == null) return;
        double angleStep = (2 * Math.PI) / count;
        for (int i = 0; i < count; i++) {
            double angle = i * angleStep;
            double vx = Math.cos(angle) * speed;
            double vy = Math.sin(angle) * speed;
            pool.spawnBullet(originX, originY, vx, vy, sprite, fallbackColor);
        }
    }

    /**
     * Spawns two angled fan patterns offset on the left and right sides.
     */
    default void spawnDualFan(BulletPool pool, double originX, double originY, double offset, int bulletsPerSide, double speed, double inwardAngleRad, Image sprite, Color fallbackColor) {
        if (pool == null) return;

        double leftSpawnX = originX - offset;
        double rightSpawnX = originX + offset;
        double angleSpread = Math.toRadians(6);

        for (int i = 0; i < bulletsPerSide; i++) {
            double spreadOffset = (i - (bulletsPerSide - 1) / 2.0) * angleSpread;

            // Left side fan
            double leftAngle = inwardAngleRad + spreadOffset;
            double lvx = Math.cos(leftAngle) * speed;
            double lvy = Math.sin(leftAngle) * speed;
            pool.spawnBullet(leftSpawnX, originY, lvx, lvy, sprite, fallbackColor);

            // Right side fan
            double rightAngle = Math.PI - inwardAngleRad - spreadOffset;
            double rvx = Math.cos(rightAngle) * speed;
            double rvy = Math.sin(rightAngle) * speed;
            pool.spawnBullet(rightSpawnX, originY, rvx, rvy, sprite, fallbackColor);
        }
    }

    /**
     * Spawns a fan/arc of bullets centered directly toward the target player.
     */
    default void spawnAimedSpread(BulletPool pool, double originX, double originY, Object target, int count, double speed, double spreadAngleRad, Image sprite, Color fallbackColor) {
        if (pool == null) return;
        double baseAngle = getAngleToTarget(originX, originY, target);
        double startAngle = baseAngle - (spreadAngleRad / 2.0);
        double angleStep = count > 1 ? spreadAngleRad / (count - 1) : 0;

        for (int i = 0; i < count; i++) {
            double angle = startAngle + (i * angleStep);
            double vx = Math.cos(angle) * speed;
            double vy = Math.sin(angle) * speed;
            pool.spawnBullet(originX, originY, vx, vy, sprite, fallbackColor);
        }
    }
}