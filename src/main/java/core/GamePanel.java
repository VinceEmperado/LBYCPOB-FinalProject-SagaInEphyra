package core;

import entities.EnemyController;
import entities.PlayerCharacter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.Color;

public class GamePanel extends JPanel {
    private boolean up, down, left, right, slowDown;
    private PlayerCharacter playerCharacter;
    private EnemyController enemy;

    public GamePanel(PlayerCharacter playerCharacter, EnemyController enemy) {
        this.playerCharacter = playerCharacter;
        this.enemy = enemy;
        this.setBackground(Color.BLACK);
        setDoubleBuffered(true);
        setPreferredSize(new Dimension(800,600));
        setFocusable(true);
        setupKeyBindings();
    }

    // This method takes the key input of the user and performs an action depending on which key is pressed or released.
    private void setupKeyBindings() {
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP -> up = true; // Set up arrow key to make the player entity move north
                    case KeyEvent.VK_DOWN -> down = true; // Set down arrow key to make the player entity move south
                    case KeyEvent.VK_LEFT -> left = true; // Set left arrow key to make the player entity move west
                    case KeyEvent.VK_RIGHT -> right = true; // Set right arrow key to make the player entity move east
                    case KeyEvent.VK_SHIFT -> slowDown = true; // Holding shift will make the player entity slow down
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                switch (e.getKeyCode()) {
                    // Releasing any of these keys stops the player entity from moving in that direction
                    case KeyEvent.VK_UP -> up = false;
                    case KeyEvent.VK_DOWN -> down = false;
                    case KeyEvent.VK_LEFT -> left = false;
                    case KeyEvent.VK_RIGHT -> right = false;
                    case KeyEvent.VK_SHIFT -> slowDown = false;
                }
            }

        });
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        playerCharacter.render(g2d);
        enemy.render(g2d);
    }

    // Getters for the methods that help the player entity move in the PlayerCharacter class
    public boolean isUp() {
        return up;
    }

    public boolean isDown() {
        return down;
    }

    public boolean isLeft() {
        return left;
    }

    public boolean isRight() {
        return right;
    }

    public boolean isSlowDown() {
        return slowDown;
    }
}
