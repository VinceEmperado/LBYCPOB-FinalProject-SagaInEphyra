package entities;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import java.util.Random;

public class DamageOrb {
    private double x, y;
    private double radius = 12.0;
    private int damageAmount;
    private Random random = new Random();

    public DamageOrb(double x, double y) {
        this.x = x;
        this.y = y;

        this.damageAmount = random.nextInt(31) + 30;
    }

    public void render(GraphicsContext gc) {
        gc.setFill(Color.CYAN);
        gc.fillOval(x - radius, y - radius, radius * 2, radius * 2);
        gc.setFill(Color.WHITE);
        gc.fillOval(x - (radius/2), y - (radius/2), radius, radius);
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public double getRadius() { return radius; }
    public int getDamageAmount() { return damageAmount; }
}