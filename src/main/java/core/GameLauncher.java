package core;

import entities.EnemyController;
import entities.PlayerCharacter;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;


import static javafx.application.Application.launch;

public class GameLauncher extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        PlayerCharacter player = new PlayerCharacter(390, 500);
        EnemyController enemy = new EnemyController(100, 150);
        GamePanel gamePanel =  new GamePanel(player, enemy);
        GameLoopManager loopManager = new GameLoopManager(gamePanel, player, enemy);

        Pane root = new Pane(gamePanel.getCanvas());
        Scene scene = new Scene(root, 1900, 600);
        gamePanel.inputHandling(scene);

        primaryStage.setTitle("Saga in Ephyra");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.setOnCloseRequest(e -> {
            loopManager.stop();
            System.exit(0);
        });
        primaryStage.show();

        gamePanel.getCanvas().requestFocus();
        loopManager.start();
    }
}
