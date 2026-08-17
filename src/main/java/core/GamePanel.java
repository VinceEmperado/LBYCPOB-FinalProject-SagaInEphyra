package core;

import combat.PatternSpawner;
import entities.enemies.EnemyController;
import entities.PlayerCharacter;
import pools.BulletPool;
import pools.ItemPool;
import ui.DialogueSystem;
import ui.GameHudManager;
import ui.GameOverMenu;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class GamePanel extends Canvas {
    private final PlayerCharacter player;
    private EnemyController enemy;
    private final BulletPool bulletPool;
    private final BulletPool playerBulletPool;
    private final ItemPool itemPool;
    private final StageDirector stageDirector;
    private GameOverMenu gameOverMenu;
    private final ScoreManager scoreManager = new ScoreManager();
    private final GameHudManager hudManager = new GameHudManager(scoreManager);
    private final DialogueSystem dialogueSystem = new DialogueSystem();

    private boolean up, down, left, right, slowDown, shooting;

    public GamePanel(PlayerCharacter player, EnemyController enemy, BulletPool bulletPool) {
        this(player, enemy, bulletPool, new BulletPool(1000));
    }

    public GamePanel(PlayerCharacter player, EnemyController enemy, BulletPool bulletPool, BulletPool playerBulletPool) {
        // Pass bulletPool into the PatternSpawner constructor
        this(player, enemy, bulletPool, playerBulletPool, new PatternSpawner(bulletPool));
    }

    public GamePanel(PlayerCharacter player, EnemyController enemy, BulletPool bulletPool, BulletPool playerBulletPool, PatternSpawner patternSpawner) {
        super(1600, 900);
        this.player = player;
        this.bulletPool = bulletPool;
        this.playerBulletPool = playerBulletPool;
        this.itemPool = new ItemPool(20);

        // Fallback to a new spawner with bulletPool if null is passed
        PatternSpawner spawnerToUse = (patternSpawner != null) ? patternSpawner : new PatternSpawner(bulletPool);

        this.stageDirector = new StageDirector(
                spawnerToUse,
                player,
                this.itemPool,
                newBoss -> this.enemy = newBoss
        );

        if (this.stageDirector.getCurrentEnemy() != null) {
            this.enemy = this.stageDirector.getCurrentEnemy();
        } else {
            this.enemy = enemy;
        }

        setFocusTraversable(true);

        setOnKeyPressed(this::keyPressed);
        setOnKeyReleased(this::keyReleased);
    }

    public StackPane createContainer(GameOverMenu menu) {
        this.gameOverMenu = menu;
        StackPane root = new StackPane();
        root.getChildren().addAll(this, menu);
        return root;
    }

    public void showGameOver(boolean victory) {
        resetInputKeys();
        if (gameOverMenu != null) {
            gameOverMenu.show(victory);
        }
    }

    public ScoreManager getScoreManager() { return scoreManager; }
    public GameHudManager getHudManager() { return hudManager; }
    public DialogueSystem getDialogueSystem() { return dialogueSystem; }
    public StageDirector getStageDirector() { return stageDirector; }
    public ItemPool getItemPool() { return itemPool; }

    public void resetInputKeys() {
        up = false;
        down = false;
        left = false;
        right = false;
        slowDown = false;
        shooting = false;
    }

    public void update(double delta) {
        if (stageDirector != null) {
            if (stageDirector.isAllStagesCleared()) {
                showGameOver(true);
                return;
            }

            stageDirector.update();
            this.enemy = stageDirector.getCurrentEnemy();
        }

        if (itemPool != null && player != null) {
            itemPool.update(delta, player);
        }

        if (player != null) {
            player.update(delta, up, down, left, right, shooting, playerBulletPool, slowDown);
            if (player.isGameOver()) {
                showGameOver(false);
            }
        }

        if (enemy != null && !enemy.isDead()) {
            enemy.update(delta, getWidth(), getHeight());
        }
    }

    public void render() {
        GraphicsContext gc = getGraphicsContext2D();

        // Background
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, getWidth(), getHeight());

        // Item Drops
        if (itemPool != null) {
            itemPool.render(gc);
        }

        // Game Entities & Bullets
        if (enemy != null && !enemy.isDead()) {
            enemy.render(gc);
        }
        if (player != null) {
            player.render(gc);
        }

        if (bulletPool != null) {
            bulletPool.render(gc);
        }

        if (playerBulletPool != null) {
            playerBulletPool.render(gc);
        }

        hudManager.render(gc, getWidth(), getHeight(), player, enemy);

        // Stage HUD Title Header
        if (stageDirector != null) {
            gc.setFill(Color.WHITE);
            gc.setFont(Font.font("Consolas", FontWeight.BOLD, 18));
            gc.fillText(stageDirector.getCurrentStageTitle(), 20, 30);
        }

        if (dialogueSystem.isActive()) {
            dialogueSystem.render(gc, getWidth(), getHeight());
        }
    }

    public void keyPressed(KeyEvent e) {
        KeyCode code = e.getCode();
        if (code == KeyCode.W || code == KeyCode.UP) up = true;
        if (code == KeyCode.S || code == KeyCode.DOWN) down = true;
        if (code == KeyCode.A || code == KeyCode.LEFT) left = true;
        if (code == KeyCode.D || code == KeyCode.RIGHT) right = true;
        if (code == KeyCode.SHIFT) slowDown = true;
        if (code == KeyCode.SPACE || code == KeyCode.Z || code == KeyCode.J) shooting = true;
    }

    public void keyReleased(KeyEvent e) {
        KeyCode code = e.getCode();
        if (code == KeyCode.W || code == KeyCode.UP) up = false;
        if (code == KeyCode.S || code == KeyCode.DOWN) down = false;
        if (code == KeyCode.A || code == KeyCode.LEFT) left = false;
        if (code == KeyCode.D || code == KeyCode.RIGHT) right = false;
        if (code == KeyCode.SHIFT) slowDown = false;
        if (code == KeyCode.SPACE || code == KeyCode.Z || code == KeyCode.J) shooting = false;
    }

    public boolean isUp() { return up; }
    public boolean isDown() { return down; }
    public boolean isLeft() { return left; }
    public boolean isRight() { return right; }
    public boolean isSlowDown() { return slowDown; }
    public boolean isShooting() { return shooting; }

    public BulletPool getBulletPool() { return bulletPool; }
    public BulletPool getPlayerBulletPool() { return playerBulletPool; }

    public GameOverMenu getGameOverMenu() { return gameOverMenu; }
    public void setGameOverMenu(GameOverMenu gameOverMenu) { this.gameOverMenu = gameOverMenu; }

    @SuppressWarnings("unused")
    public EnemyController getEnemy() { return enemy; }

    public PlayerCharacter getPlayer() { return player; }

    public void setEnemy(EnemyController enemy) { this.enemy = enemy; }
}