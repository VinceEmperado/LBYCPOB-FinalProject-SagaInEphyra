package combat;

import pools.BulletPool;
import java.awt.Color;
import java.awt.image.BufferedImage;

public interface FirePattern {
    void execute(BulletPool pool, double originX, double originY, BufferedImage sprite, Color fallbackColor);
}