package core;

import javax.swing.*;

public class GameLauncher extends JFrame {
    public static void main(String[] args) {
        // Creates the window object, the title at the top bar will be Saga in Ephyra
        JFrame gameFrame = new JFrame("Saga in Ephyra");

        // Ensures that the window will close whenever the X button is pressed
        gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Set the dimensions of the window, in this case it will be 800x600
        gameFrame.setSize(800, 600);

        // The will force the window to stay on the same size, the user will not be able to resize it
        gameFrame.setResizable(false);

        // Centers the window on the screen
        gameFrame.setLocationRelativeTo(null);

        // Makes the window visible
        gameFrame.setVisible(true);

        gameFrame.setContentPane(new GamePanel());
    }
}
