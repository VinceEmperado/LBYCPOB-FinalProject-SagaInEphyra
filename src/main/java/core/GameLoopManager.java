package core;

import java.awt.*;
import javafx.animation.AnimationTimer;
import entities.PlayerCharacter;
import entities.EnemyController;

// Purpose of this class is so that regardless of the device frame rate, the movement speed of the entities will remain the same
public class GameLoopManager {
    private long lastTime = -1;
    private AnimationTimer timer;
    private final GamePanel gamePanel;
    private PlayerCharacter playerCharacter;
    private EnemyController enemy;

    public GameLoopManager(GamePanel gamePanel, PlayerCharacter playerCharacter, EnemyController enemy) {
        this.gamePanel = gamePanel;
        this.playerCharacter = playerCharacter;
        this.enemy = enemy;
    }


    public void start() {
        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (lastTime < 0) {
                    lastTime = now;
                    return;
                }
                double delta = (now - lastTime) / 1000000000.0;
                lastTime = now;

                update(delta);
                gamePanel.render();
            }
        };
        timer.start();
    }

    public void stop() {
        if (timer != null) {
            timer.stop();
        }
    }

    private void update(double delta) {
        int panelWidth = gamePanel.getPanelWidth();
        int panelHeight = gamePanel.getPanelHeight();

        playerCharacter.update(delta, gamePanel.isUp(), gamePanel.isDown(), gamePanel.isLeft(), gamePanel.isRight(), panelWidth, panelHeight, gamePanel.isSlowDown());
        enemy.update(delta, panelWidth, panelHeight);
    }
}
