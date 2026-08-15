package core;

import combat.PatternSpawner;
import entities.EnemyController;
import entities.PlayerCharacter;
import pools.BulletPool;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class GameLauncher extends Application {
    public void start(Stage primaryStage) {
        // 1. Initialize bullet handling infrastructure
        BulletPool bulletPool = new BulletPool(1000);
        PatternSpawner patternSpawner = new PatternSpawner(bulletPool);

        // 2. Instantiate entities (passing patternSpawner to enemy)
        PlayerCharacter player = new PlayerCharacter(780, 800);
        EnemyController enemy = new EnemyController(100, 150, patternSpawner);

        // 3. Instantiate GamePanel with bulletPool for rendering/updates
        GamePanel gamePanel = new GamePanel(player, enemy, bulletPool);
        GameLoopManager loopManager = new GameLoopManager(gamePanel, player, enemy);

        Pane root = new Pane(gamePanel);

        // Sets the scene dimensions
        Scene scene = new Scene(root, 1600, 900);

        // Title bar
        primaryStage.setTitle("Saga in Ephyra");

        // Prevents users from resizing the window
        primaryStage.setResizable(false);

        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.centerOnScreen();

        gamePanel.requestFocus();
        loopManager.start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}