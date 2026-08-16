package entities.boss;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public abstract class BossController {
    protected double x, y;
    protected int width = 120, height = 120;
    protected BufferedImage bossSprite;

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
                bossSprite = ImageIO.read(getClass().getResourceAsStream(imagePath));
            }
        } catch (Exception e) {
            System.out.println("Custom sprite not found. Defaulting to Mart.");
        }

        // fallback to mart
        if (bossSprite == null) {
            try {
                bossSprite = ImageIO.read(getClass().getResourceAsStream("/sprites/boss/mart.png"));
            } catch (Exception ex) {
                System.err.println("Could not load mart.");
            }
        }
    }

    public void update(double delta, int panelWidth, int panelHeight) {
        // stop attacking and just count down the dialogue timer
        if (isTransitioning) {
            dialogueTimer += delta;

            if (dialogueTimer >= dialogueDuration) {
                isTransitioning = false;
                dialogueTimer = 0;
                currentPhase++; //
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
            System.out.println("Boss defeated");
        }

        performAttackPattern(delta, panelWidth, panelHeight);
    }

    protected abstract void performAttackPattern(double delta, int panelWidth, int panelHeight);
    protected abstract String getPhaseTwoDialogue();
    protected abstract String getEnragedDialogue();

    public void render(Graphics2D g2d) {
        // sprite
        if (bossSprite != null) {
            g2d.drawImage(bossSprite, (int) x, (int) y, width, height, null);
        } else {
            g2d.setColor(Color.YELLOW);
            g2d.fillRect((int) x, (int) y, width, height);
        }

        // hp bar for phase 3
        if (currentPhase == 3 && !isTransitioning) {
            g2d.setColor(Color.RED);
            g2d.fillRect((int)x, (int)y - 20, width, 10);
            g2d.setColor(Color.GREEN);
            int healthWidth = (int)((currentHealth / (double)maxHealth) * width);
            g2d.fillRect((int)x, (int)y - 20, healthWidth, 10);
        }

        // text
        if (isTransitioning) {
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 16));
            g2d.drawString(currentDialogueText, (int)x - 40, (int)y - 30);
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