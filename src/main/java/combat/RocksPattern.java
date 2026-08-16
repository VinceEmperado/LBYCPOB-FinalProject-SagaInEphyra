package combat;

import entities.PlayerCharacter;
import pools.BulletPool;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class RocksPattern implements FirePattern {
    private final double speed;
    private final PlayerCharacter target;
    private final double rockSize;

    public RocksPattern(int rockCount, double speed, double spreadWidth, PlayerCharacter target) {
        this.speed = speed;
        this.target = target;
        this.rockSize = 15.0;
    }

    public RocksPattern(double speed, PlayerCharacter target) {
        this.speed = speed;
        this.target = target;
        this.rockSize = 15.0;
    }

    public RocksPattern(double speed) {
        this.speed = speed;
        this.target = null;
        this.rockSize = 15.0;
    }

    @Override
    public void execute(BulletPool pool, double originX, double originY, Image sprite, Color fallbackColor) {
        if (pool == null) return;

        double vx = 0.0;
        double vy = speed;

        if (target != null) {
            double dx = target.getX() - originX;
            double dy = target.getY() - originY;
            double distance = Math.sqrt(dx * dx + dy * dy);

            if (distance > 0) {
                vx = (dx / distance) * speed;
                vy = (dy / distance) * speed;
            }
        }

        pool.spawnBullet(originX, originY, vx, vy, rockSize, sprite, fallbackColor);
    }
}