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
import java.util.concurrent.ThreadLocalRandom;

public class Kanaloa extends EnemyController {

    private final HealthSystem healthSystem;
    private PlayerCharacter player;
    private DialogueSystem dialogueSystem;

    private Image bulletSprite;

    private final double attackIntervalPhase1 = 1.3;
    private final double attackIntervalPhase2 = 0.9;

    private final double jetFrequencyPhase1 = 2.4;
    private final double jetFrequencyPhase2 = 1.8;

    private double attackCooldown = 0.0;
    private int attackPatternCycle = 0;

    private double jetCooldownTimer = 0.0;
    private double currentVx = 0.0;
    private double currentVy = 0.0;

    private boolean hasSpokenIntro = false;
    private boolean hasSpokenPhase2 = false;
    private boolean hasSpokenDefeat = false;

    public Kanaloa(double startX, double startY) {
        this(startX, startY, 800.0, null, null, null);
    }

    public Kanaloa(double startX, double startY, PatternSpawner patternSpawner, PlayerCharacter player) {
        this(startX, startY, 800.0, patternSpawner, player, null);
    }

    public Kanaloa(double startX, double startY, double maxHealth, PatternSpawner patternSpawner, PlayerCharacter player, DialogueSystem dialogueSystem) {
        super(startX, startY, "/sprites/boss/kanaloa.png", patternSpawner);
        this.player = player;
        this.dialogueSystem = dialogueSystem;

        this.healthSystem = new HealthSystem(maxHealth);
        this.maxHealth = maxHealth;
        this.currentHealth = maxHealth;

        InputStream spriteStream = getClass().getResourceAsStream("/sprites/bullets/ink.png");
        if (spriteStream != null) {
            this.bulletSprite = new Image(spriteStream);
        } else {
            System.err.println("Could not load bullet sprite for Kanaloa: Resource missing.");
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
        return "The dark depths devour all!";
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
            dialogueSystem.showMessage("Kanaloa", "You wander deep into my abyssal trench... face the deep!", null, 3.5);
        }
    }

    private void triggerPhaseDialogue() {
        if (dialogueSystem != null && !hasSpokenPhase2) {
            hasSpokenPhase2 = true;
            dialogueSystem.showMessage("Kanaloa", getEnragedDialogue(), null, 3.5);
        }
    }

    private void triggerDefeatDialogue() {
        if (dialogueSystem != null && !hasSpokenDefeat) {
            hasSpokenDefeat = true;
            dialogueSystem.showMessage("Kanaloa", "The light... reaches even down here...?", null, 3.5);
        }
    }

    @Override
    protected void performAttackPattern(double delta, double panelWidth, double panelHeight) {
        if (isDead()) return;

        if (!hasSpokenIntro) {
            triggerIntroDialogue();
        }

        checkPhaseTransition();

        jetCooldownTimer += delta;
        double jetFrequency = (currentPhase == 1) ? jetFrequencyPhase1 : jetFrequencyPhase2;

        if (jetCooldownTimer >= jetFrequency) {
            jetCooldownTimer = 0;
            ThreadLocalRandom rng = ThreadLocalRandom.current();
            double targetX = rng.nextDouble() * Math.max(1.0, panelWidth - width - 100.0) + 50.0;
            double targetY = rng.nextDouble() * (panelHeight * 0.35) + 40.0;

            double dx = targetX - x;
            double dy = targetY - y;
            double dist = Math.hypot(dx, dy);

            if (dist > 0) {
                double speed = (currentPhase == 1) ? 360.0 : 480.0;
                currentVx = (dx / dist) * speed;
                currentVy = (dy / dist) * speed;
                AudioManager.getInstance().playSFX("/audio/sfx/boss_dash.wav");
            }
        }

        x += currentVx * delta;
        y += currentVy * delta;
        currentVx *= Math.pow(0.02, delta);
        currentVy *= Math.pow(0.02, delta);

        x = Math.clamp(x, 20.0, Math.max(20.0, panelWidth - width - 20.0));
        y = Math.clamp(y, 20.0, Math.max(20.0, panelHeight * 0.5));

        attackCooldown += delta;
        double currentInterval = (currentPhase == 1) ? attackIntervalPhase1 : attackIntervalPhase2;

        if (attackCooldown >= currentInterval) {
            attackCooldown = 0;

            switch (attackPatternCycle % 3) {
                case 0 -> fireRocksAttack();
                case 1 -> fireTentacleAttack();
                case 2 -> fireStickAttack();
            }

            attackPatternCycle++;
        }
    }

    private void fireRocksAttack() {
        if (patternSpawner == null) return;

        double centerX = x + (width / 2.0);
        double centerY = y + (height / 2.0);

        double speed = (currentPhase == 1) ? 230.0 : 270.0;

        AudioManager.getInstance().playSFX("/audio/sfx/ink_attack.wav");
        new RocksPattern(speed, player).execute(patternSpawner.getBulletPool(), centerX, centerY, bulletSprite, Color.GRAY);
    }

    private void fireTentacleAttack() {
        if (patternSpawner == null) return;

        double centerX = x + (width / 2.0);
        double centerY = y + (height / 2.0);

        int count = (currentPhase == 1) ? 3 : 4;
        double speed = (currentPhase == 1) ? 180.0 : 210.0;

        AudioManager.getInstance().playSFX("/audio/sfx/ink_attack.wav");
        new TentaclePattern(count, count + 2, speed, 0.05).execute(patternSpawner.getBulletPool(), centerX, centerY, bulletSprite, Color.PURPLE);
    }

    private void fireStickAttack() {
        if (patternSpawner == null) return;

        double centerX = x + (width / 2.0);
        double centerY = y + (height / 2.0);

        int count = (currentPhase == 1) ? 10 : 16;
        double speed = (currentPhase == 1) ? 175.0 : 200.0;

        AudioManager.getInstance().playSFX("/audio/sfx/ink_attack.wav");
        new StickPattern(count, speed, player).execute(patternSpawner.getBulletPool(), centerX, centerY, bulletSprite, Color.SADDLEBROWN);
    }

    @Override
    protected void renderSprite(GraphicsContext gc) {
        if (isDead()) return;
        super.renderSprite(gc);
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
}