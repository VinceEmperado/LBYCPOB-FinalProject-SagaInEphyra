package core;

import entities.EnemyController;
import entities.PlayerCharacter;

import javax.swing.*;

public class GameLauncher extends JFrame {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            PlayerCharacter player = new PlayerCharacter(780, 800);
            EnemyController enemy = new EnemyController(100, 150);
            GamePanel gamePanel = new GamePanel(player, enemy);
            GameLoopManager loopManager = new GameLoopManager(gamePanel, player, enemy);

            // Creates the window object, the title at the top bar will be Saga in Ephyra
            JFrame gameFrame = new JFrame("Saga in Ephyra");

            // Ensures that the window will close whenever the X button is pressed
            gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            // Set the dimensions of the window, in this case it will be 800x600
            gameFrame.setSize(1600, 900);

            // The will force the window to stay on the same size, the user will not be able to resize it
            gameFrame.setResizable(false);

            // Centers the window on the screen
            gameFrame.setLocationRelativeTo(null);


            gameFrame.setContentPane(gamePanel);
            ;
            // Makes the window visible
            gameFrame.setVisible(true);
            gamePanel.requestFocusInWindow();

            loopManager.start();
        });
    }
}
