package core;

import combat.PatternSpawner;
import entities.enemies.EnemyController;
import entities.PlayerCharacter;
import pools.BulletPool;
import ui.TestersMenu;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class GameLauncher extends Application {

    @Override
    public void start(Stage primaryStage) {
        BulletPool bulletPool = new BulletPool(1000);
        PatternSpawner patternSpawner = new PatternSpawner(bulletPool);

        PlayerCharacter player = new PlayerCharacter(780, 800);
        EnemyController enemy = new EnemyController(100, 150, patternSpawner);

        GamePanel gamePanel = new GamePanel(player, enemy, bulletPool);
        GameLoopManager loopManager = new GameLoopManager(gamePanel, player, enemy);

        // Passed player as the 3rd argument here
        Scene scene = createGameScene(gamePanel, bulletPool, player);

        primaryStage.setTitle("Saga in Ephyra");
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.centerOnScreen();

        gamePanel.requestFocus();
        loopManager.start();
    }

    private Scene createGameScene(GamePanel gamePanel, BulletPool bulletPool, PlayerCharacter player) {
        TestersMenu testersMenu = new TestersMenu(bulletPool, 1600, 900, player);
        testersMenu.setLayoutX(10);
        testersMenu.setLayoutY(10);
        testersMenu.setPrefHeight(400);

        Pane root = new Pane(gamePanel, testersMenu);
        Scene scene = new Scene(root, 1600, 900);

        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.F1) {
                testersMenu.setVisible(!testersMenu.isVisible());
            }
        });

        return scene;
    }
}