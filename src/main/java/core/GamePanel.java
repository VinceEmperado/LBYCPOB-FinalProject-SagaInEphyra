package core;

import entities.PlayerCharacter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class GamePanel extends JPanel {
    private boolean up, down, left, right, slowDown;
    private PlayerCharacter playerCharacter;

    public GamePanel(PlayerCharacter playerCharacter) {
        this.playerCharacter = playerCharacter;
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
                    case KeyEvent.VK_W -> up = true; // W is bound to up
                    case KeyEvent.VK_S -> down = true; // S is bound to down
                    case KeyEvent.VK_A -> left = true; // A is bound to left
                    case KeyEvent.VK_D -> right = true; // D is bound to right
                    case KeyEvent.VK_SHIFT -> slowDown = true; // Holding shift will make the player entity slow down
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                switch (e.getKeyCode()) {
                    // Releasing any of these keys stops the player entity from moving in that direction
                    case KeyEvent.VK_W -> up = false;
                    case KeyEvent.VK_S -> down = false;
                    case KeyEvent.VK_A -> left = false;
                    case KeyEvent.VK_D -> right = false;
                    case KeyEvent.VK_SHIFT -> slowDown = false;
                }
            }

        });
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        playerCharacter.render(g2d);
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
