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

public class Clawdia extends EnemyController {

    private final Random random = new Random();
    private final HealthSystem healthSystem;
    private PlayerCharacter player;
    private DialogueSystem dialogueSystem;

    private Image bulletSprite;

    private final double attackDuration = 3.5;
    private final double teleportWarningDuration = 0.7;

    private final double attackIntervalPhase1 = 1.2;
    private final double attackIntervalPhase2 = 0.8;

    private double attackTimer = 0.0;
    private double currentRotation = 0.0;
    private boolean isPreparingToTeleport = false;

    private double attackCooldown = 0.0;
    private boolean useClawNext = true;

    private boolean hasSpokenIntro = false;
    private boolean hasSpokenPhase2 = false;
    private boolean hasSpokenDefeat = false;

    public Clawdia(double startX, double startY) {
        this(startX, startY, 450.0, null, null, null);
    }

    public Clawdia(double startX, double startY, PatternSpawner patternSpawner, PlayerCharacter player) {
        this(startX, startY, 450.0, patternSpawner, player, null);
    }

    public Clawdia(double startX, double startY, PatternSpawner patternSpawner, PlayerCharacter player, DialogueSystem dialogueSystem) {
        this(startX, startY, 450.0, patternSpawner, player, dialogueSystem);
    }

    public Clawdia(double startX, double startY, double maxHealth, PatternSpawner patternSpawner, PlayerCharacter player, DialogueSystem dialogueSystem) {
        super(startX, startY, "/sprites/enemy/clawdia.png", patternSpawner);
        this.player = player;
        this.dialogueSystem = dialogueSystem;

        this.healthSystem = new HealthSystem(maxHealth);
        this.maxHealth = maxHealth;
        this.currentHealth = maxHealth;

        InputStream spriteStream = getClass().getResourceAsStream("/sprites/bullets/pincer.png");
        if (spriteStream != null) {
            this.bulletSprite = new Image(spriteStream);
        } else {
            System.err.println("Could not load bullet sprite for Clawdia: Resource missing.");
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
        return "Snip snip! You can't escape the claws!";
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
            dialogueSystem.showMessage("Clawdia", "Who dares step into my reef? Prepare to get pinched!", null, 3.0);
        }
    }

    private void triggerPhaseDialogue() {
        if (dialogueSystem != null && !hasSpokenPhase2) {
            hasSpokenPhase2 = true;
            dialogueSystem.showMessage("Clawdia", getEnragedDialogue(), null, 3.0);
        }
    }

    private void triggerDefeatDialogue() {
        if (dialogueSystem != null && !hasSpokenDefeat) {
            hasSpokenDefeat = true;
            dialogueSystem.showMessage("Clawdia", "My claws... broken... Nooo!", null, 3.0);
        }
    }

    @Override
    protected void performAttackPattern(double delta, double panelWidth, double panelHeight) {
        if (isDead()) return;

        if (!hasSpokenIntro) {
            triggerIntroDialogue();
        }

        checkPhaseTransition();

        attackTimer += delta;

        if (attackTimer >= attackDuration && attackTimer < (attackDuration + teleportWarningDuration)) {
            isPreparingToTeleport = true;
            currentRotation += 720 * delta;
        }
        else if (attackTimer >= (attackDuration + teleportWarningDuration)) {
            int minX = 80;
            int maxX = Math.max(minX + 1, (int) panelWidth - width - 80);
            int minY = 60;
            int maxY = Math.max(minY + 1, (int) (panelHeight * 0.35) - height);

            x = minX + random.nextInt(maxX - minX + 1);
            y = minY + random.nextInt(maxY - minY + 1);

            attackTimer = 0;
            isPreparingToTeleport = false;
            currentRotation = 0.0;
        }
        else {
            isPreparingToTeleport = false;
            currentRotation = 0.0;

            attackCooldown += delta;

            double currentInterval = (currentPhase == 1) ? attackIntervalPhase1 : attackIntervalPhase2;

            if (attackCooldown >= currentInterval) {
                attackCooldown = 0;

                if (useClawNext) {
                    fireClawAttack();
                } else {
                    firePincerAttack();
                }

                useClawNext = !useClawNext;
            }
        }
    }

    private void firePincerAttack() {
        if (patternSpawner == null) return;

        double centerX = x + (width / 2.0);
        double centerY = y + (height / 2.0);

        int bulletsPerSide = (currentPhase == 1) ? 3 : 4;
        double bulletSpeed = (currentPhase == 1) ? 175.0 : 200.0;
        double spreadAngle = Math.toRadians(45);

        patternSpawner.spawnPincer(
                centerX, centerY, bulletsPerSide,
                bulletSpeed, spreadAngle, player, bulletSprite, Color.ORANGE
        );
    }

    private void fireClawAttack() {
        if (patternSpawner == null) return;

        double centerX = x + (width / 2.0);
        double centerY = y + (height / 2.0);

        int bulletCount = (currentPhase == 1) ? 5 : 7;
        double speed = (currentPhase == 1) ? 190.0 : 215.0;
        double arcAngle = Math.toRadians(80);

        patternSpawner.spawnClaw(
                centerX, centerY, bulletCount,
                speed, arcAngle, player, bulletSprite, Color.RED
        );
    }

    @Override
    protected void renderSprite(GraphicsContext gc) {
        if (isDead()) return;

        gc.save();

        if (isPreparingToTeleport) {
            double pivotX = x + (width / 2.0);
            double pivotY = y + (height / 2.0);

            gc.translate(pivotX, pivotY);
            gc.rotate(currentRotation);
            gc.translate(-pivotX, -pivotY);
        }

        super.renderSprite(gc);

        gc.restore();
    }
}