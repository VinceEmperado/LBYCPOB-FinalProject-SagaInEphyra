package entities;

import combat.PatternSpawner;

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
    private BufferedImage bulletSprite;
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
            enemySprite = ImageIO.read(getClass().getResourceAsStream("/sprites/boss/mart.png"));
        } catch (IOException | IllegalArgumentException e) {
            System.err.println("Could not load standard enemy sprite.");
        }

        try {
            bulletSprite = ImageIO.read(getClass().getResourceAsStream("/sprites/bullets/pincer.png"));
        } catch (IOException | IllegalArgumentException e) {
            System.err.println("Could not load bullet sprite, defaulting to fallback color.");
        }
    }

    public void update(double delta, int panelWidth, int panelHeight) {
        attackTimer += delta;

        if (attackTimer >= attackDuration && attackTimer < (attackDuration + teleportWarningDuration)) {
            isPreparingToTeleport = true;
            currentRotation += Math.PI * 8 * delta;
        }

        else if (attackTimer >= (attackDuration + teleportWarningDuration)) {
            x = random.nextInt(Math.max(1, panelWidth - width));
            y = random.nextInt(Math.max(1, (panelHeight / 2) - height));

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

    public double getX() { return x; }
    public double getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
}