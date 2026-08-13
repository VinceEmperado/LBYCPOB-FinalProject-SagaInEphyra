package entities;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.Random;

public class EnemyController {
    private double x, y;

    private int width = 120, height = 120;
    private BufferedImage enemySprite;
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
            enemySprite = ImageIO.read(getClass().getResourceAsStream("/sprites/boss/mart.png"));
        } catch (IOException | IllegalArgumentException e) {
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

    public void render(Graphics2D g2d) {
        AffineTransform oldTransform = g2d.getTransform();

        if (isPreparingToTeleport) {
            g2d.rotate(currentRotation, x + (width / 2.0), y + (height / 2.0));
        }

        if (enemySprite != null) {
            g2d.drawImage(enemySprite, (int) x, (int) y, width, height, null);
        } else {
            g2d.setColor(Color.YELLOW);
            g2d.fillRect((int) x, (int) y, width, height);
        }

        g2d.setTransform(oldTransform);
    }

    // Getters
    public double getX() { return x; }
    public double getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
}