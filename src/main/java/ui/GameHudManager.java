package ui;

import core.ScoreManager;
import entities.PlayerCharacter;
import entities.enemies.EnemyController;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class GameHudManager {

    private final ScoreManager scoreManager;

    public GameHudManager(ScoreManager scoreManager) {
        this.scoreManager = scoreManager;
    }

    public void render(GraphicsContext gc, double panelWidth, double panelHeight, PlayerCharacter player, EnemyController enemy) {
        gc.save();

        if (enemy != null && !enemy.isDead()) {
            renderBossHealthBar(gc, enemy, panelWidth);
        }

        renderBottomRightHudBox(gc, player, panelWidth, panelHeight);

        gc.restore();
    }

    private void renderBossHealthBar(GraphicsContext gc, EnemyController enemy, double panelWidth) {
        double barWidth = panelWidth * 0.60;
        double barHeight = 14;
        double barX = (panelWidth - barWidth) / 2.0;
        double barY = 20;

        double healthPercent = Math.max(0.0, enemy.getHealthPercentage());

        // Background Track
        gc.setFill(Color.rgb(20, 20, 20, 0.8));
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(1.5);
        gc.fillRect(barX, barY, barWidth, barHeight);
        gc.strokeRect(barX, barY, barWidth, barHeight);

        // Fill Color
        Color fillColor = (enemy.getCurrentPhase() == 1) ? Color.web("#ff2a2a") : Color.web("#ff9900");
        gc.setFill(fillColor);
        gc.fillRect(barX + 2, barY + 2, (barWidth - 4) * healthPercent, barHeight - 4);

        // Header & Phase
        gc.setFont(Font.font("Consolas", FontWeight.BOLD, 14));
        gc.setFill(Color.WHITE);
        gc.fillText(enemy.getClass().getSimpleName().toUpperCase(), barX, barY - 6);

        gc.setFill(Color.web("#00ffff"));
        gc.fillText("PHASE " + enemy.getCurrentPhase(), barX + barWidth - 65, barY - 6);
    }

    /**
     * Compact, self-contained Bottom-Right HUD Box displaying Score, Lives, and Multipliers.
     */
    private void renderBottomRightHudBox(GraphicsContext gc, PlayerCharacter player, double panelWidth, double panelHeight) {
        double boxWidth = 280;
        double boxHeight = 160;
        double margin = 20;

        double boxX = panelWidth - boxWidth - margin;
        double boxY = panelHeight - boxHeight - margin;

        // Dark Translucent Box Background with Border
        gc.setFill(Color.rgb(15, 15, 25, 0.85));
        gc.fillRect(boxX, boxY, boxWidth, boxHeight);
        gc.setStroke(Color.web("#00ffcc"));
        gc.setLineWidth(2.0);
        gc.strokeRect(boxX, boxY, boxWidth, boxHeight);

        double textX = boxX + 15;
        double currentY = boxY + 25;

        // --- SCORES ---
        gc.setFont(Font.font("Consolas", FontWeight.BOLD, 12));
        gc.setFill(Color.web("#8888aa"));
        gc.fillText("SCORE", textX, currentY);

        gc.setFont(Font.font("Consolas", FontWeight.BOLD, 15));
        gc.setFill(Color.web("#00ffcc"));
        gc.fillText(String.format("%09d", scoreManager.getScore()), textX + 55, currentY);

        currentY += 22;
        gc.setFont(Font.font("Consolas", FontWeight.BOLD, 12));
        gc.setFill(Color.web("#8888aa"));
        gc.fillText("HIGH", textX, currentY);

        gc.setFont(Font.font("Consolas", FontWeight.BOLD, 15));
        gc.setFill(Color.WHITE);
        gc.fillText(String.format("%09d", scoreManager.getHighScore()), textX + 55, currentY);

        // Divider Line
        currentY += 12;
        gc.setStroke(Color.rgb(255, 255, 255, 0.2));
        gc.strokeLine(textX, currentY, boxX + boxWidth - 15, currentY);

        // --- PLAYER LIVES & HP ---
        currentY += 22;
        gc.setFont(Font.font("Consolas", FontWeight.BOLD, 12));
        gc.setFill(Color.web("#ff4466"));
        gc.fillText("LIVES", textX, currentY);

        if (player != null) {
            int lives = player.getLives();
            StringBuilder lifeIcons = new StringBuilder();
            for (int i = 0; i < lives; i++) {
                lifeIcons.append("♥ ");
            }

            gc.setFont(Font.font("Arial", FontWeight.BOLD, 16));
            gc.setFill(Color.web("#ff2a6d"));
            gc.fillText(lifeIcons.toString(), textX + 55, currentY);

            // Health percentage text
            gc.setFont(Font.font("Consolas", FontWeight.BOLD, 12));
            gc.setFill(Color.WHITE);
            gc.fillText(String.format("HP: %.0f%%", player.getHealth()), textX + 180, currentY);
        }

        // --- MULTIPLIER ---
        currentY += 24;
        gc.setFont(Font.font("Consolas", FontWeight.BOLD, 12));
        gc.setFill(Color.web("#8888aa"));
        gc.fillText("MULT", textX, currentY);

        gc.setFont(Font.font("Consolas", FontWeight.BOLD, 14));
        gc.setFill(Color.YELLOW);
        gc.fillText(String.format("%.2fx", scoreManager.getMultiplier()), textX + 55, currentY);
    }
}