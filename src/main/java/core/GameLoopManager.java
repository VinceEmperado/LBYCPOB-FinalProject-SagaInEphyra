package core;

import combat.Bullet;
import entities.PlayerCharacter;
import entities.enemies.EnemyController;
import javafx.animation.AnimationTimer;

public class GameLoopManager {
    private long lastTime = 0;
    private final GamePanel gamePanel;
    private final PlayerCharacter playerCharacter;
    private EnemyController enemy; // Removed 'final' to allow swapping
    private final AnimationTimer timer;

    public GameLoopManager(GamePanel gamePanel, PlayerCharacter playerCharacter, EnemyController enemy) {
        this.gamePanel = gamePanel;
        this.playerCharacter = playerCharacter;
        this.enemy = enemy;

        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (lastTime == 0) {
                    lastTime = now;
                    return;
                }
                double delta = (now - lastTime) / 1_000_000_000.0;
                lastTime = now;

                update(delta);
                gamePanel.render();
            }
        };
    }

    public void setEnemy(EnemyController enemy) {
        this.enemy = enemy;
    }

    public void start() {
        lastTime = 0;
        timer.start();
    }

    public void stop() {
        timer.stop();
    }

    private void update(double delta) {
        if (playerCharacter != null && playerCharacter.isGameOver()) {
            stop();
            gamePanel.showGameOver(false);
            return;
        }

        if (enemy != null && enemy.isDead()) {
            stop();
            gamePanel.showGameOver(true);
            return;
        }

        playerCharacter.update(
                delta,
                gamePanel.isUp(),
                gamePanel.isDown(),
                gamePanel.isLeft(),
                gamePanel.isRight(),
                gamePanel.isShooting(),
                gamePanel.getPlayerBulletPool(),
                gamePanel.isSlowDown()
        );

        if (enemy != null && !enemy.isDead()) {
            enemy.update(delta, gamePanel.getWidth(), gamePanel.getHeight());
            enemy.checkOrbCollisions(playerCharacter);
        }

        // Update Bullet Pools
        if (gamePanel.getBulletPool() != null) {
            gamePanel.getBulletPool().update(delta, gamePanel.getWidth(), gamePanel.getHeight());
        }

        if (gamePanel.getPlayerBulletPool() != null) {
            gamePanel.getPlayerBulletPool().update(delta, gamePanel.getWidth(), gamePanel.getHeight());
        }

        checkPlayerBulletCollisions();
        checkEnemyBulletCollisions();
    }

    private void checkPlayerBulletCollisions() {
        if (enemy == null || enemy.isDead() || gamePanel.getPlayerBulletPool() == null) return;

        for (Bullet bullet : gamePanel.getPlayerBulletPool().getActiveBullets()) {
            if (!bullet.isActive()) continue;

            if (isColliding(bullet, enemy.getX(), enemy.getY(), enemy.getWidth(), enemy.getHeight())) {
                enemy.takeDamage(bullet.getDamagePacket().getAmount());
                bullet.setActive(false); // Recycles bullet back into pool
            }
        }
    }

    private void checkEnemyBulletCollisions() {
        if (playerCharacter == null || playerCharacter.isDead() || gamePanel.getBulletPool() == null) return;

        for (Bullet bullet : gamePanel.getBulletPool().getActiveBullets()) {
            if (!bullet.isActive()) continue;

            if (isColliding(bullet, playerCharacter.getX(), playerCharacter.getY(),
                    playerCharacter.getWidth(), playerCharacter.getHeight())) {
                playerCharacter.takeDamage(bullet.getDamagePacket().getAmount());
                bullet.setActive(false);
            }
        }
    }

    private boolean isColliding(Bullet bullet, double rx, double ry, double rw, double rh) {
        double closestX = Math.clamp(bullet.getX(), rx, rx + rw);
        double closestY = Math.clamp(bullet.getY(), ry, ry + rh);

        double distX = bullet.getX() - closestX;
        double distY = bullet.getY() - closestY;

        return (distX * distX + distY * distY) < (bullet.getRadius() * bullet.getRadius());
    }
}