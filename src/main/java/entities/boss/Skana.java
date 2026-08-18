package entities.boss;

import combat.*;
import core.AudioManager;
import entities.PlayerCharacter;
import entities.enemies.EnemyController;
import ui.DialogueSystem;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.Stop;
import javafx.scene.effect.BlendMode;
import javafx.scene.shape.FillRule;

import java.io.InputStream;
import java.util.Random;

public class Skana extends EnemyController {

    private final Random random = new Random();
    private final HealthSystem healthSystem;

    private Image bulletSprite;

    private double attackCooldown = 0.0;
    private int attackPatternCycle = 0;
    private double floatTimer = 0.0;

    // Track active decay state and timing
    private boolean isDecayActive = false;
    private double decayTimer = 0.0;

    private boolean hasSpokenIntro = false;
    private boolean hasSpokenPhase2 = false;
    private boolean hasSpokenDefeat = false;

    public Skana(double startX, double startY, PatternSpawner patternSpawner, PlayerCharacter player) {
        this(startX, startY, 1200.0, patternSpawner, player, null);
    }

    public Skana(double startX, double startY, double maxHealth, PatternSpawner patternSpawner, PlayerCharacter player, DialogueSystem dialogueSystem) {
        super(startX, startY, "/sprites/boss/Skana.png", "/sprites/boss/SkanaAttack.png", patternSpawner);

        setPlayer(player);
        setDialogueSystem(dialogueSystem);

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
            isDecayActive = false;
        } else {
            checkPhaseTransition();
        }
    }

    @Override
    public boolean isDead() {
        return healthSystem != null && healthSystem.isDead();
    }

    private void checkPhaseTransition() {
        if (currentPhase == 1 && healthSystem.getHealthPercentage() <= 0.5 && !hasSpokenPhase2) {
            currentPhase = 2;
            triggerPhaseDialogue();
        }
    }

    private void triggerIntroDialogue() {
        if (getDialogueSystem() != null && !hasSpokenIntro) {
            hasSpokenIntro = true;
            getDialogueSystem().showMessage("Skana", "You have reached the deepest reflection... Shall we test your resolve?", null, 3.5);
        }
    }

    private void triggerPhaseDialogue() {
        if (getDialogueSystem() != null && !hasSpokenPhase2) {
            hasSpokenPhase2 = true;
            getDialogueSystem().showMessage("Skana", getEnragedDialogue(), null, 3.5);
        }
    }

    private void triggerDefeatDialogue() {
        if (getDialogueSystem() != null && !hasSpokenDefeat) {
            hasSpokenDefeat = true;
            getDialogueSystem().showMessage("Skana", "The ocean calms at last... You have proven your strength.", null, 3.5);
        }
    }

    @Override
    protected void performAttackPattern(double delta, double panelWidth, double panelHeight) {
        if (isDead()) return;

        if (!hasSpokenIntro) {
            triggerIntroDialogue();
        }

        checkPhaseTransition();

        floatTimer += delta;
        double speedMult = (currentPhase == 1) ? 0.9 : 1.2;
        x = (panelWidth / 2.0 - width / 2.0) + Math.cos(floatTimer * 0.8 * speedMult) * (panelWidth * 0.35);
        y = 80.0 + Math.sin(floatTimer * 1.5 * speedMult) * 35.0;

        // Phase 2 Exclusive: Maintain strictly one active decay box at a time
        if (currentPhase == 2) {
            if (!isDecayActive) {
                fireSingleDecayAtPlayer(panelWidth, panelHeight);
                isDecayActive = true;
                decayTimer = 0.0;
            } else {
                decayTimer += delta;

                // Using a local variable for the clear time fixes field IDE warnings
                double decayClearTime = 8.0;

                if (decayTimer >= decayClearTime) {
                    isDecayActive = false; // Ready to spawn a fresh single box
                }
            }
        }

        attackCooldown += delta;
        double currentInterval = (currentPhase == 1) ? 1.4 : 1.0;

        if (attackCooldown >= currentInterval) {
            attackCooldown = 0;

            switch (attackPatternCycle % 2) {
                case 0 -> fireTearsAttack(panelWidth);
                case 1 -> fireAllAttacksEcho();
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

    private void fireAllAttacksEcho() {
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

    private void fireSingleDecayAtPlayer(double panelWidth, double panelHeight) {
        if (patternSpawner == null) return;

        double speed = 250.0;
        double targetX = (player != null) ? player.getX() + (player.getWidth() / 2.0) : x;
        double targetY = (player != null) ? player.getY() + (player.getHeight() / 2.0) : y;

        AudioManager.getInstance().playSFX("/audio/sfx/decay_attack.wav");

        new DecayPattern(speed, 90.0, 40.0, panelWidth, panelHeight)
                .execute(patternSpawner.getBulletPool(), targetX, targetY, bulletSprite, Color.DARKMAGENTA);
    }

    @Override
    protected void renderUI(GraphicsContext gc) {
        super.renderUI(gc);

        // Render Phase 2 abyssal darkness and aura glow
        if (currentPhase == 2 && !isDead()) {
            renderAbyssalLighting(gc, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
        }
    }

    public void renderAbyssalLighting(GraphicsContext gc, double panelWidth, double panelHeight) {
        if (isDead()) return;

        gc.save();

        gc.setFillRule(FillRule.EVEN_ODD);
        gc.setFill(Color.rgb(2, 4, 15, 0.93));

        gc.beginPath();
        gc.rect(0, 0, panelWidth, panelHeight);

        if (player != null) {
            double playerCenterX = player.getX() + player.getWidth() / 2.0;
            double playerCenterY = player.getY() + player.getHeight() / 2.0;
            double playerRadius = 90.0;
            gc.arc(playerCenterX, playerCenterY, playerRadius, playerRadius, 0, 360);
            gc.closePath();
        }

        double bossCenterX = x + (width / 2.0);
        double bossCenterY = y + (height / 2.0);
        double bossRadius = Math.max(width, height) * 0.8;
        gc.arc(bossCenterX, bossCenterY, bossRadius, bossRadius, 0, 360);
        gc.closePath();

        gc.fill();

        gc.restore();

        gc.save();
        gc.setGlobalBlendMode(BlendMode.SCREEN);

        double glowRadius = bossRadius * 1.35;
        RadialGradient abyssalGlow = new RadialGradient(
                0, 0, bossCenterX, bossCenterY, glowRadius, false, CycleMethod.NO_CYCLE,
                new Stop(0, Color.rgb(0, 190, 255, 0.4)),
                new Stop(0.6, Color.rgb(30, 60, 180, 0.15)),
                new Stop(1, Color.rgb(0, 0, 0, 0.0))
        );

        gc.setFill(abyssalGlow);
        gc.fillOval(bossCenterX - glowRadius, bossCenterY - glowRadius, glowRadius * 2, glowRadius * 2);

        gc.restore();
    }

    @Override
    protected void renderSprite(GraphicsContext gc) {
        if (isDead()) return;
        super.renderSprite(gc);
    }
}