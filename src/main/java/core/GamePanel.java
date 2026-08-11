package core;

import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel {

    public GamePanel() {
        setDoubleBuffered(true);
        setPreferredSize(new Dimension(800,600));
        setFocusable(true);
    }
    public void paintComponent(Graphics g) {
        g.setColor(Color.RED);
        g.fillRect(100, 100, 10, 10);
    }
}
