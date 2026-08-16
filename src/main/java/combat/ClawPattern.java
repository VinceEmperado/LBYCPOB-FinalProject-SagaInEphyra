package combat;

import entities.PlayerCharacter;
import pools.BulletPool;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class ClawPattern implements FirePattern {
    private final int bulletCount;
    private final double speed;
    private final double arcAngle;
    private final PlayerCharacter target;

    public ClawPattern(int bulletCount, double speed, double arcAngle) {
        this(bulletCount, speed, arcAngle, null);
    }

    public ClawPattern(int bulletCount, double speed, double arcAngle, PlayerCharacter target) {
        this.bulletCount = bulletCount;
        this.speed = speed;
        this.arcAngle = arcAngle;
        this.target = target;
    }

    @Override
    public void execute(BulletPool pool, double originX, double originY, Image sprite, Color fallbackColor) {
        if (pool == null) return;

        double baseAngle = getAngleToTarget(originX, originY, target);
        double startAngle = baseAngle - (arcAngle / 2.0);
        double angleStep = bulletCount > 1 ? arcAngle / (bulletCount - 1) : 0;
        double centerIndex = (bulletCount - 1) / 2.0;

        for (int i = 0; i < bulletCount; i++) {
            double angle = startAngle + (i * angleStep);


            double speedModifier = 1.0;
            if (bulletCount > 1) {
                speedModifier = 0.75 + (0.25 * (Math.abs(i - centerIndex) / Math.max(1.0, centerIndex)));
            }
            double currentSpeed = speed * speedModifier;

            double vx = Math.cos(angle) * currentSpeed;
            double vy = Math.sin(angle) * currentSpeed;

            pool.spawnBullet(originX, originY, vx, vy, sprite, fallbackColor);
        }
    }

    private double getAngleToTarget(double originX, double originY, PlayerCharacter target) {
        if (target == null) {
            return Math.PI / 2; // Shoots straight down by default
        }
        double dx = target.getX() - originX;
        double dy = target.getY() - originY;


        return Math.atan2(dy, dx);
    }
}