package entities.boss;

import combat.*;
import entities.PlayerCharacter;
import entities.enemies.EnemyController;
import javafx.scene.paint.Color;

public class Kanaloa extends EnemyController {

    private final PlayerCharacter player;
    private double attackTimer = 0.0;
    private final double attackInterval = 1.8;
    private int attackPatternIndex = 0;

    public Kanaloa(double startX, double startY, PatternSpawner patternSpawner, PlayerCharacter player) {
        super(startX, startY, "/sprites/boss/kanaloa.png", patternSpawner);
        this.player = player;

        // Stage 1 Boss Stats
        this.width = 160;
        this.height = 160;
        this.maxHealth = 1000.0;
        this.currentHealth = 1000.0;
        this.phaseOneDuration = 12.0;
    }

    @Override
    protected void performAttackPattern(double delta, double panelWidth, double panelHeight) {
        attackTimer += delta;

        if (attackTimer >= attackInterval) {
            attackTimer = 0.0;

            double bossCenterX = x + (width / 2.0);
            double bossCenterY = y + (height / 2.0);

            if (currentPhase == 1) {
                if (attackPatternIndex % 2 == 0) {
                    new SonarPulsePattern(250.0, 80.0, 16, 2, player)
                            .execute(patternSpawner.getBulletPool(), bossCenterX, bossCenterY, null, Color.CYAN);
                } else {
                    // Rock Barrage: Spawn 8 rocks with varying speeds to pelt the player
                    int rockCount = 8;
                    for (int i = 0; i < rockCount; i++) {
                        double variedSpeed = 300.0 + (Math.random() * 200.0); // Speeds between 300 and 500
                        new RocksPattern(variedSpeed, player)
                                .execute(patternSpawner.getBulletPool(), 0, 0, null, Color.DODGERBLUE);
                    }
                }
            } else {
                switch (attackPatternIndex % 3) {
                    case 0 -> new TentaclePattern(8, 12, 350.0, 0.05)
                            .execute(patternSpawner.getBulletPool(), bossCenterX, bossCenterY, null, Color.DEEPPINK);
                    case 1 -> new SpiralPattern(24, 380.0, 0.1)
                            .execute(patternSpawner.getBulletPool(), bossCenterX, bossCenterY, null, Color.MAGENTA);
                    case 2 -> new StickPattern(10, 420.0)
                            .execute(patternSpawner.getBulletPool(), bossCenterX, bossCenterY, null, Color.CORAL);
                }
            }

            attackPatternIndex++;
        }
    }

    @Override
    protected String getEnragedDialogue() {
        return "You dare disturb the ocean depths?! Prepare to drown!";
    }
}