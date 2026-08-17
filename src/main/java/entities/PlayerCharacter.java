package entities;

import combat.HealthSystem;
import pools.BulletPool;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import java.io.InputStream;

public class PlayerCharacter {
    private double x, y;
    private final double spawnX, spawnY;
    private double speed = 300;
    private final int width = 40;
    private final int height = 40;
    private Image playerSprite;
    private Image bulletSprite;

    // Health & Respawn System
    private final HealthSystem healthSystem;
    private int lives;
    private double invulnerabilityTimer = 0.0;
    private final double invulnerabilityDuration = 2.0; // 2 seconds of i-frames after respawning
    private boolean godMode = false;

    // Shooting controls, damage & cooldowns
    private double shootCooldown = 0.0;
    private final double shootInterval = 0.10;
    private final double playerBulletSpeed = 600.0;
    private double bulletDamage = 15.0;

    public PlayerCharacter(double startX, double startY) {
        this(startX, startY, 100.0, 3); // Default 100 HP, 3 Lives
    }

    public PlayerCharacter(double startX, double startY, double maxHealth, int startingLives) {
        this.spawnX = startX;
        this.spawnY = startY;
        this.x = startX;
        this.y = startY;
        this.lives = startingLives;
        this.healthSystem = new HealthSystem(maxHealth);

        InputStream playerStream = getClass().getResourceAsStream("/sprites/player/zany.jpg");
        if (playerStream != null) {
            this.playerSprite = new Image(playerStream);
        }

        InputStream bulletStream = getClass().getResourceAsStream("/sprites/bullets/player_bullet.png");
        if (bulletStream != null) {
            this.bulletSprite = new Image(bulletStream);
        }
    }

    public void update(double delta, boolean up, boolean down, boolean left, boolean right,
                       boolean isShooting, BulletPool playerBulletPool, boolean slowDown) {

        if (isGameOver()) return;

        if (invulnerabilityTimer > 0) {
            invulnerabilityTimer -= delta;
        }

        this.speed = slowDown ? 180 : 320;

        if (up) y -= speed * delta;
        if (down) y += speed * delta;
        if (left) x -= speed * delta;
        if (right) x += speed * delta;

        x = Math.clamp(x, 0, 1600 - width);
        y = Math.clamp(y, 0, 900 - height);

        shootCooldown += delta;
        if (isShooting && shootCooldown >= shootInterval && playerBulletPool != null) {
            shoot(playerBulletPool);
            shootCooldown = 0.0;
        }
    }

    private void shoot(BulletPool playerBulletPool) {
        double centerX = x + (width / 2.0) - 4.0;
        double spawnYPosition = y - 10.0;

        playerBulletPool.spawnBullet(
                centerX,
                spawnYPosition,
                0.0,
                -playerBulletSpeed,
                bulletDamage,
                bulletSprite,
                Color.CYAN
        );
    }

    public void takeDamage(double amount) {
        if (godMode || isInvulnerable() || isGameOver()) return;

        healthSystem.takeDamage(amount);

        if (healthSystem.isDead()) {
            lives--;
            if (lives > 0) {
                respawn();
            }
        }
    }

    public void respawn() {
        this.x = spawnX;
        this.y = spawnY;
        this.healthSystem.reset();
        this.invulnerabilityTimer = invulnerabilityDuration;
    }

    public double getHealth() {
        return healthSystem.getCurrentHealth();
    }

    public boolean isInvulnerable() {
        return invulnerabilityTimer > 0;
    }

    public boolean isGameOver() {
        return lives <= 0 && healthSystem.isDead();
    }

    public boolean isDead() {
        return isGameOver();
    }

    public void setGodMode(boolean godMode) {
        this.godMode = godMode;
    }

    public boolean isGodMode() {
        return godMode;
    }

    public void addLife(int amount) {
        this.lives += amount;
    }

    public void heal(double amount) {
        if (this.healthSystem != null) {
            this.healthSystem.heal(amount);
        }
    }

    public void increaseBulletDamage(double amount) {
        this.bulletDamage += amount;
    }

    public double getBulletDamage() {
        return bulletDamage;
    }

    public void render(GraphicsContext gc) {
        if (isGameOver()) return;

        if (isInvulnerable() && ((int) (invulnerabilityTimer * 15) % 2 == 0)) {
            return;
        }

        if (playerSprite != null && !playerSprite.isError()) {
            gc.drawImage(playerSprite, x, y, width, height);
        } else {
            gc.setFill(Color.GREEN);
            gc.fillRect((int) x, (int) y, width, height);
        }
    }

    public int getLives() { return lives; }
    public double getX() { return x; }
    public double getY() { return y; }
    public double getHeight() { return height; }
    public double getWidth() { return width; }
    public HealthSystem getHealthSystem() { return healthSystem; }
}