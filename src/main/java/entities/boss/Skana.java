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

public class Skana extends EnemyController {

    private final Random random = new Random();
    private final HealthSystem healthSystem;
    private PlayerCharacter player;
    private DialogueSystem dialogueSystem;

    private Image bulletSprite;

    private final double attackIntervalPhase1 = 1.4;
    private final double attackIntervalPhase2 = 1.0;

    private double attackCooldown = 0.0;
    private int attackPatternCycle = 0;
    private double floatTimer = 0.0;

    private double activeDecayTimer = 0.0;

    private boolean hasSpokenIntro = false;
    private boolean hasSpokenPhase2 = false;
    private boolean hasSpokenDefeat = false;

    public Skana(double startX, double startY) {
        this(startX, startY, 1200.0, null, null, null);
    }

    public Skana(double startX, double startY, PatternSpawner patternSpawner, PlayerCharacter player) {
        this(startX, startY, 1200.0, patternSpawner, player, null);
    }

    public Skana(double startX, double startY, PatternSpawner patternSpawner, PlayerCharacter player, DialogueSystem dialogueSystem) {
        this(startX, startY, 1200.0, patternSpawner, player, dialogueSystem);
    }

    public Skana(double startX, double startY, double maxHealth, PatternSpawner patternSpawner, PlayerCharacter player, DialogueSystem dialogueSystem) {
        super(startX, startY, "/sprites/boss/skana.png", patternSpawner);
        this.player = player;
        this.dialogueSystem = dialogueSystem;

        this.healthSystem = new HealthSystem(maxHealth);
        this.maxHealth = maxHealth;
        this.currentHealth = maxHealth;

        InputStream spriteStream = getClass().getResourceAsStream("/sprites/bullets/tear.png");
        if (spriteStream != null) {
            this.bulletSprite = new Image(spriteStream);
        } else {
            System.err.println("Could not load bullet sprite for Skana: Resource missing.");
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
        return "Is this the reflection you truly wished to face?";
    }

    @Override
    public void takeDamage(double amount) {
        healthSystem.takeDamage(amount);
        this.currentHealth = healthSystem.getCurrentHealth();

        AudioManager.getInstance().playSFX("/audio/sfx/boss_hit.wav");

        if (isDead() && !hasSpokenDefeat) {
            triggerDefeatDialogue();
        } else {
            checkPhaseTransition();
        }
    }

    @Override
    public boolean isDead() {
        return healthSystem != null && healthSystem.isDead();
    }

    public HealthSystem getHealthSystem() {
        return healthSystem;
    }

    private void checkPhaseTransition() {
        if (currentPhase == 1 && healthSystem.getHealthPercentage() <= 0.5 && !hasSpokenPhase2) {
            currentPhase = 2;
            triggerPhaseDialogue();
        }
    }

    private void triggerIntroDialogue() {
        if (dialogueSystem != null && !hasSpokenIntro) {
            hasSpokenIntro = true;
            dialogueSystem.showMessage("Skana", "You have reached the deepest reflection... Shall we test your resolve?", null, 3.5);
        }
    }

    private void triggerPhaseDialogue() {
        if (dialogueSystem != null && !hasSpokenPhase2) {
            hasSpokenPhase2 = true;
            dialogueSystem.showMessage("Skana", getEnragedDialogue(), null, 3.5);
        }
    }

    private void triggerDefeatDialogue() {
        if (dialogueSystem != null && !hasSpokenDefeat) {
            hasSpokenDefeat = true;
            dialogueSystem.showMessage("Skana", "The ocean calms at last... You have proven your strength.", null, 3.5);
        }
    }

    @Override
    protected void performAttackPattern(double delta, double panelWidth, double panelHeight) {
        if (isDead()) return;

        if (!hasSpokenIntro) {
            triggerIntroDialogue();
        }

        checkPhaseTransition();

        if (activeDecayTimer > 0) {
            activeDecayTimer -= delta;
        }

        floatTimer += delta;
        double speedMult = (currentPhase == 1) ? 0.9 : 1.2;
        x = (panelWidth / 2.0 - width / 2.0) + Math.cos(floatTimer * 0.8 * speedMult) * (panelWidth * 0.35);
        y = 80.0 + Math.sin(floatTimer * 1.5 * speedMult) * 35.0;

        attackCooldown += delta;

        double currentInterval = (currentPhase == 1) ? attackIntervalPhase1 : attackIntervalPhase2;

        if (attackCooldown >= currentInterval) {
            attackCooldown = 0;

            switch (attackPatternCycle % 3) {
                case 0 -> fireTearsAttack(panelWidth);
                case 1 -> fireAllAttacksEcho(panelWidth, panelHeight);
                case 2 -> {
                    if (activeDecayTimer <= 0) {
                        fireDecayAroundPlayer(panelWidth, panelHeight);
                        activeDecayTimer = 7.5;
                    } else {
                        fireAllAttacksEcho(panelWidth, panelHeight);
                    }
                }
            }

            attackPatternCycle++;
        }
    }

    private void fireTearsAttack(double panelWidth) {
        if (patternSpawner == null) return;

        int dropCount = (currentPhase == 1) ? 14 : 20;
        double speed = (currentPhase == 1) ? 240.0 : 280.0;

        AudioManager.getInstance().playSFX("/audio/sfx/tear_attack.wav");
        new TeardropPattern(dropCount, speed, panelWidth, 3.0)
                .execute(patternSpawner.getBulletPool(), 0, 0, bulletSprite, Color.CYAN);
    }

    private void fireAllAttacksEcho(double panelWidth, double panelHeight) {
        if (patternSpawner == null) return;

        double centerX = x + (width / 2.0);
        double centerY = y + (height / 2.0);

        AudioManager.getInstance().playSFX("/audio/sfx/echo_attack.wav");

        int randomAttack = random.nextInt(4);
        switch (randomAttack) {
            case 0 -> new RocksPattern(currentPhase == 1 ? 260.0 : 300.0, player)
                    .execute(patternSpawner.getBulletPool(), centerX, centerY, bulletSprite, Color.GRAY);
            case 1 -> new TentaclePattern(3, 5, currentPhase == 1 ? 180.0 : 210.0, 0.05)
                    .execute(patternSpawner.getBulletPool(), centerX, centerY, bulletSprite, Color.PURPLE);
            case 2 -> new SpiralPattern(16, currentPhase == 1 ? 180.0 : 210.0, 0.08)
                    .execute(patternSpawner.getBulletPool(), centerX, centerY, bulletSprite, Color.CYAN);
            case 3 -> new JawBitePattern(6, currentPhase == 1 ? 240.0 : 280.0)
                    .execute(patternSpawner.getBulletPool(), centerX, centerY, bulletSprite, Color.DODGERBLUE);
        }
    }

    private void fireDecayAroundPlayer(double panelWidth, double panelHeight) {
        if (patternSpawner == null) return;

        double speed = (currentPhase == 1) ? 250.0 : 320.0;

        double targetX = (player != null) ? player.getX() + (player.getWidth() / 2.0) : x;
        double targetY = (player != null) ? player.getY() + (player.getHeight() / 2.0) : y;

        AudioManager.getInstance().playSFX("/audio/sfx/decay_attack.wav");
        new DecayPattern(speed, 90.0, 40.0, panelWidth, panelHeight)
                .execute(patternSpawner.getBulletPool(), targetX, targetY, bulletSprite, Color.DARKMAGENTA);
    }

    @Override
    protected void renderSprite(GraphicsContext gc) {
        if (isDead()) return;
        super.renderSprite(gc);
    }
}