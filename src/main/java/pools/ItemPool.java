package pools;

import entities.PlayerCharacter;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.ArrayList;
import java.util.List;

public class ItemPool {

    public enum ItemType {
        EXTRA_LIFE,
        HEAL,
        POWER_UP
    }

    public static class Item {
        private static final double WIDTH = 28;
        private static final double HEIGHT = 28;
        private static final double SPEED_Y = 100.0; // Gentle falling speed

        private double x, y;
        private ItemType type;
        private boolean active = false;

        public void spawn(double x, double y, ItemType type) {
            this.x = x;
            this.y = y;
            this.type = type;
            this.active = true;
        }

        public void update(double deltaTime) {
            if (!active) return;

            y += SPEED_Y * deltaTime;

            // Deactivate when off-screen
            if (y > 950) {
                active = false;
            }
        }

        public boolean checkCollision(PlayerCharacter player) {
            if (!active) return false;

            double playerX = player.getX();
            double playerY = player.getY();

            // Distance-based circle collision with player center
            double dx = x - playerX;
            double dy = y - playerY;
            double distance = Math.sqrt(dx * dx + dy * dy);

            return distance < 35.0; // Pickup radius
        }

        public void render(GraphicsContext gc) {
            if (!active) return;

            gc.save();

            Color itemColor;
            String symbol;

            switch (type) {
                case EXTRA_LIFE -> {
                    itemColor = Color.web("#ff0055"); // Neon Pink/Red
                    symbol = "♥";
                }
                case HEAL -> {
                    itemColor = Color.web("#00ffcc"); // Cyan/Green
                    symbol = "+";
                }
                case POWER_UP -> {
                    itemColor = Color.web("#ffcc00"); // Gold
                    symbol = "★";
                }
                default -> {
                    itemColor = Color.WHITE;
                    symbol = "?";
                }
            }

            // Item Aura
            gc.setFill(itemColor);
            gc.fillOval(x - WIDTH / 2, y - HEIGHT / 2, WIDTH, HEIGHT);

            // Border Ring
            gc.setStroke(Color.WHITE);
            gc.setLineWidth(2.0);
            gc.strokeOval(x - WIDTH / 2, y - HEIGHT / 2, WIDTH, HEIGHT);

            // Icon Symbol
            gc.setFill(Color.BLACK);
            gc.setFont(Font.font("Consolas", FontWeight.BOLD, 16));
            gc.fillText(symbol, x - 5, y + 5);

            gc.restore();
        }

        public boolean isActive() {
            return active;
        }

        public void setActive(boolean active) {
            this.active = active;
        }

        public ItemType getType() {
            return type;
        }
    }

    private final List<Item> pool;

    public ItemPool(int capacity) {
        pool = new ArrayList<>(capacity);
        for (int i = 0; i < capacity; i++) {
            pool.add(new Item());
        }
    }

    public void spawnItem(double x, double y, ItemType type) {
        for (Item item : pool) {
            if (!item.isActive()) {
                item.spawn(x, y, type);
                break;
            }
        }
    }

    /**
     * Call this when a boss is defeated to drop loot items.
     */
    public void spawnBossLoot(double x, double y) {
        spawnItem(x - 40, y, ItemType.HEAL);
        spawnItem(x, y - 20, ItemType.POWER_UP);
        spawnItem(x + 40, y, ItemType.EXTRA_LIFE);
    }

    /**
     * Updates falling items and triggers collision effects with the player.
     */
    public void update(double deltaTime, PlayerCharacter player) {
        for (Item item : pool) {
            if (!item.isActive()) continue;

            item.update(deltaTime);

            if (item.checkCollision(player)) {
                applyEffect(item.getType(), player);
                item.setActive(false);
            }
        }
    }

    private void applyEffect(ItemType type, PlayerCharacter player) {
        switch (type) {
            case EXTRA_LIFE -> player.addLife(1);
            case HEAL -> player.heal(50);
            case POWER_UP -> player.increaseBulletDamage(5);
        }
    }

    public void render(GraphicsContext gc) {
        for (Item item : pool) {
            if (item.isActive()) {
                item.render(gc);
            }
        }
    }

    public void clear() {
        for (Item item : pool) {
            item.setActive(false);
        }
    }
}