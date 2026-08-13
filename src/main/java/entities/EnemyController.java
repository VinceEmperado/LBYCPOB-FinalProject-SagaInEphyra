package entities;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;
import java.util.Random;

public class EnemyController {
    private double x, y;

    private int width = 120, height = 120;
    private Image enemySprite;
    private Random random = new Random();

    private double attackTimer = 0;
    private double attackDuration = 2.0;

    private double teleportWarningDuration = 0.5;
    private double currentRotation = 0.0;
    private boolean isPreparingToTeleport = false;

    public EnemyController(double startX, double startY) {
        this.x = startX;
        this.y = startY;

        try {
            enemySprite = new Image(getClass().getResourceAsStream("/sprites/boss/mart.png"));
        } catch (Exception e) {
            System.err.println("Could not load standard enemy sprite.");
        }
    }

    public void update(double delta, int panelWidth, int panelHeight) {
        attackTimer += delta;

        if (attackTimer >= attackDuration && attackTimer < (attackDuration + teleportWarningDuration)) {
            isPreparingToTeleport = true;
            currentRotation += Math.PI * 8 * delta;
        }

        else if (attackTimer >= (attackDuration + teleportWarningDuration)) {
            x = random.nextInt(panelWidth - width);
            y = random.nextInt((panelHeight / 2) - height);

            attackTimer = 0;
            isPreparingToTeleport = false;
            currentRotation = 0.0;
        }

        else {
            isPreparingToTeleport = false;
            currentRotation = 0.0;
        }
    }

    public void render(GraphicsContext gc) {
        Affine oldTransform = gc.getTransform();

        if (isPreparingToTeleport) {
            double pivotX = x + width / 2;
            double pivotY = y + height / 2;
            double degrees = Math.toDegrees(currentRotation);

            gc.translate(pivotX, pivotY);
            gc.rotate(degrees);
            gc.translate(-pivotX, -pivotY);
        }

        if (enemySprite != null) {
            gc.drawImage(enemySprite, (int) x, (int) y, width, height);
        } else {
            gc.setFill(Color.YELLOW);
            gc.fillRect((int) x, (int) y, width, height);
        }

        gc.setTransform(oldTransform);
    }

    // Getters
    public double getX() { return x; }
    public double getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
}