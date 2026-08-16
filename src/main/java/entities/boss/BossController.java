package entities.boss;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public abstract class BossController {
    protected double x, y;
    protected int width = 120, height = 120;
    protected Image bossSprite;

    protected int currentPhase = 1; // 1 = Phase 1, 2 = Phase 2, 3 = Enraged
    protected double survivalTimer = 0.0;
    protected double phaseOneDuration = 15.0;
    protected double phaseTwoDuration = 15.0;

    protected int maxHealth = 100;
    protected int currentHealth = 100;

    protected boolean isTransitioning = false;
    protected double dialogueTimer = 0.0;
    protected double dialogueDuration = 4.0;
    protected String currentDialogueText = "";

    public BossController(double startX, double startY, String imagePath) {
        this.x = startX;
        this.y = startY;

        // load boss sprite
        try {
            if (imagePath != null) {
                bossSprite = new Image(getClass().getResourceAsStream(imagePath));
            }
        } catch (Exception e) {
            System.out.println("Custom sprite not found. Defaulting to Mart.");
        }

        // fallback to mart
        if (bossSprite == null) {
            try {
                bossSprite = new Image(getClass().getResourceAsStream("/sprites/boss/mart.png"));
            } catch (Exception ex) {
                System.err.println("Could not load mart.");
            }
        }
    }

    public void update(double delta, double panelWidth, double panelHeight) {
        // stop attacking and just count down the dialogue timer
        if (isTransitioning) {
            dialogueTimer += delta;

            if (dialogueTimer >= dialogueDuration) {
                isTransitioning = false;
                dialogueTimer = 0;
                currentPhase++;
            }
            return; // early exit to prevent attacks
        }

        // phase 1
        if (currentPhase == 1) {
            survivalTimer += delta;
            if (survivalTimer >= phaseOneDuration) {
                isTransitioning = true;
                currentDialogueText = getPhaseTwoDialogue();
                survivalTimer = 0; // reset timer for Phase 2
                System.out.println("Phase 2");
            }
        }

        // phase 2
        else if (currentPhase == 2) {
            survivalTimer += delta;
            if (survivalTimer >= phaseTwoDuration) {
                isTransitioning = true;
                currentDialogueText = getEnragedDialogue();
                System.out.println("Enraged");
            }
        }

        // enraged
        else if (currentPhase == 3 && currentHealth <= 0) {
            // TODO: Boss death logic
            System.out.println("Boss defeated");
        }

        performAttackPattern(delta, panelWidth, panelHeight);
    }

    protected abstract void performAttackPattern(double delta, double panelWidth, double panelHeight);
    protected abstract String getPhaseTwoDialogue();
    protected abstract String getEnragedDialogue();

    public void render(GraphicsContext gc) {
        renderSprite(gc);
        renderUI(gc);
    }

    protected void renderSprite(GraphicsContext gc) {
        // sprite
        if (bossSprite != null) {
            gc.drawImage(bossSprite, x, y, width, height);
        } else {
            gc.setFill(Color.YELLOW);
            gc.fillRect(x, y, width, height);
        }
    }

    protected void renderUI(GraphicsContext gc) {
        // hp bar for phase 3
        if (currentPhase == 3 && !isTransitioning) {
            gc.setFill(Color.RED);
            gc.fillRect(x, y - 20, width, 10);
            gc.setFill(Color.GREEN);
            double healthWidth = ((double) currentHealth / maxHealth) * width;
            gc.fillRect(x, y - 20, healthWidth, 10);
        }

        // text
        if (isTransitioning) {
            gc.setFill(Color.WHITE);
            gc.setFont(Font.font("Arial", FontWeight.BOLD, 16));
            gc.fillText(currentDialogueText, x - 40, y - 30);
        }
    }

    public void takeDamage(int amount) {
        if (currentPhase == 3 && !isTransitioning) {
            currentHealth -= amount; // they will only take damage during enraged
            if (currentHealth < 0) currentHealth = 0;
        }
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
}