package combat;

import pools.BulletPool;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import java.util.Random;

public class TeardropPattern implements FirePattern {
    private final int dropCount;
    private final double baseFallSpeed;
    private final double screenWidth;
    private final double durationSeconds;
    private final Random random = new Random();

    public TeardropPattern(int dropCount, double baseFallSpeed, double screenWidth, double durationSeconds) {
        this.dropCount = dropCount;
        this.baseFallSpeed = baseFallSpeed;
        this.screenWidth = screenWidth;
        this.durationSeconds = durationSeconds;
    }

    public TeardropPattern(int dropCount, double baseFallSpeed, double screenWidth) {
        this(dropCount, baseFallSpeed, screenWidth, 4.0);
    }

    @Override
    public void execute(BulletPool bulletPool, double startX, double startY, Image sprite, Color fallbackColor) {
        if (dropCount <= 0 || bulletPool == null) return;

        int bursts = dropCount;
        double interval = durationSeconds / bursts;

        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(interval), event -> {
            int dropsPerPulse = 12;
            for (int i = 0; i < dropsPerPulse; i++) {
                double spawnX = random.nextDouble() * screenWidth;
                double spawnY = -random.nextDouble() * 100 - 10;

                double fallSpeed = baseFallSpeed + (random.nextDouble() * 200 - 50);
                double windDrift = (random.nextDouble() * 30) - 15;

                bulletPool.spawnBullet(spawnX, spawnY, windDrift, fallSpeed, sprite, fallbackColor);
            }
        }));

        timeline.setCycleCount(bursts);
        timeline.play();
    }
}