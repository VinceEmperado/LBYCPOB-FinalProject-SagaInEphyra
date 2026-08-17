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
        // Matched callbacks with MainMenu(onLogin, onStart, onLeaderboard, onExit)
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
                    // Handle login submission logic (e.g., authenticate user)
                    System.out.println("User logged in: " + username);
                    showMainMenu(primaryStage);
                },
                () -> showMainMenu(primaryStage)
        );

        Scene loginScene = new Scene(loginMenu, 1600, 900);
        primaryStage.setScene(loginScene);
    }

    private void showLeaderboardMenu(Stage primaryStage) {
        // Opens leaderboard from the main menu with default 0 current score view
        LeaderboardMenu leaderboardMenu = new LeaderboardMenu(
                0,
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

        // GameOverMenu returns to main menu on exit
        GameOverMenu gameOverMenu = new GameOverMenu(
                () -> restartGame(primaryStage),
                () -> {
                    if (loopManager != null) loopManager.stop();
                    showMainMenu(primaryStage);
                }
        );
        gamePanel.setGameOverMenu(gameOverMenu);

        Scene scene = createGameScene(gamePanel, bulletPool, player, patternSpawner, gameOverMenu);

        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();

        gamePanel.requestFocus();
        loopManager.start();
    }

    private Scene createGameScene(GamePanel gamePanel, BulletPool bulletPool, PlayerCharacter player,
                                  PatternSpawner patternSpawner, GameOverMenu gameOverMenu) {

        TestersMenu testersMenu = new TestersMenu(
                bulletPool,
                1600,
                900,
                player,
                patternSpawner,
                newEnemy -> {
                    gamePanel.setEnemy(newEnemy);
                    loopManager.setEnemy(newEnemy);
                }
        );

        testersMenu.setLayoutX(10);
        testersMenu.setLayoutY(10);
        testersMenu.setPrefHeight(400);

        // Wrap TestersMenu in a non-blocking Pane to maintain absolute layout positioning
        Pane testersOverlay = new Pane(testersMenu);
        testersOverlay.setPickOnBounds(false);

        // Stack layers: Canvas -> Testers Menu -> Game Over Menu Overlay
        StackPane root = new StackPane(gamePanel, testersOverlay, gameOverMenu);
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