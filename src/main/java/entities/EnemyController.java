package entities;

import java.awt.*;
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
    // stays still to attack before moving again
    private double attackDuration = 2.0;

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
        // call the attacks here

        attackTimer += delta;

        // picks a new spot to teleport to once the timer finishes
        if (attackTimer >= attackDuration) {

            // teleport to a new random location
            x = random.nextInt(panelWidth - width);
            y = random.nextInt((panelHeight / 2) - height);

            // reset the attack timer
            attackTimer = 0;
        }
    }

    public void render(Graphics2D g2d) {
        if (enemySprite != null) {
            g2d.drawImage(enemySprite, (int) x, (int) y, width, height, null);
        } else {
            g2d.setColor(Color.YELLOW);
            g2d.fillRect((int) x, (int) y, width, height);
        }
    }

    // Getters
    public double getX() { return x; }
    public double getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
}