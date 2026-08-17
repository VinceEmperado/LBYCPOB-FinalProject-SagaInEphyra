package entities.enemies;

import combat.PatternSpawner;
import entities.DamageOrb;
import entities.PlayerCharacter;
import ui.DialogueSystem;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class EnemyController {
    protected double x, y;
    protected int width = 120, height = 120;
    protected Image enemySprite;
    protected PatternSpawner patternSpawner;
    protected PlayerCharacter player;
    protected DialogueSystem dialogueSystem;

    protected int currentPhase = 1;
    protected double survivalTimer = 0.0;
    protected double phaseOneDuration = 10.0;

    protected double maxHealth = 500.0;
    protected double currentHealth = 500.0;

    protected boolean isTransitioning = false;
    protected double dialogueTimer = 0.0;
    protected double dialogueDuration = 3.0;
    protected String currentDialogueText = "";

    protected List<DamageOrb> activeOrbs = new ArrayList<>();
    protected double orbSpawnTimer = 0.0;
    protected double orbSpawnInterval = 1.5;

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

    public void setPlayer(PlayerCharacter player) {
        this.player = player;
    }

    public void setDialogueSystem(DialogueSystem dialogueSystem) {
        this.dialogueSystem = dialogueSystem;
    }

    public DialogueSystem getDialogueSystem() {
        return dialogueSystem;
    }

    public void update(double delta, double panelWidth, double panelHeight) {
        if (isDead()) return;

        if (isTransitioning) {
            dialogueTimer += delta;
            if (dialogueTimer >= dialogueDuration) {
                isTransitioning = false;
                dialogueTimer = 0;
                currentPhase = 2;
            }
            return;
        }

        // Phase 1 timer OR health threshold triggers phase transition
        if (currentPhase == 1) {
            survivalTimer += delta;
            if (survivalTimer >= phaseOneDuration || currentHealth <= (maxHealth / 2.0)) {
                triggerPhaseTransition(getEnragedDialogue());
            }
        }

        if (currentPhase == 2 && currentHealth > 0 && !isTransitioning) {
            orbSpawnTimer += delta;

            if (orbSpawnTimer >= orbSpawnInterval) {
                double dropX = new Random().nextInt(Math.max(1, (int) panelWidth - 30));
                double dropY = new Random().nextInt(Math.max(1, (int) panelHeight - 30));

                activeOrbs.add(new DamageOrb(dropX, dropY));
                orbSpawnTimer = 0;
            }
        }

        performAttackPattern(delta, panelWidth, panelHeight);
    }

    protected void triggerPhaseTransition(String dialogueText) {
        isTransitioning = true;
        currentDialogueText = dialogueText;

        if (dialogueSystem != null) {
            dialogueSystem.showMessage("Enemy", dialogueText, null, dialogueDuration);
        }
    }

    public void checkOrbCollisions(PlayerCharacter player) {
        if (player == null || currentPhase != 2 || isDead()) return;

        for (int i = activeOrbs.size() - 1; i >= 0; i--) {
            DamageOrb orb = activeOrbs.get(i);

            double dx = (player.getX() + player.getWidth() / 2.0) - orb.getX();
            double dy = (player.getY() + player.getHeight() / 2.0) - orb.getY();
            double distance = Math.hypot(dx, dy);

            if (distance < (player.getWidth() / 2.0 + orb.getRadius())) {
                takeDamage(orb.getDamageAmount());
                activeOrbs.remove(i);
            }
        }
    }

    protected abstract void performAttackPattern(double delta, double panelWidth, double panelHeight);
    protected abstract String getEnragedDialogue();

    public void render(GraphicsContext gc) {
        if (isDead()) return;

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
        for (DamageOrb orb : activeOrbs) {
            orb.render(gc);
        }

        if (isTransitioning && dialogueSystem == null) {
            gc.setFill(Color.WHITE);
            gc.setFont(Font.font("Arial", FontWeight.BOLD, 14));
            gc.fillText(currentDialogueText, x - 30, y - 20);
        }
    }

    public void takeDamage(double amount) {
        if (!isTransitioning) {
            currentHealth = Math.max(0, currentHealth - amount);
        }
    }

    public void takeDamage(int amount) {
        takeDamage((double) amount);
    }

    public boolean isDead() {
        return currentHealth <= 0;
    }

    public int getCurrentPhase() { return currentPhase; }
    public double getHealthPercentage() { return maxHealth > 0 ? (currentHealth / maxHealth) : 0.0; }
    public double getX() { return x; }
    public double getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public double getCurrentHealth() { return currentHealth; }
    public double getMaxHealth() { return maxHealth; }
}