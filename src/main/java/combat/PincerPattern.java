package combat;

import pools.BulletPool;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class PincerPattern implements FirePattern {
    private double offset;
    private int bulletsPerSide;
    private double speed;
    private double inwardAngleRad;

    public PincerPattern(double offset, int bulletsPerSide, double speed, double inwardAngleRad) {
        this.offset = offset;
        this.bulletsPerSide = bulletsPerSide;
        this.speed = speed;
        this.inwardAngleRad = inwardAngleRad;
    }

    @Override
    public void execute(BulletPool pool, double originX, double originY, Image sprite, Color fallbackColor) {
        if (pool == null) return;

        double leftSpawnX = originX - offset;
        double rightSpawnX = originX + offset;

        double angleSpread = Math.toRadians(6);

        for (int i = 0; i < bulletsPerSide; i++) {
            double spreadOffset = (i - (bulletsPerSide - 1) / 2.0) * angleSpread;

            double leftAngle = inwardAngleRad + spreadOffset;
            double lvx = Math.cos(leftAngle) * speed;
            double lvy = Math.sin(leftAngle) * speed;
            pool.spawnBullet(leftSpawnX, originY, lvx, lvy, sprite, fallbackColor);

            double rightAngle = Math.PI - inwardAngleRad - spreadOffset;
            double rvx = Math.cos(rightAngle) * speed;
            double rvy = Math.sin(rightAngle) * speed;
            pool.spawnBullet(rightSpawnX, originY, rvx, rvy, sprite, fallbackColor);
        }
    }
}