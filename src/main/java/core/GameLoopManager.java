package core;

import combat.Bullet;
import entities.PlayerCharacter;
import entities.enemies.EnemyController;
import javafx.animation.AnimationTimer;

public class GameLoopManager {
    private long lastTime = 0;
    private final GamePanel gamePanel;
    private final PlayerCharacter playerCharacter;
    private EnemyController enemy;
    private final AnimationTimer timer;
    private static final double GRAZE_BUFFER = 20.0;
    private static final double PLAYER_HIT_RADIUS = 6.0;

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
        // 1. Sync current enemy from GamePanel (in case StageDirector swapped bosses)
        if (gamePanel.getEnemy() != null) {
            this.enemy = gamePanel.getEnemy();
        }

        // 2. Check Defeat Condition
        if (playerCharacter != null && playerCharacter.isGameOver()) {
            stop();
            gamePanel.showGameOver(false);
            return;
        }

        // 3. Check Victory Condition (Only when StageDirector clears ALL stages)
        if (gamePanel.getStageDirector() != null && gamePanel.getStageDirector().isAllStagesCleared()) {
            stop();
            gamePanel.showGameOver(true);
            return;
        }

        // 4. Update GamePanel, StageDirector, and Item Pools
        gamePanel.update(delta);

        // 5. Update Active Boss logic & Orbs
        if (enemy != null && !enemy.isDead()) {
            enemy.checkOrbCollisions(playerCharacter);
        }

        // 6. Update Bullet Pools
        if (gamePanel.getBulletPool() != null) {
            gamePanel.getBulletPool().update(delta, gamePanel.getWidth(), gamePanel.getHeight());
        }

        if (gamePanel.getPlayerBulletPool() != null) {
            gamePanel.getPlayerBulletPool().update(delta, gamePanel.getWidth(), gamePanel.getHeight());
        }

        // 7. Update Dialogue System UI timers
        if (gamePanel.getDialogueSystem() != null) {
            gamePanel.getDialogueSystem().update(delta);
        }

        // 8. Check Collisions
        checkPlayerBulletCollisions();
        checkEnemyBulletCollisions();
        checkGrazeEvents();
    }

    private void checkPlayerBulletCollisions() {
        if (enemy == null || enemy.isDead() || gamePanel.getPlayerBulletPool() == null) return;

        for (Bullet bullet : gamePanel.getPlayerBulletPool().getActiveBullets()) {
            if (!bullet.isActive()) continue;

            if (isColliding(bullet, enemy.getX(), enemy.getY(), enemy.getWidth(), enemy.getHeight())) {
                enemy.takeDamage(bullet.getDamagePacket().getAmount());
                bullet.setActive(false); // Recycles bullet back into pool

                // Award points to the ScoreManager on hit
                if (gamePanel.getScoreManager() != null) {
                    gamePanel.getScoreManager().addScore(100);
                }
            }
        }
    }

    private void checkEnemyBulletCollisions() {
        if (playerCharacter == null || playerCharacter.isDead() || gamePanel.getBulletPool() == null) return;

        for (Bullet bullet : gamePanel.getBulletPool().getActiveBullets()) {
            if (!bullet.isActive()) continue;

            if (isCollidingCircle(bullet, playerCharacter.getHitCenterX(), playerCharacter.getHitCenterY(), PLAYER_HIT_RADIUS)) {
                playerCharacter.takeDamage(bullet.getDamagePacket().getAmount());
                bullet.setActive(false);

                // Reset multiplier when player gets hit
                if (gamePanel.getScoreManager() != null) {
                    gamePanel.getScoreManager().resetMultiplier();
                }
            }
        }
    }

    private void checkGrazeEvents() {
        if (playerCharacter == null || playerCharacter.isDead() || gamePanel.getBulletPool() == null) {
            return;
        }

        double px = playerCharacter.getX() + playerCharacter.getWidth() / 2;
        double py = playerCharacter.getY() + playerCharacter.getHeight() / 2;

        for (Bullet bullet : gamePanel.getBulletPool().getActiveBullets()) {
            if (!bullet.isActive() || bullet.isGrazed()) {
                continue;
            }

            double dx = bullet.getX() - px;
            double dy = bullet.getY() - py;
            double distance = Math.hypot(dx, dy);

            double hitBoundary = bullet.getRadius() + PLAYER_HIT_RADIUS;
            double grazeBoundary = hitBoundary + GRAZE_BUFFER;

            if (distance > hitBoundary && distance <= grazeBoundary) {
                bullet.setGrazed(true);
                playerCharacter.triggerGrazeFlash();
                if (gamePanel.getScoreManager() != null) {
                    gamePanel.getScoreManager().registerGraze();
                }
            }
        }
    }

    // Mathematical collision for Circle-to-Rectangle (Used for shooting bosses)
    private boolean isColliding(Bullet bullet, double rx, double ry, double rw, double rh) {
        double closestX = Math.clamp(bullet.getX(), rx, rx + rw);
        double closestY = Math.clamp(bullet.getY(), ry, ry + rh);

        double distX = bullet.getX() - closestX;
        double distY = bullet.getY() - closestY;

        return (distX * distX + distY * distY) < (bullet.getRadius() * bullet.getRadius());
    }

    private boolean isCollidingCircle(Bullet bullet, double cx, double cy, double radius) {
        double dx = bullet.getX() - cx;
        double dy = bullet.getY() - cy;
        double distanceSquared = (dx * dx) + (dy * dy);

        double radiusSum = bullet.getRadius() + radius;
        return distanceSquared < (radiusSum * radiusSum);
    }
}