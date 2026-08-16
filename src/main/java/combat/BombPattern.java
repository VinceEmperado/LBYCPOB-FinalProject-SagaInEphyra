package combat;

import pools.BulletPool;
import javafx.scene.paint.Color;

public class BombPattern {
    private final int count;
    private final double speed;

    public BombPattern(int count, double speed) {
        this.count = count;
        this.speed = speed;
    }

    public void execute(BulletPool bulletPool, double startX, double startY, Object target, Color color) {
        double step = (2 * Math.PI) / count;
        for (int i = 0; i < count; i++) {
            double angle = i * step;
            double vx = Math.cos(angle) * speed;
            double vy = Math.sin(angle) * speed;
            bulletPool.spawnBullet(startX, startY, vx, vy, null, color);
        }
    }
}