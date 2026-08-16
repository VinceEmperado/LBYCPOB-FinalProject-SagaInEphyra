package core;

import entities.PlayerCharacter;
import entities.enemies.EnemyController;
import javafx.animation.AnimationTimer;

// Purpose of this class is so that regardless of the device frame rate, the movement speed of the entities will remain the same
public class GameLoopManager {
    private long lastTime = 0;
    private boolean running = false;
    private GamePanel gamePanel;
    private PlayerCharacter playerCharacter;
    private EnemyController enemy;
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

    public void start() {
        lastTime = 0;
        timer.start();
    }

    public void stop() {
        timer.stop();
    }

    // Updates the position of the player, enemy, and bullet entities
    private void update(double delta) {
        playerCharacter.update(delta, gamePanel.isUp(), gamePanel.isDown(), gamePanel.isLeft(), gamePanel.isRight(), gamePanel.getWidth(), gamePanel.getHeight(), gamePanel.isSlowDown());
        enemy.update(delta, gamePanel.getWidth(), gamePanel.getHeight());

        enemy.checkOrbCollisions(playerCharacter);

        if (gamePanel.getBulletPool() != null) {
            gamePanel.getBulletPool().update(delta, gamePanel.getWidth(), gamePanel.getHeight());
        }
    }
}