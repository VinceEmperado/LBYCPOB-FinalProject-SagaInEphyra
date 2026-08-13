package core;

import entities.EnemyController;
import entities.PlayerCharacter;
import pools.BulletPool;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class GamePanel extends JPanel implements KeyListener {
    private PlayerCharacter player;
    private EnemyController enemy;
    private BulletPool bulletPool;

    private boolean up, down, left, right, slowDown;

    public GamePanel(PlayerCharacter player, EnemyController enemy, BulletPool bulletPool) {
        this.player = player;
        this.enemy = enemy;
        this.bulletPool = bulletPool;

        setPreferredSize(new Dimension(1600, 900));
        setBackground(Color.BLACK);
        setDoubleBuffered(true);
        setFocusable(true);

        addKeyListener(this);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // Render enemy and active bullets
        if (enemy != null) {
            enemy.render(g2d);
        }

        if (bulletPool != null) {
            bulletPool.render(g2d);
        }

        // Render player if method exists
        //if (player != null) {
        //    player.render(g2d);
        //}

        g2d.dispose();
    }


    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) up = true;
        if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) down = true;
        if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) left = true;
        if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) right = true;
        if (code == KeyEvent.VK_SHIFT) slowDown = true;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();
        if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) up = false;
        if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) down = false;
        if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) left = false;
        if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) right = false;
        if (code == KeyEvent.VK_SHIFT) slowDown = false;
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // Not used
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