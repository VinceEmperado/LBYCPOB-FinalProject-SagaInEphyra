package entities.enemies;

import combat.PatternSpawner;
import entities.PlayerCharacter;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import java.util.Random;

public class Clawdia extends EnemyController {
    private Image bulletSprite;
    private Random random = new Random();

    // tracking
    private PlayerCharacter player;

    private double attackTimer = 0;
    private double attackDuration = 2.0;
    private double teleportWarningDuration = 0.5;
    private double currentRotation = 0.0;
    private boolean isPreparingToTeleport = false;

    // cd
    private double attackCooldown = 0.0;
    private double attackInterval = 1.0;
    private boolean useClawNext = true;

    public Clawdia(double startX, double startY, PatternSpawner patternSpawner, PlayerCharacter player) {
        super(startX, startY, "/sprites/enemies/clawdia.png", patternSpawner);
        this.player = player;

        try {
            bulletSprite = new Image(getClass().getResourceAsStream("/sprites/bullets/pincer.png"));
        } catch (Exception e) {
            System.err.println("Could not load bullet sprite for Clawdia.");
        }
    }

    @Override
    protected String getEnragedDialogue() {
        return "a"; // change ts
    }

    @Override
    protected void performAttackPattern(double delta, double panelWidth, double panelHeight) {
        attackTimer += delta;

        if (attackTimer >= attackDuration && attackTimer < (attackDuration + teleportWarningDuration)) {
            isPreparingToTeleport = true;
            currentRotation += 1440 * delta;
        }
        else if (attackTimer >= (attackDuration + teleportWarningDuration)) {
            x = random.nextInt(Math.max(1, (int) panelWidth - width));
            y = random.nextInt(Math.max(1, (int) (panelHeight / 2) - height));

            attackTimer = 0;
            isPreparingToTeleport = false;
            currentRotation = 0.0;
        }
        else {
            isPreparingToTeleport = false;
            currentRotation = 0.0;

            attackCooldown += delta;

            double currentInterval = (currentPhase == 1) ? attackInterval : attackInterval * 0.5;

            if (attackCooldown >= currentInterval) {
                attackCooldown = 0;

                // Alternate attacks
                if (useClawNext) {
                    fireClawAttack();
                } else {
                    firePincerAttack();
                }

                //flip
                useClawNext = !useClawNext;
            }
        }
    }

    private void firePincerAttack() {
        if (patternSpawner == null) return;

        double centerX = x + (width / 2.0);
        double centerY = y + (height / 2.0);

        int bulletsPerSide = 4;
        double bulletSpeed = 200.0;
        double spreadAngle = Math.toRadians(55);

        patternSpawner.spawnPincer(
                centerX, centerY, bulletsPerSide,
                bulletSpeed, spreadAngle, player, bulletSprite, Color.ORANGE
        );
    }

    private void fireClawAttack() {
        if (patternSpawner == null) return;

        double centerX = x + (width / 2.0);
        double centerY = y + (height / 2.0);

        int bulletCount = 5;
        double speed = 250.0;
        double arcAngle = Math.toRadians(90);

        patternSpawner.spawnClaw(
                centerX, centerY, bulletCount,
                speed, arcAngle, player, bulletSprite, Color.RED
        );
    }

    @Override
    protected void renderSprite(GraphicsContext gc) {
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