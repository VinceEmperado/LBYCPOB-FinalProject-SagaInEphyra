package combat;

import pools.BulletPool;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class TentaclePattern implements FirePattern {
    private final int arms;
    private final int bulletsPerArm;
    private final double speed;
    private final double curvature;
    private final double baseAngle;

    public TentaclePattern(int arms, int bulletsPerArm, double speed, double curvature) {
        this(arms, bulletsPerArm, speed, curvature, Math.PI / 2.0);
    }

    public TentaclePattern(int arms, int bulletsPerArm, double speed, double curvature, double baseAngle) {
        this.arms = arms;
        this.bulletsPerArm = bulletsPerArm;
        this.speed = speed;
        this.curvature = curvature;
        this.baseAngle = baseAngle;
    }

    @Override
    public void execute(BulletPool pool, double originX, double originY, Image sprite, Color fallbackColor) {
        if (pool == null) return;

        for (int a = 0; a < arms; a++) {
            double armAngleOffset = (a * 2.0 * Math.PI) / arms;
            for (int b = 0; b < bulletsPerArm; b++) {
                double progress = (b + 1.0) / bulletsPerArm;
                double angle = baseAngle + armAngleOffset + (curvature * b);
                double currentSpeed = speed * progress;
                double vx = Math.cos(angle) * currentSpeed;
                double vy = Math.sin(angle) * currentSpeed;
                pool.spawnBullet(originX, originY, vx, vy, sprite, fallbackColor);
            }
        }
    }
}