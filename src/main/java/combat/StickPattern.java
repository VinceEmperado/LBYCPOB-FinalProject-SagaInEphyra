package combat;

import entities.PlayerCharacter;
import pools.BulletPool;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class StickPattern implements FirePattern {
    private final int segments;
    private final double speed;
    private final double stickLength;
    private final Object target;

    public StickPattern(int segments, double speed) {
        this(segments, speed, 100.0, null);
    }

    public StickPattern(int segments, double speed, Object target) {
        this(segments, speed, 100.0, target);
    }

    public StickPattern(int segments, double speed, double stickLength, Object target) {
        this.segments = segments;
        this.speed = speed;
        this.stickLength = stickLength;
        this.target = target;
    }

    @Override
    public void execute(BulletPool pool, double originX, double originY, Image sprite, Color fallbackColor) {
        if (pool == null) return;
        double moveAngle = getAngleToTarget(originX, originY, target);
        double vx = Math.cos(moveAngle) * speed;
        double vy = Math.sin(moveAngle) * speed;

        double segmentSpacing = stickLength / Math.max(1, segments);
        double halfLength = stickLength / 2.0;

        for (int i = 0; i < segments; i++) {
            double offset = -halfLength + (i * segmentSpacing);
            double offsetX = offset * Math.cos(moveAngle + Math.PI / 2);
            double offsetY = offset * Math.sin(moveAngle + Math.PI / 2);

            pool.spawnBullet(originX + offsetX, originY + offsetY, vx, vy, sprite, fallbackColor);
        }
    }
}