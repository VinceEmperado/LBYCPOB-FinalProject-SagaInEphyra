package core;

import entities.PlayerCharacter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

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
        inputMap.put(KeyStroke.getKeyStroke("pressed W"), "moveUpPressed");
        inputMap.put(KeyStroke.getKeyStroke("released W"), "moveUpReleased");
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
        inputMap.put(KeyStroke.getKeyStroke("pressed S"), "moveDownPressed");
        inputMap.put(KeyStroke.getKeyStroke("released S"), "moveDownReleased");
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

    }
    public boolean isUp() {
        return up;
    }

    public boolean isDown() {
        return down;
    }

}
