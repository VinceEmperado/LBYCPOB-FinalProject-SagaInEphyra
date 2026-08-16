package combat;

import pools.BulletPool;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class SpiralPattern implements FirePattern {
    private final int totalBullets;
    private final double speed;
    private final double spiralStep;

    public SpiralPattern(int totalBullets, double speed, double spiralStep) {
        this.totalBullets = totalBullets;
        this.speed = speed;
        this.spiralStep = spiralStep;
    }

    @Override
    public void execute(BulletPool pool, double originX, double originY, Image sprite, Color fallbackColor) {
        if (pool == null) return;
        for (int i = 0; i < totalBullets; i++) {
            double angle = i * spiralStep;
            double vx = Math.cos(angle) * speed;
            double vy = Math.sin(angle) * speed;
            pool.spawnBullet(originX, originY, vx, vy, sprite, fallbackColor);
        }
    }
}