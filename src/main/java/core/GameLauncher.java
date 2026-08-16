package core;

import combat.PatternSpawner;
import entities.enemies.Clawdia;
import entities.enemies.EnemyController;
import entities.PlayerCharacter;
import pools.BulletPool;
import ui.GameOverMenu;
import ui.TestersMenu;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class GameLauncher extends Application {

    private GameLoopManager loopManager;

    @Override
    public void start(Stage primaryStage) {
        BulletPool bulletPool = new BulletPool(1000);
        PatternSpawner patternSpawner = new PatternSpawner(bulletPool);

        PlayerCharacter player = new PlayerCharacter(780, 800);
        EnemyController enemy = new Clawdia(100, 150, patternSpawner, player);

        GamePanel gamePanel = new GamePanel(player, enemy, bulletPool);
        loopManager = new GameLoopManager(gamePanel, player, enemy);

        // Instantiate GameOverMenu with callbacks
        GameOverMenu gameOverMenu = new GameOverMenu(
                () -> restartGame(primaryStage),
                () -> System.exit(0)
        );
        gamePanel.setGameOverMenu(gameOverMenu);

        // Pass patternSpawner to createGameScene
        Scene scene = createGameScene(gamePanel, bulletPool, player, patternSpawner, gameOverMenu);

        primaryStage.setTitle("Saga in Ephyra");
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.show();
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
        start(stage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}