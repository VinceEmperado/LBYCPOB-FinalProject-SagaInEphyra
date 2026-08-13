package entities;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.Random;

public class EnemyController {
    private double x, y;
    private double targetX, targetY;

    private int width = 60, height = 60;
    private double speed = 300;
    private BufferedImage enemySprite;
    private Random random = new Random();

    // state machine
    private enum State { MOVING, ATTACKING }
    // attack then move
    private State currentState = State.ATTACKING;

    private double attackTimer = 0;
    // stays still to attack before moving again
    private double attackDuration = 2.0;

    public EnemyController(double startX, double startY) {
        this.x = startX;
        this.y = startY;
        this.targetX = startX;
        this.targetY = startY;

        try {
            enemySprite = ImageIO.read(getClass().getResourceAsStream("/sprites/boss/enemy.png"));
        } catch (IOException | IllegalArgumentException e) {
            System.err.println("Could not load standard enemy sprite.");
        }
    }

    public void update(double delta, int panelWidth, int panelHeight) {
        if (currentState == State.MOVING) {
            double dx = targetX - x;
            double dy = targetY - y;
            double distance = Math.sqrt(dx * dx + dy * dy);

            if (distance < 5.0) {
                x = targetX;
                y = targetY;
                currentState = State.ATTACKING;
                attackTimer = 0; // reset the attack timer
            } else {
                x += (dx / distance) * speed * delta;
                y += (dy / distance) * speed * delta;
            }

        } else if (currentState == State.ATTACKING) {
            // call the attacks here

            attackTimer += delta;

            // picks a new spot to move
            if (attackTimer >= attackDuration) {
                targetX = random.nextInt(panelWidth - width);
                
                targetY = random.nextInt((panelHeight / 2) - height);

                currentState = State.MOVING;
            }
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