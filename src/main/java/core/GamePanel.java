package core;

import entities.PlayerCharacter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class GamePanel extends JPanel {
    private boolean up, down, left, right;
    private PlayerCharacter playerCharacter;

    public GamePanel(PlayerCharacter playerCharacter) {
        this.playerCharacter = playerCharacter;
        setDoubleBuffered(true);
        setPreferredSize(new Dimension(800,600));
        setFocusable(true);
        setupKeyBindings();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        playerCharacter.render(g2d);
    }

    private void setupKeyBindings() {
        InputMap inputMap = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = getActionMap();

        // Map W to make the player entity move upwards.
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_W, 0, false), "moveUpPressed");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_W, 0, true), "moveUpReleased");
        actionMap.put("moveUpPressed", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                up = true;
            }
        });
        actionMap.put("moveUpReleased", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                up = false;
            }
        });

        // Map S to make the player entity move down
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, 0, false), "moveDownPressed");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, 0, true), "moveDownReleased");
        actionMap.put("moveDownPressed", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                down = true;
            }
        });
        actionMap.put("moveDownReleased", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                down = false;
            }
        });

        // Map A to make the player entity move left
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_A, 0, false), "moveLeftPressed");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_A, 0, true), "moveLeftReleased");
        actionMap.put("moveLeftPressed", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                left = true;
            }
        });
        actionMap.put("moveLeftReleased", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                left = false;
            }
        });

        // Map D to make the player entity move right
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_D, 0, false), "moveRightPressed");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_D, 0, true), "moveRightReleased");
        actionMap.put("moveRightPressed", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                right = true;
            }
        });
        actionMap.put("moveRightReleased", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                right = false;
            }
        });
    }
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
}
