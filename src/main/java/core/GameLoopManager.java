package core;

import java.awt.*;
import core.GamePanel;
import static jdk.jfr.internal.consumer.EventLog.update;

// Purpose of this class is so that regardless of the device frame rate, the movement speed of the entities will remain the same
public class GameLoopManager implements Runnable{
    private long lastTime = System.nanoTime();
    private boolean running = false;
    private GamePanel gamePanel;
    private Thread thread;

    public GameLoopManager(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
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
        }
    }

    private void update(double delta) {
    } // Leave empty for now, we will put individual .update for the entities

    private void sleepUntilNextFrame() {
        try {
            Thread.sleep(16); // Should be 60fps
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
