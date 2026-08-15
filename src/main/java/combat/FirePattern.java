package combat;

import pools.BulletPool;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public interface FirePattern {
    void execute(BulletPool pool, double originX, double originY, Image sprite, Color fallbackColor);
}