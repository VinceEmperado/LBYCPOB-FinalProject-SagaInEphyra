package entities.enemies;

import combat.HealthSystem;
import combat.PatternSpawner;
import core.AudioManager;
import entities.PlayerCharacter;
import ui.DialogueSystem;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.io.InputStream;
import java.util.Random;

public class Finana extends EnemyController {

    private final Random random = new Random();
    private final HealthSystem healthSystem;
    private PlayerCharacter player;
    private DialogueSystem dialogueSystem;

    private Image bulletSprite;

    private final double attackInterval = 0.85;
    private double attackCooldown = 0.0;
    private int attackPatternCycle = 0;

    private double swimTimer = 0.0;
    private double swimDirection = 1.0;

    public Finana(double startX, double startY) {
        this(startX, startY, 600.0, null, null, null);
    }

    public Finana(double startX, double startY, PatternSpawner patternSpawner, PlayerCharacter player) {
        this(startX, startY, 600.0, patternSpawner, player, null);
    }

    public Finana(double startX, double startY, PatternSpawner patternSpawner, PlayerCharacter player, DialogueSystem dialogueSystem) {
        this(startX, startY, 600.0, patternSpawner, player, dialogueSystem);
    }

    public Finana(double startX, double startY, double maxHealth, PatternSpawner patternSpawner, PlayerCharacter player, DialogueSystem dialogueSystem) {
        super(startX, startY, "/sprites/enemy/finana.png", patternSpawner);
        this.player = player;
        this.dialogueSystem = dialogueSystem;
        this.healthSystem = new HealthSystem(maxHealth);

        InputStream spriteStream = getClass().getResourceAsStream("/sprites/bullets/fin.png");
        if (spriteStream != null) {
            this.bulletSprite = new Image(spriteStream);
        } else {
            System.err.println("Could not load bullet sprite for Finana: Resource missing.");
        }
    }

    public void setPlayer(PlayerCharacter player) {
        this.player = player;
    }

    public void setDialogueSystem(DialogueSystem dialogueSystem) {
        this.dialogueSystem = dialogueSystem;
    }

    @Override
    protected String getEnragedDialogue() {
        return "You can't outswim the current!";
    }

    public void takeDamage(double amount) {
        healthSystem.takeDamage(amount);
        AudioManager.getInstance().playSFX("/audio/sfx/boss_hit.wav");
        checkPhaseTransition();
    }

    public boolean isDead() {
        return healthSystem.isDead();
    }

    public HealthSystem getHealthSystem() {
        return healthSystem;
    }

    private void checkPhaseTransition() {
        if (currentPhase == 1 && healthSystem.getHealthPercentage() <= 0.5) {
            currentPhase = 2;
            triggerPhaseDialogue();
        }
    }

    private void triggerPhaseDialogue() {
        if (dialogueSystem != null) {
            dialogueSystem.showMessage("Finana", getEnragedDialogue(), null, 3.0);
        }
    }

    @Override
    protected void performAttackPattern(double delta, double panelWidth, double panelHeight) {
        if (isDead()) return;

        checkPhaseTransition();

        swimTimer += delta;
        double swimSpeed = (currentPhase == 1) ? 190.0 : 270.0;
        x += swimDirection * swimSpeed * delta;
        y = 90.0 + Math.sin(swimTimer * 6.0) * 30.0;

        if (x <= 20) {
            x = 20;
            swimDirection = 1.0;
        } else if (x >= panelWidth - width - 20) {
            x = panelWidth - width - 20;
            swimDirection = -1.0;
        }

        attackCooldown += delta;
        double currentInterval = (currentPhase == 1) ? attackInterval : attackInterval * 0.5;

        if (attackCooldown >= currentInterval) {
            attackCooldown = 0;

            switch (attackPatternCycle % 3) {
                case 0 -> fireFinSlices();
                case 1 -> fireStreamFan();
                case 2 -> fireHydroBurst();
            }

            attackPatternCycle++;
        }
    }

    private void fireFinSlices() {
        if (patternSpawner == null) return;

        double centerX = x + (width / 2.0);
        double centerY = y + (height / 2.0);

        int bulletCount = (currentPhase == 1) ? 5 : 8;
        double speed = 240.0;
        double arcAngle = Math.toRadians(75);

        patternSpawner.spawnClaw(
                centerX, centerY, bulletCount,
                speed, arcAngle, player, bulletSprite, Color.LIGHTBLUE
        );
    }

    private void fireStreamFan() {
        if (patternSpawner == null) return;

        double centerX = x + (width / 2.0);
        double centerY = y + (height / 2.0);

        int bulletsPerSide = (currentPhase == 1) ? 3 : 5;
        double bulletSpeed = 220.0;
        double spreadAngle = Math.toRadians(45);

        patternSpawner.spawnPincer(
                centerX, centerY, bulletsPerSide,
                bulletSpeed, spreadAngle, player, bulletSprite, Color.AQUA
        );
    }

    private void fireHydroBurst() {
        if (patternSpawner == null) return;

        double centerX = x + (width / 2.0);
        double centerY = y + (height / 2.0);

        int bulletCount = (currentPhase == 1) ? 10 : 16;
        double speed = 190.0;

        patternSpawner.spawnClaw(
                centerX, centerY, bulletCount,
                speed, Math.toRadians(360), player, bulletSprite, Color.TEAL
        );
    }

    @Override
    protected void renderSprite(GraphicsContext gc) {
        if (isDead()) return;
        super.renderSprite(gc);
    }
}