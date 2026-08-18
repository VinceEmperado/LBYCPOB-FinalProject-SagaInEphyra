package entities.boss;

import combat.HealthSystem;
import combat.PatternSpawner;
import entities.enemies.EnemyController;
import entities.PlayerCharacter;
import ui.DialogueSystem;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public abstract class BossController extends EnemyController {

    protected HealthSystem healthSystem;
    protected String bossName = "Boss";

    protected double phaseTwoDuration = 60.0;

    // UPDATED: Now takes both normal and attack image paths
    public BossController(double startX, double startY, String normalImagePath, String attackImagePath, double maxHealth,
                          PatternSpawner patternSpawner, PlayerCharacter player, DialogueSystem dialogueSystem) {
        super(startX, startY, normalImagePath, attackImagePath, patternSpawner);

        this.player = player;
        this.dialogueSystem = dialogueSystem;
        this.healthSystem = new HealthSystem(maxHealth);
        this.maxHealth = maxHealth;
        this.currentHealth = maxHealth;
        this.phaseOneDuration = 60.0;
        this.dialogueDuration = 4.0;
    }

    @Override
    public void update(double delta, double panelWidth, double panelHeight) {
        if (isDead()) return;

        if (isTransitioning) {
            dialogueTimer += delta;

            if (dialogueTimer >= dialogueDuration) {
                isTransitioning = false;
                dialogueTimer = 0;
                currentPhase++;
            }
            return;
        }

        // Phase 1 -> Phase 2
        if (currentPhase == 1) {
            survivalTimer += delta;
            if (survivalTimer >= phaseOneDuration) {
                triggerPhaseTransition(getPhaseTwoDialogue());
                survivalTimer = 0;
            }
        }
        // Phase 2 -> Phase 3 (Enraged)
        else if (currentPhase == 2) {
            survivalTimer += delta;
            if (survivalTimer >= phaseTwoDuration) {
                triggerPhaseTransition(getEnragedDialogue());
            }
        }

        // Note: EnemyController's state machine handles the ATTACKING -> STANDING -> SHAKING -> TELEPORTING cycles automatically!
        performAttackPattern(delta, panelWidth, panelHeight);
    }

    @Override
    protected void triggerPhaseTransition(String dialogueText) {
        isTransitioning = true;
        currentDialogueText = dialogueText;

        if (dialogueSystem != null) {
            dialogueSystem.showMessage(bossName, dialogueText, null, dialogueDuration);
        }
    }

    protected abstract String getPhaseTwoDialogue();

    @Override
    public void render(GraphicsContext gc) {
        if (isDead()) return;
        renderSprite(gc);
        renderUI(gc);
    }

    @Override
    protected void renderUI(GraphicsContext gc) {
        if (isTransitioning && dialogueSystem == null) {
            gc.setFill(Color.WHITE);
            gc.setFont(Font.font("Arial", FontWeight.BOLD, 16));
            gc.fillText(currentDialogueText, x - 40, y - 30);
        }
    }

    @Override
    public void takeDamage(double amount) {
        if (currentPhase == 3 && !isTransitioning) {
            healthSystem.takeDamage(amount);
            this.currentHealth = healthSystem.getCurrentHealth();
        }
    }

    @Override
    public boolean isDead() {
        return healthSystem != null && healthSystem.isDead();
    }

    public HealthSystem getHealthSystem() {
        return healthSystem;
    }
}