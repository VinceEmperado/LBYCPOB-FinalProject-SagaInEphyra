package combat;

import pools.BulletPool;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class SonarPulsePattern implements FirePattern {

    private final double expandSpeed;
    private final double ringSpacing;
    private final int bulletCount;
    private final int pulseRings;
    private final Object target;

    public SonarPulsePattern(double expandSpeed, double ringSpacing, int bulletCount, int pulseRings) {
        this(expandSpeed, ringSpacing, bulletCount, pulseRings, null);
    }

    public SonarPulsePattern(double expandSpeed, double ringSpacing, int bulletCount, int pulseRings, Object target) {
        this.expandSpeed = expandSpeed;
        this.ringSpacing = ringSpacing;
        this.bulletCount = bulletCount;
        this.pulseRings = pulseRings;
        this.target = target;
    }

    @Override
    public void execute(BulletPool pool, double originX, double originY, Image sprite, Color fallbackColor) {
        if (pool == null) return;
        for (int r = 0; r < pulseRings; r++) {
            double currentSpeed = expandSpeed + (r * ringSpacing);
            spawnRing(pool, originX, originY, bulletCount, currentSpeed, sprite, fallbackColor);
        }
    }
}