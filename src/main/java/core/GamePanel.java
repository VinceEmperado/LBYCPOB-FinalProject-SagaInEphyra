package core;

import entities.EnemyController;
import entities.PlayerCharacter;
import pools.BulletPool;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;

public class GamePanel extends Canvas {
    private PlayerCharacter player;
    private EnemyController enemy;
    private BulletPool bulletPool;

    private boolean up, down, left, right, slowDown;

    public GamePanel(PlayerCharacter player, EnemyController enemy, BulletPool bulletPool) {
        super(1600, 900);
        this.player = player;
        this.enemy = enemy;
        this.bulletPool = bulletPool;

        setFocusTraversable(true);

        setOnKeyPressed(this::keyPressed);
        setOnKeyReleased(this::keyReleased);
    }

    public void render() {
        GraphicsContext gc = getGraphicsContext2D();

        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, getWidth(), getHeight());

        // Render enemy and active bullets
        if (enemy != null) {
            enemy.render(gc);
        }
        if (player != null) {
            player.render(gc);
        }

        if (bulletPool != null) {
            bulletPool.render(gc);
        }
    }

    public void keyPressed(KeyEvent e) {
        KeyCode code = e.getCode();
        if (code == KeyCode.W || code == KeyCode.UP) up = true;
        if (code == KeyCode.S || code == KeyCode.DOWN) down = true;
        if (code == KeyCode.A || code == KeyCode.LEFT) left = true;
        if (code == KeyCode.D|| code == KeyCode.RIGHT) right = true;
        if (code == KeyCode.SHIFT) slowDown = true;
    }

    public void keyReleased(KeyEvent e) {
        KeyCode code = e.getCode();
        if (code == KeyCode.W || code == KeyCode.UP) up = false;
        if (code == KeyCode.S || code == KeyCode.DOWN) down = false;
        if (code == KeyCode.A || code == KeyCode.LEFT) left = false;
        if (code == KeyCode.D|| code == KeyCode.RIGHT) right = false;
        if (code == KeyCode.SHIFT) slowDown = false;
    }

    public boolean isUp() { return up; }
    public boolean isDown() { return down; }
    public boolean isLeft() { return left; }
    public boolean isRight() { return right; }
    public boolean isSlowDown() { return slowDown; }

    public BulletPool getBulletPool() { return bulletPool; }
    public EnemyController getEnemy() { return enemy; }
    public PlayerCharacter getPlayer() { return player; }
}