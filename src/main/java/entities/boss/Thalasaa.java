package entities.boss;

import combat.*;
import core.AudioManager;
import entities.PlayerCharacter;
import entities.enemies.EnemyController;
import ui.DialogueSystem;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.io.InputStream;
import java.util.Random;

public class Thalasaa extends EnemyController {

    private final Random random = new Random();
    private final HealthSystem healthSystem;
    private PlayerCharacter player;
    private DialogueSystem dialogueSystem;

    private Image bulletSprite;

    private final double attackInterval = 0.9;
    private double attackCooldown = 0.0;
    private int attackPatternCycle = 0;
    private double moveTimer = 0.0;

    public Thalasaa(double startX, double startY) {
        this(startX, startY, 1000.0, null, null, null);
    }

    public Thalasaa(double startX, double startY, PatternSpawner patternSpawner, PlayerCharacter player) {
        this(startX, startY, 1000.0, patternSpawner, player, null);
    }

    public Thalasaa(double startX, double startY, PatternSpawner patternSpawner, PlayerCharacter player, DialogueSystem dialogueSystem) {
        this(startX, startY, 1000.0, patternSpawner, player, dialogueSystem);
    }

    public Thalasaa(double startX, double startY, double maxHealth, PatternSpawner patternSpawner, PlayerCharacter player, DialogueSystem dialogueSystem) {
        super(startX, startY, "/sprites/boss/Thalasaa.png", "/sprites/boss/ThalasaaAttack.png", patternSpawner);
        this.player = player;
        this.dialogueSystem = dialogueSystem;
        this.healthSystem = new HealthSystem(maxHealth);

        InputStream spriteStream = getClass().getResourceAsStream("/sprites/bullets/bubble.png");
        if (spriteStream != null) {
            this.bulletSprite = new Image(spriteStream);
        } else {
            System.err.println("Could not load bullet sprite for Thalasaa: Resource missing.");
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
        return "Drown beneath the pressure of the endless deep!";
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
            dialogueSystem.showMessage("Thalasaa", getEnragedDialogue(), null, 3.5);
        }
    }

    @Override
    protected void performAttackPattern(double delta, double panelWidth, double panelHeight) {
        if (isDead()) return;

        checkPhaseTransition();

        moveTimer += delta;
        double speedMult = (currentPhase == 1) ? 1.0 : 1.5;
        x = (panelWidth / 2.0 - width / 2.0) + Math.sin(moveTimer * 1.2 * speedMult) * (panelWidth * 0.3);
        y = 70.0 + Math.sin(moveTimer * 2.4 * speedMult) * 25.0;

        attackCooldown += delta;
        double currentInterval = (currentPhase == 1) ? attackInterval : attackInterval * 0.55;

        if (attackCooldown >= currentInterval) {
            attackCooldown = 0;

            switch (attackPatternCycle % 3) {
                case 0 -> fireBiteAttack();
                case 1 -> fireSpiralAttack();
                case 2 -> fireBombAttack();
            }

            attackPatternCycle++;
        }
    }

    private void fireBiteAttack() {
        if (patternSpawner == null) return;

        double centerX = x + (width / 2.0);
        double centerY = y + (height / 2.0);

        int toothCount = (currentPhase == 1) ? 6 : 10;
        double speed = 350.0;

        new JawBitePattern(toothCount, speed).execute(
                patternSpawner.getBulletPool(), centerX, centerY, bulletSprite, Color.DODGERBLUE
        );
    }

    private void fireSpiralAttack() {
        if (patternSpawner == null) return;

        double centerX = x + (width / 2.0);
        double centerY = y + (height / 2.0);

        int bulletCount = (currentPhase == 1) ? 24 : 36;
        double speed = 250.0;

        new SpiralPattern(bulletCount, speed, 0.08).execute(
                patternSpawner.getBulletPool(), centerX, centerY, bulletSprite, Color.CYAN
        );
    }

    private void fireBombAttack() {
        if (patternSpawner == null) return;

        double centerX = x + (width / 2.0);
        double centerY = y + (height / 2.0);

        int ringCount = (currentPhase == 1) ? 12 : 18;
        double speed = 220.0;

        new BombPattern(ringCount, speed).execute(
                patternSpawner.getBulletPool(), centerX, centerY, bulletSprite, Color.DEEPSKYBLUE
        );
    }

    @Override
    protected void renderSprite(GraphicsContext gc) {
        if (isDead()) return;
        super.renderSprite(gc);
    }
}