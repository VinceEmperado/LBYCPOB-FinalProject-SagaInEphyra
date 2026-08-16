package ui;

import combat.*;
import entities.PlayerCharacter;
import entities.boss.Kanaloa;
import entities.enemies.Clawdia;
import entities.enemies.EnemyController;
import pools.BulletPool;

import java.util.function.Consumer;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class TestersMenu extends ScrollPane {

    private final BulletPool bulletPool;
    private final PlayerCharacter player;
    private final PatternSpawner patternSpawner;
    private final Consumer<EnemyController> enemySwapper;
    private boolean godMode = false;

    public TestersMenu(BulletPool bulletPool, double screenWidth, double screenHeight,
                       PlayerCharacter player, PatternSpawner patternSpawner,
                       Consumer<EnemyController> enemySwapper) {
        this.bulletPool = bulletPool;
        this.player = player;
        this.patternSpawner = patternSpawner;
        this.enemySwapper = enemySwapper;

        VBox container = new VBox(8);
        container.setPadding(new Insets(15));
        container.setAlignment(Pos.TOP_CENTER);
        container.setStyle("-fx-background-color: rgba(10, 10, 20, 0.95); " +
                "-fx-border-color: #00ffcc; " +
                "-fx-border-width: 2px; " +
                "-fx-border-radius: 5px;");
        container.setPrefWidth(250);

        setContent(container);
        setFitToWidth(true);
        setHbarPolicy(ScrollBarPolicy.NEVER);
        setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
        setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        Label titleLabel = new Label("TESTER MENU");
        titleLabel.setStyle("-fx-text-fill: #00ffcc; -fx-font-weight: bold; -fx-font-size: 16px;");

        Label bossLabel = new Label("Select Boss Stage:");
        bossLabel.setStyle("-fx-text-fill: #ffffff;");

        ComboBox<String> bossSelect = new ComboBox<>();
        bossSelect.getItems().addAll(
                "Stage 1: Clawdia",
                "Stage 1 Boss: Kanaloa",
                "Stage 2: Finana",
                "Stage 2 Boss: Thalasaa",
                "Final Stage: Skaana"
        );
        bossSelect.getSelectionModel().selectFirst();
        bossSelect.setMaxWidth(Double.MAX_VALUE);

        // Enemy Controls (Spawn & Remove)
        Button spawnEnemyBtn = new Button("Spawn Enemy");
        spawnEnemyBtn.setMaxWidth(Double.MAX_VALUE);
        spawnEnemyBtn.setStyle("-fx-background-color: #00cc66; -fx-text-fill: white; -fx-font-weight: bold;");
        spawnEnemyBtn.setOnAction(e -> {
            if (this.enemySwapper != null) {
                String selected = bossSelect.getValue();
                EnemyController newEnemy = createEnemyFromSelection(selected, screenWidth);
                this.enemySwapper.accept(newEnemy);
            }
        });

        Button removeEnemyBtn = new Button("Remove Enemy");
        removeEnemyBtn.setMaxWidth(Double.MAX_VALUE);
        removeEnemyBtn.setStyle("-fx-background-color: #ff9900; -fx-text-fill: white; -fx-font-weight: bold;");
        removeEnemyBtn.setOnAction(e -> {
            if (this.enemySwapper != null) {
                this.enemySwapper.accept(null);
            }
        });

        HBox enemyControlBox = new HBox(6, spawnEnemyBtn, removeEnemyBtn);
        enemyControlBox.setAlignment(Pos.CENTER);

        Label patternLabel = new Label("Trigger Pattern (12 Attacks):");
        patternLabel.setStyle("-fx-text-fill: #ffffff; -fx-font-weight: bold;");

        // 1. Claw Pattern
        Button clawBtn = createTestButton("1. Claw Pattern", () ->
                new ClawPattern(50, 450.0, Math.toRadians(45), player)
                        .execute(this.bulletPool, screenWidth / 2.0, 150, null, Color.RED)
        );

        // 2. Pincer Pattern
        Button pincerBtn = createTestButton("2. Pincer Pattern", () ->
                new PincerPattern(12, 400.0, Math.toRadians(45), player)
                        .execute(this.bulletPool, screenWidth / 2.0, 150, null, Color.ORANGE)
        );

        // 3. Bomb Pattern
        Button bombBtn = createTestButton("3. Bomb Pattern", () ->
                new BombPattern(16, 400.0)
                        .execute(this.bulletPool, screenWidth / 2.0, 150, null, Color.YELLOW)
        );

        // 4. Stick Pattern
        Button stickBtn = createTestButton("4. Stick Pattern", () ->
                new StickPattern(12, 350.0, player)
                        .execute(this.bulletPool, screenWidth / 2.0, 150, null, Color.PURPLE)
        );

        // 5. Teardrop Pattern
        Button teardropBtn = createTestButton("5. Teardrop Pattern", () ->
                new TeardropPattern(12, 450.0, screenWidth)
                        .execute(this.bulletPool, 0, 0, null, Color.DODGERBLUE)
        );

        // 6. Decay Pattern
        Button decayBtn = createTestButton("6. Decay Pattern", () ->
                new DecayPattern(400.0, 90.0, 40.0, screenWidth, screenHeight)
                        .execute(this.bulletPool, screenWidth / 2.0, 150, null, Color.LIMEGREEN)
        );

        // 7. Tentacle Pattern
        Button tentacleBtn = createTestButton("7. Tentacle Pattern", () ->
                new TentaclePattern(8, 12, 350.0, 0.05)
                        .execute(this.bulletPool, screenWidth / 2.0, screenHeight / 2.0, null, Color.DEEPPINK)
        );

        // 8. Sonar Pulse Pattern
        Button sonarBtn = createTestButton("8. Sonar Pulse Pattern", () ->
                new SonarPulsePattern(250.0, 80.0, 24, 3, player)
                        .execute(this.bulletPool, screenWidth / 2.0, 150, null, Color.CYAN)
        );

        // 9. Spiral Pattern
        Button spiralBtn = createTestButton("9. Spiral Pattern", () ->
                new SpiralPattern(36, 400.0, 0.1)
                        .execute(this.bulletPool, screenWidth / 2.0, 150, null, Color.MAGENTA)
        );

        // 10. Jaw Bite Pattern
        Button jawBiteBtn = createTestButton("10. Jaw Bite Pattern", () ->
                new JawBitePattern(8, 350.0)
                        .execute(this.bulletPool, screenWidth / 2.0, 150, null, Color.CORAL)
        );

        // 11. Rocks Pattern
        Button rocksBtn = createTestButton("11. Rocks Pattern", () ->
                new RocksPattern(400.0, player)
                        .execute(this.bulletPool, screenWidth / 2.0, 100, null, Color.GRAY)
        );

        ToggleButton godModeToggle = new ToggleButton("God Mode: OFF");
        godModeToggle.setMaxWidth(Double.MAX_VALUE);
        godModeToggle.setStyle("-fx-background-color: #333333; -fx-text-fill: white; -fx-font-weight: bold;");
        godModeToggle.setOnAction(e -> {
            godMode = godModeToggle.isSelected();
            godModeToggle.setText("God Mode: " + (godMode ? "ON" : "OFF"));

            if (godMode) {
                godModeToggle.setStyle("-fx-background-color: #00cc66; -fx-text-fill: white; -fx-font-weight: bold;");
            } else {
                godModeToggle.setStyle("-fx-background-color: #333333; -fx-text-fill: white; -fx-font-weight: bold;");
            }

            if (this.player != null) {
                this.player.setGodMode(godMode);
            }
        });

        Button clearBtn = new Button("Clear Screen");
        clearBtn.setMaxWidth(Double.MAX_VALUE);
        clearBtn.setStyle("-fx-background-color: #ff3366; -fx-text-fill: white; -fx-font-weight: bold;");
        clearBtn.setOnAction(e -> {
            if (this.bulletPool != null) {
                this.bulletPool.clearAllBullets();
            }
        });

        container.getChildren().addAll(
                titleLabel,
                bossLabel, bossSelect, enemyControlBox,
                patternLabel,
                clawBtn, pincerBtn, bombBtn, stickBtn, teardropBtn, decayBtn,
                tentacleBtn, sonarBtn, spiralBtn, jawBiteBtn, rocksBtn,
                godModeToggle, clearBtn
        );
    }

    private EnemyController createEnemyFromSelection(String selection, double screenWidth) {
        return switch (selection) {
            case "Stage 1 Boss: Kanaloa" -> new Kanaloa(screenWidth / 2.0 - 80, 120, patternSpawner, player);
            default -> new Clawdia(screenWidth / 2.0 - 60, 150, patternSpawner, player);
        };
    }

    private Button createTestButton(String text, Runnable action) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setOnAction(e -> {
            if (bulletPool != null) action.run();
        });
        return btn;
    }

    public boolean isGodModeEnabled() {
        return godMode;
    }
}