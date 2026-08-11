package core;

import java.awt.*;
import core.GamePanel;
import entities.PlayerCharacter;

// Purpose of this class is so that regardless of the device frame rate, the movement speed of the entities will remain the same
public class GameLoopManager implements Runnable{
    private long lastTime = System.nanoTime();
    private boolean running = false;
    private GamePanel gamePanel;
    private Thread thread;
    private PlayerCharacter playerCharacter;

    public GameLoopManager(GamePanel gamePanel, PlayerCharacter playerCharacter) {
        this.gamePanel = gamePanel;
        this.playerCharacter = playerCharacter;
    }


    public void start() {
        running = true;
        thread = new Thread(this);
        thread.start();
    }

    public void stop() {
        running = false;
    }
    
    @Override
    public void run() {
        while (running) {
            long now = System.nanoTime();
            double delta = (now - lastTime) / 1_000_000_000.0;
            lastTime = now;

            update(delta);
            gamePanel.repaint();
            sleepUntilNextFrame();
        }
    }

    private void update(double delta) {
        playerCharacter.update(delta, gamePanel.isUp(), gamePanel.isDown(), gamePanel.isLeft(), gamePanel.isRight(), gamePanel.getWidth(), gamePanel.getHeight());
    }

    private void sleepUntilNextFrame() {
        try {
            Thread.sleep(16); // Should be 60fps
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
