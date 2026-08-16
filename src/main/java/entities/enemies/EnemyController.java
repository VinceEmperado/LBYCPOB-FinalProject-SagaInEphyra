package entities.enemies;

import combat.PatternSpawner;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public abstract class EnemyController {
    protected double x, y;
    protected int width = 120, height = 120;
    protected Image enemySprite;
    protected PatternSpawner patternSpawner;

    protected int currentPhase = 1;
    protected double survivalTimer = 0.0;
    protected double phaseOneDuration = 10.0;

    protected int maxHealth = 50;
    protected int currentHealth = 50;

    protected boolean isTransitioning = false;
    protected double dialogueTimer = 0.0;
    protected double dialogueDuration = 3.0;
    protected String currentDialogueText = "";

    public EnemyController(double startX, double startY, String imagePath, PatternSpawner patternSpawner) {
        this.x = startX;
        this.y = startY;
        this.patternSpawner = patternSpawner;

        try {
            if (imagePath != null) {
                enemySprite = new Image(getClass().getResourceAsStream(imagePath));
            }
        } catch (Exception e) {
            System.out.println("Custom enemy sprite not found. Defaulting to fallback.");
        }

        if (enemySprite == null) {
            try {
                enemySprite = new Image(getClass().getResourceAsStream("/sprites/boss/mart.png"));
            } catch (Exception ex) {
                System.err.println("Could not load fallback sprite.");
            }
        }
    }

    public void update(double delta, double panelWidth, double panelHeight) {
        if (isTransitioning) {
            dialogueTimer += delta;
            if (dialogueTimer >= dialogueDuration) {
                isTransitioning = false;
                dialogueTimer = 0;
                currentPhase = 2;
            }
            return;
        }

        if (currentPhase == 1) {
            survivalTimer += delta;
            if (survivalTimer >= phaseOneDuration) {
                isTransitioning = true;
                currentDialogueText = getEnragedDialogue();
            }
        }

        else if (currentPhase == 2 && currentHealth <= 0) {
            // TODO: Death logic (e.g., set isDead flag, play animation)
        }

        performAttackPattern(delta, panelWidth, panelHeight);
    }

    protected abstract void performAttackPattern(double delta, double panelWidth, double panelHeight);
    protected abstract String getEnragedDialogue();

    public void render(GraphicsContext gc) {
        renderSprite(gc);
        renderUI(gc);
    }

    protected void renderSprite(GraphicsContext gc) {
        if (enemySprite != null) {
            gc.drawImage(enemySprite, x, y, width, height);
        } else {
            gc.setFill(Color.ORANGE);
            gc.fillRect(x, y, width, height);
        }
    }

    protected void renderUI(GraphicsContext gc) {
        if (currentPhase == 2 && !isTransitioning) {
            gc.setFill(Color.RED);
            gc.fillRect(x, y - 15, width, 8);
            gc.setFill(Color.GREEN);
            double healthWidth = ((double) currentHealth / maxHealth) * width;
            gc.fillRect(x, y - 15, healthWidth, 8);
        }

        if (isTransitioning) {
            gc.setFill(Color.WHITE);
            gc.setFont(Font.font("Arial", FontWeight.BOLD, 14));
            gc.fillText(currentDialogueText, x - 30, y - 20);
        }
    }

    public void takeDamage(int amount) {
        if (currentPhase == 2 && !isTransitioning) {
            currentHealth -= amount;
            if (currentHealth < 0) currentHealth = 0;
        }
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
}