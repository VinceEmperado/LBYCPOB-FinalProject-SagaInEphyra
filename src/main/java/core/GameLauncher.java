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
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.util.Optional;

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
        AudioManager.getInstance().playBGM("/audio/bgm/menu_theme.mp3", true);

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
        boolean loadSavedData = false;

        // Check if there is existing save data for the active user
        if (SaveManager.hasSave()) {
            Alert saveAlert = new Alert(Alert.AlertType.CONFIRMATION);
            saveAlert.setTitle("Saved Progress Found");
            saveAlert.setHeaderText("A save file was detected for [" + SaveManager.getCurrentUser() + "]");
            saveAlert.setContentText("Would you like to continue from your saved progress or start a new game?");

            ButtonType btnContinue = new ButtonType("Continue", ButtonBar.ButtonData.OK_DONE);
            ButtonType btnNewGame = new ButtonType("New Game", ButtonBar.ButtonData.OTHER);
            ButtonType btnCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

            saveAlert.getButtonTypes().setAll(btnContinue, btnNewGame, btnCancel);

            Optional<ButtonType> choice = saveAlert.showAndWait();
            if (choice.isPresent()) {
                if (choice.get() == btnContinue) {
                    loadSavedData = true;
                } else if (choice.get() == btnCancel) {
                    return; // Cancel start, stay on Main Menu
                } else if (choice.get() == btnNewGame) {
                    SaveManager.resetCurrentProgress();
                    loadSavedData = false;
                }
            }
        }

        BulletPool bulletPool = new BulletPool(1000);
        PatternSpawner patternSpawner = new PatternSpawner(bulletPool);

        PlayerCharacter player = new PlayerCharacter(780, 800);
        EnemyController enemy = new Clawdia(100, 150, patternSpawner, player);

        GamePanel gamePanel = new GamePanel(player, enemy, bulletPool);
        loopManager = new GameLoopManager(gamePanel, player, enemy);

        // Apply saved score and stage index if continuing
        if (loadSavedData) {
            long score = SaveManager.getCurrentScore();
            int stageIndex = SaveManager.getSavedStageIndex();

            gamePanel.getScoreManager().setScore(score);
            if (gamePanel.getStageDirector() != null) {
                gamePanel.getStageDirector().setCurrentStageIndex(stageIndex);
            }
            System.out.println("Loaded game save | Score: " + score + " | Stage Index: " + stageIndex);
        } else {
            if (gamePanel.getStageDirector() != null) {
                gamePanel.getStageDirector().setCurrentStageIndex(0);
            }
        }

        // Pause Menu Callbacks
        PauseMenu pauseMenu = new PauseMenu(
                () -> {
                    gamePanel.setPaused(false);
                    AudioManager.getInstance().resumeBGM();
                    gamePanel.requestFocus();
                },
                () -> saveGame(gamePanel),
                () -> restartGame(primaryStage),
                () -> {
                    if (loopManager != null) loopManager.stop();
                    showMainMenu(primaryStage);
                }
        );
        pauseMenu.setVisible(false);
        gamePanel.setPauseMenu(pauseMenu);

        // GameOverMenu Callbacks
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

        StackPane root = new StackPane(gamePanel, testersOverlay, pauseMenu, gameOverMenu);
        Scene scene = new Scene(root, 1600, 900);

        scene.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            if (e.getCode() == KeyCode.ESCAPE || e.getCode() == KeyCode.P) {
                gamePanel.togglePause();
                if (gamePanel.isPaused()) {
                    AudioManager.getInstance().pauseBGM();
                } else {
                    AudioManager.getInstance().resumeBGM();
                    gamePanel.requestFocus();
                }
                e.consume();
            } else if (!gamePanel.isPaused()) {
                gamePanel.keyPressed(e);
            }
        });

        scene.addEventFilter(KeyEvent.KEY_RELEASED, e -> {
            if (!gamePanel.isPaused()) {
                gamePanel.keyReleased(e);
            }
        });

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
        AudioManager.getInstance().stopBGM();
        startGame(stage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}