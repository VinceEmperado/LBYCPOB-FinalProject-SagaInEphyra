package core;

import combat.PatternSpawner;
import entities.boss.Kanaloa;
import entities.enemies.EnemyController;
import entities.PlayerCharacter;
import pools.BulletPool;
import pools.ItemPool;
import ui.DialogueSystem;
import ui.GameHudManager;
import ui.GameOverMenu;
import ui.PauseMenu;

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
    private PauseMenu pauseMenu;
    private boolean isPaused = false;

    private final ScoreManager scoreManager = new ScoreManager();
    private final GameHudManager hudManager = new GameHudManager(scoreManager);
    private final DialogueSystem dialogueSystem = new DialogueSystem();

    private boolean up, down, left, right, slowDown, shooting;

    public GamePanel(PlayerCharacter player, EnemyController enemy, BulletPool bulletPool) {
        this(player, enemy, bulletPool, new BulletPool(1000));
    }

    public GamePanel(PlayerCharacter player, EnemyController enemy, BulletPool bulletPool, BulletPool playerBulletPool) {
        this(player, enemy, bulletPool, playerBulletPool, new PatternSpawner(bulletPool));
    }

    public GamePanel(PlayerCharacter player, EnemyController enemy, BulletPool bulletPool, BulletPool playerBulletPool, PatternSpawner patternSpawner) {
        super(1600, 900);
        this.player = player;
        this.bulletPool = bulletPool;
        this.playerBulletPool = playerBulletPool;
        this.itemPool = new ItemPool(20);

        PatternSpawner spawnerToUse = (patternSpawner != null) ? patternSpawner : new PatternSpawner(bulletPool);

        this.stageDirector = new StageDirector(
                spawnerToUse,
                player,
                this.itemPool,
                newBoss -> setEnemy(newBoss) // Connect new bosses to DialogueSystem on spawn
        );

        if (this.stageDirector.getCurrentEnemy() != null) {
            setEnemy(this.stageDirector.getCurrentEnemy());
        } else if (enemy != null) {
            setEnemy(enemy);
        }

        setFocusTraversable(true);

        setOnKeyPressed(this::keyPressed);
        setOnKeyReleased(this::keyReleased);
    }

    /**
     * Builds the root container layering Canvas, Pause Menu, and GameOver Menu.
     */
    public StackPane createContainer(GameOverMenu menu) {
        return createContainer(menu, new PauseMenu(
                () -> setPaused(false), // Resume callback
                () -> restartGame(),    // Restart callback
                () -> quitGame()        // Quit callback
        ));
    }

    public StackPane createContainer(GameOverMenu menu, PauseMenu pauseMenu) {
        this.gameOverMenu = menu;
        this.pauseMenu = pauseMenu;

        if (this.pauseMenu != null) {
            this.pauseMenu.setVisible(false);
            this.pauseMenu.setOnResume(() -> setPaused(false));
        }

        StackPane root = new StackPane();
        root.getChildren().add(this);

        if (this.pauseMenu != null) {
            root.getChildren().add(this.pauseMenu);
        }
        if (this.gameOverMenu != null) {
            root.getChildren().add(this.gameOverMenu);
        }

        return root;
    }

    public void togglePause() {
        setPaused(!isPaused);
    }

    public void setPaused(boolean paused) {
        this.isPaused = paused;
        resetInputKeys();

        if (pauseMenu != null) {
            pauseMenu.setVisible(isPaused);
            if (isPaused) {
                pauseMenu.toFront();
            }
        }
    }

    public boolean isPaused() {
        return isPaused;
    }

    private void restartGame() {
        setPaused(false);
        // Add stage/player reset logic here if needed
    }

    private void quitGame() {
        setPaused(false);
        // Add scene transition/quit logic here
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
        // Freeze game logic while paused
        if (isPaused) return;

        // Update dialogue typewriter/display timer
        if (dialogueSystem != null) {
            dialogueSystem.update(delta);
        }

        if (stageDirector != null) {
            if (stageDirector.isAllStagesCleared()) {
                showGameOver(true);
                return;
            }

            stageDirector.update();

            // Sync active enemy and ensure dialogue system reference persists across stages
            EnemyController currentStageEnemy = stageDirector.getCurrentEnemy();
            if (currentStageEnemy != null && currentStageEnemy != this.enemy) {
                setEnemy(currentStageEnemy);
            }
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

        // 1. Background
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, getWidth(), getHeight());

        // 2. Item Drops
        if (itemPool != null) {
            itemPool.render(gc);
        }

        // 3. Game Entities & Bullets
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

        // 4. Kanaloa Abyssal Lighting Fog-of-War (Renders over world entities)
        if (enemy instanceof Kanaloa kanaloa && !kanaloa.isDead()) {
            kanaloa.renderAbyssalLighting(gc, getWidth(), getHeight());
        }

        // 5. HUD
        hudManager.render(gc, getWidth(), getHeight(), player, enemy);

        // Stage Title Header
        if (stageDirector != null) {
            gc.setFill(Color.WHITE);
            gc.setFont(Font.font("Consolas", FontWeight.BOLD, 18));
            gc.fillText(stageDirector.getCurrentStageTitle(), 20, 30);
        }

        // 6. Dialogue System Overlay
        if (dialogueSystem != null && dialogueSystem.isActive()) {
            dialogueSystem.render(gc, getWidth(), getHeight());
        }
    }

    public void keyPressed(KeyEvent e) {
        KeyCode code = e.getCode();

        // Pause Toggle
        if (code == KeyCode.ESCAPE || code == KeyCode.P) {
            togglePause();
            return;
        }

        if (isPaused) return;

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

    public PauseMenu getPauseMenu() { return pauseMenu; }
    public void setPauseMenu(PauseMenu pauseMenu) { this.pauseMenu = pauseMenu; }

    @SuppressWarnings("unused")
    public EnemyController getEnemy() { return enemy; }
    public PlayerCharacter getPlayer() { return player; }

    public void setEnemy(EnemyController enemy) {
        this.enemy = enemy;
        if (this.enemy != null) {
            this.enemy.setDialogueSystem(this.dialogueSystem);
        }
    }
}