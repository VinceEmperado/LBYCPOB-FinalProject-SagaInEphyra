package entities.enemies;

import combat.PatternSpawner;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import java.util.Random;


public class EnemyController {
    private double x, y;

    private int width = 120, height = 120;
    private Image enemySprite;
    private Image bulletSprite;
    private Random random = new Random();

    private double attackTimer = 0;
    private double attackDuration = 2.0;

    private double teleportWarningDuration = 0.5;
    private double currentRotation = 0.0;
    private boolean isPreparingToTeleport = false;

    private PatternSpawner patternSpawner;
    private double pincerCooldown = 0.0;
    private double pincerInterval = 1.0;

    public EnemyController(double startX, double startY, PatternSpawner patternSpawner) {
        this.x = startX;
        this.y = startY;
        this.patternSpawner = patternSpawner;

        try {
            enemySprite = new Image(getClass().getResourceAsStream("/sprites/boss/mart.png"));
        } catch (Exception e) {
            System.err.println("Could not load standard enemy sprite.");
        }

        try {
            bulletSprite = new Image(getClass().getResourceAsStream("/sprites/bullets/pincer.png"));
        } catch (Exception e) {
            System.err.println("Could not load bullet sprite, defaulting to fallback color.");
        }
    }

    public void update(double delta, double panelWidth, double panelHeight) {
        attackTimer += delta;

        if (attackTimer >= attackDuration && attackTimer < (attackDuration + teleportWarningDuration)) {
            isPreparingToTeleport = true;
            currentRotation += Math.PI * 8 * delta;
        }

        else if (attackTimer >= (attackDuration + teleportWarningDuration)) {
            x = random.nextInt(Math.max(1, (int) panelWidth - width));
            y = random.nextInt(Math.max(1, (int) (panelHeight / 2) - height));

            attackTimer = 0;
            isPreparingToTeleport = false;
            currentRotation = 0.0;
        }

        else {
            isPreparingToTeleport = false;
            currentRotation = 0.0;

            pincerCooldown += delta;
            if (pincerCooldown >= pincerInterval) {
                pincerCooldown = 0;
                firePincerAttack();
            }
        }
    }

    private void firePincerAttack() {
        if (patternSpawner == null) return;

        double centerX = x + (width / 2.0);
        double centerY = y + (height / 2.0);

        double flankOffset = 180.0;
        int bulletsPerSide = 4;
        double bulletSpeed = 200.0;
        double inwardAngle = Math.toRadians(55);

        patternSpawner.spawnPincer(
                centerX,
                centerY,
                flankOffset,
                bulletsPerSide,
                bulletSpeed,
                inwardAngle,
                bulletSprite,
                Color.ORANGE
        );
    }

    public void render(GraphicsContext gc) {
        gc.save();

        if (isPreparingToTeleport) {
            double pivotX = x + (width / 2.0);
            double pivotY = y + (height / 2.0);
            gc.translate(pivotX, pivotY);
            gc.rotate(Math.toRadians(currentRotation));
            gc.translate(-pivotX, -pivotY);
        }

        if (enemySprite != null) {
            gc.drawImage(enemySprite, x, y, width, height);
        } else {
            gc.setFill(Color.YELLOW);
            gc.fillRect(x, y, width, height);
        }

        gc.restore();
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
}