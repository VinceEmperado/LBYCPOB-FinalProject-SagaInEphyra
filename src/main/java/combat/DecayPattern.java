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

    // The primary constructor
    public DecayPattern(double boxSize, double driftX, double driftY, double screenWidth, double screenHeight) {
        this.boxSize = boxSize;
        this.driftX = driftX;
        this.driftY = driftY;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
    }

    // --- UPDATED FALLBACK CONSTRUCTORS ---
    // These now accurately default to your 1600x900 window resolution
    public DecayPattern() {
        this(350.0, 60.0, 40.0, 1600.0, 900.0);
    }

    public DecayPattern(double boxSize, double shrinkSpeed, double driftX, double driftY) {
        this(boxSize, driftX, driftY, 1600.0, 900.0);
    }

    public DecayPattern(double boxSize, double shrinkSpeed, double driftX, double driftY, Object target) {
        this(boxSize, driftX, driftY, 1600.0, 900.0);
    }

    @Override
    public void execute(BulletPool pool, double originX, double originY, Image sprite, Color fallbackColor) {
        if (pool == null) return;

        double halfSize = boxSize / 2.0;
        double spacing = 18.0;
        int steps = (int) (boxSize / spacing);

        List<double[]> bulletOffsets = new ArrayList<>();

        // Top and Bottom walls offsets
        for (int i = 0; i <= steps; i++) {
            double posX = -halfSize + (i * spacing);
            bulletOffsets.add(new double[]{posX, -halfSize});
            bulletOffsets.add(new double[]{posX, halfSize});
        }

        // Left and Right walls offsets
        for (int i = 0; i <= steps; i++) {
            double posY = -halfSize + (i * spacing);
            bulletOffsets.add(new double[]{-halfSize, posY});
            bulletOffsets.add(new double[]{halfSize, posY});
        }

        List<Bullet> boxBullets = new ArrayList<>();
        double cx = originX;
        double cy = originY;
        double currentVx = driftX;
        double currentVy = driftY;

        for (double[] offset : bulletOffsets) {
            double bx = cx + offset[0];
            double by = cy + offset[1];
            Bullet b = pool.spawnBullet(bx, by, currentVx, currentVy, 10.0, sprite, fallbackColor);
            if (b != null) {
                boxBullets.add(b);
            }
        }

        final double[] center = {cx, cy};
        final double[] velocity = {currentVx, currentVy};

        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(16), e -> {
            double dt = 0.016;
            center[0] += velocity[0] * dt;
            center[1] += velocity[1] * dt;

            // Boundaries are now securely tied to 1600 and 900
            double minX = halfSize;
            double maxX = screenWidth - halfSize;
            double minY = halfSize;
            double maxY = screenHeight - halfSize;

            // Bounce off left/right walls
            if (center[0] < minX) {
                center[0] = minX;
                velocity[0] = -velocity[0];
            } else if (center[0] > maxX) {
                center[0] = maxX;
                velocity[0] = -velocity[0];
            }

            // Bounce off top/bottom walls
            if (center[1] < minY) {
                center[1] = minY;
                velocity[1] = -velocity[1];
            } else if (center[1] > maxY) {
                center[1] = maxY;
                velocity[1] = -velocity[1];
            }

            // Update all active bullets
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