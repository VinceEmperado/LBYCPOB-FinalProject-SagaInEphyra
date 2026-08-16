package combat;

import pools.BulletPool;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.List;

public class DecayPattern implements FirePattern {
    private final double boxSize;
    private final double driftX;
    private final double driftY;
    private final double screenWidth;
    private final double screenHeight;

    public DecayPattern(double boxSize, double driftX, double driftY, double screenWidth, double screenHeight) {
        this.boxSize = boxSize;
        this.driftX = driftX;
        this.driftY = driftY;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
    }

    @Override
    public void execute(BulletPool pool, double originX, double originY, Image sprite, Color fallbackColor) {
        if (pool == null) return;

        double halfSize = boxSize / 2.0;
        double spacing = 18.0;
        int steps = (int) (boxSize / spacing);

        List<double[]> bulletOffsets = new ArrayList<>();

        for (int i = 0; i <= steps; i++) {
            double posX = -halfSize + (i * spacing);
            bulletOffsets.add(new double[]{posX, -halfSize});
            bulletOffsets.add(new double[]{posX, halfSize});
        }

        for (int i = 0; i <= steps; i++) {
            double posY = -halfSize + (i * spacing);
            bulletOffsets.add(new double[]{-halfSize, posY});
            bulletOffsets.add(new double[]{halfSize, posY});
        }

        List<Bullet> boxBullets = new ArrayList<>();

        for (double[] offset : bulletOffsets) {
            double bx = originX + offset[0];
            double by = originY + offset[1];
            Bullet b = pool.spawnBullet(bx, by, driftX, driftY, 10.0, sprite, fallbackColor);
            if (b != null) {
                boxBullets.add(b);
            }
        }

        final double[] center = {originX, originY};
        final double[] velocity = {driftX, driftY};

        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(16), e -> {
            double dt = 0.016;
            center[0] += velocity[0] * dt;
            center[1] += velocity[1] * dt;

            double maxX = screenWidth - halfSize;
            double maxY = screenHeight - halfSize;

            if (center[0] < halfSize) {
                center[0] = halfSize;
                velocity[0] = -velocity[0];
            } else if (center[0] > maxX) {
                center[0] = maxX;
                velocity[0] = -velocity[0];
            }

            if (center[1] < halfSize) {
                center[1] = halfSize;
                velocity[1] = -velocity[1];
            } else if (center[1] > maxY) {
                center[1] = maxY;
                velocity[1] = -velocity[1];
            }

            for (int i = 0; i < boxBullets.size(); i++) {
                Bullet b = boxBullets.get(i);
                if (b.isActive()) {
                    double[] offset = bulletOffsets.get(i);
                    double bx = center[0] + offset[0];
                    double by = center[1] + offset[1];

                    b.spawn(bx, by, velocity[0], velocity[1], 10.0, sprite, fallbackColor);
                }
            }
        }));

        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }
}