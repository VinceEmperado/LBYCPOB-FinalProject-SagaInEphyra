package core;

import combat.PatternSpawner;
import entities.enemies.Clawdia;
import entities.enemies.EnemyController;
import entities.PlayerCharacter;
import pools.BulletPool;
import ui.GameOverMenu;
import ui.LeaderboardMenu;
import ui.LoginMenu;
import ui.MainMenu;
import ui.PauseMenu;
import ui.TestersMenu;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class GameLauncher extends Application {

    private GameLoopManager loopManager;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Saga in Ephyra");
        primaryStage.setResizable(false);

        showMainMenu(primaryStage);

        primaryStage.show();
        primaryStage.centerOnScreen();
    }

    private void showMainMenu(Stage primaryStage) {
        MainMenu mainMenu = new MainMenu(
                () -> showLoginMenu(primaryStage),
                () -> startGame(primaryStage),
                () -> showLeaderboardMenu(primaryStage),
                () -> System.exit(0)
        );

        Scene menuScene = new Scene(mainMenu, 1600, 900);
        primaryStage.setScene(menuScene);
    }

    private void showLoginMenu(Stage primaryStage) {
        LoginMenu loginMenu = new LoginMenu(
                (username, password) -> {
                    System.out.println("User logged in: " + username);
                    showMainMenu(primaryStage);
                },
                () -> showMainMenu(primaryStage)
        );

        Scene loginScene = new Scene(loginMenu, 1600, 900);
        primaryStage.setScene(loginScene);
    }

    private void showLeaderboardMenu(Stage primaryStage) {
        LeaderboardMenu leaderboardMenu = new LeaderboardMenu(
                SaveManager.getCurrentScore(),
                () -> showMainMenu(primaryStage)
        );

        Scene leaderboardScene = new Scene(leaderboardMenu, 1600, 900);
        primaryStage.setScene(leaderboardScene);
    }

    private void startGame(Stage primaryStage) {
        BulletPool bulletPool = new BulletPool(1000);
        PatternSpawner patternSpawner = new PatternSpawner(bulletPool);

        PlayerCharacter player = new PlayerCharacter(780, 800);
        EnemyController enemy = new Clawdia(100, 150, patternSpawner, player);

        GamePanel gamePanel = new GamePanel(player, enemy, bulletPool);
        loopManager = new GameLoopManager(gamePanel, player, enemy);

        // Apply saved user score if progress was loaded
        if (SaveManager.getCurrentScore() > 0) {
            gamePanel.getScoreManager().setScore(SaveManager.getCurrentScore());
        }

        // Pause Menu Callbacks (Resume, Save, Restart, Quit)
        PauseMenu pauseMenu = new PauseMenu(
                () -> gamePanel.setPaused(false), // Resume
                () -> saveGame(gamePanel),        // Save Game
                () -> restartGame(primaryStage),  // Restart
                () -> {                            // Quit to Main Menu
                    if (loopManager != null) loopManager.stop();
                    showMainMenu(primaryStage);
                }
        );
        pauseMenu.setVisible(false);
        gamePanel.setPauseMenu(pauseMenu);

        // GameOverMenu Callbacks (Connected live score supplier)
        GameOverMenu gameOverMenu = new GameOverMenu(
                () -> restartGame(primaryStage),
                () -> {
                    if (loopManager != null) loopManager.stop();
                    showMainMenu(primaryStage);
                },
                () -> gamePanel.getScoreManager().getScore()
        );
        gamePanel.setGameOverMenu(gameOverMenu);

        Scene scene = createGameScene(gamePanel, bulletPool, player, patternSpawner, pauseMenu, gameOverMenu);

        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();

        gamePanel.requestFocus();
        loopManager.start();
    }

    private Scene createGameScene(GamePanel gamePanel, BulletPool bulletPool, PlayerCharacter player,
                                  PatternSpawner patternSpawner, PauseMenu pauseMenu, GameOverMenu gameOverMenu) {

        TestersMenu testersMenu = new TestersMenu(
                bulletPool,
                1600,
                900,
                player,
                patternSpawner,
                newEnemy -> {
                    gamePanel.setEnemy(newEnemy);
                    loopManager.setEnemy(newEnemy);
                },
                gamePanel.getDialogueSystem()
        );

        testersMenu.setLayoutX(10);
        testersMenu.setLayoutY(10);
        testersMenu.setPrefHeight(400);

        Pane testersOverlay = new Pane(testersMenu);
        testersOverlay.setPickOnBounds(false);

        // Stack layers: Canvas -> Testers Menu -> Pause Menu -> Game Over Menu
        StackPane root = new StackPane(gamePanel, testersOverlay, pauseMenu, gameOverMenu);
        Scene scene = new Scene(root, 1600, 900);

        scene.addEventFilter(KeyEvent.KEY_PRESSED, gamePanel::keyPressed);
        scene.addEventFilter(KeyEvent.KEY_RELEASED, gamePanel::keyReleased);

        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.F1) {
                testersMenu.setVisible(!testersMenu.isVisible());
            }
        });

        return scene;
    }

    private void saveGame(GamePanel gamePanel) {
        long score = gamePanel.getScoreManager().getScore();
        int stageIndex = gamePanel.getStageDirector().getCurrentStageIndex();

        SaveManager.saveCurrentProgress(score, stageIndex);
        System.out.println("Saved progress for user [" + SaveManager.getCurrentUser() +
                "] | Stage Index: " + stageIndex + " | Score: " + score);
    }

    private void restartGame(Stage stage) {
        if (loopManager != null) {
            loopManager.stop();
        }
        startGame(stage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}