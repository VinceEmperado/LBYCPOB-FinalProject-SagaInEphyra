package core;

import java.io.*;
import java.util.Properties;

public class SaveManager {

    private static final String SAVE_FILE = "player_saves.properties";

    private static String currentUser = "GUEST";
    private static long currentScore = 0;
    private static int currentStageIndex = 0;

    public static void loginUser(String username) {
        if (username == null || username.trim().isEmpty()) {
            currentUser = "GUEST";
        } else {
            currentUser = username.trim().toUpperCase();
        }
        loadProgress(currentUser);
    }

    public static boolean hasSave() {
        Properties props = loadAllData();
        return props.containsKey(currentUser + ".score") || props.containsKey(currentUser + ".stageIndex");
    }

    public static void saveCurrentProgress(long score, int stageIndex) {
        currentScore = score;
        currentStageIndex = stageIndex;

        Properties props = loadAllData();
        props.setProperty(currentUser + ".score", String.valueOf(currentScore));
        props.setProperty(currentUser + ".stageIndex", String.valueOf(currentStageIndex));

        try (FileOutputStream out = new FileOutputStream(SAVE_FILE)) {
            props.store(out, "Saga in Ephyra - Player Progress Data");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadProgress(String username) {
        Properties props = loadAllData();

        String savedScore = props.getProperty(username + ".score", "0");
        String savedStage = props.getProperty(username + ".stageIndex", "0");

        try {
            currentScore = Long.parseLong(savedScore);
            currentStageIndex = Integer.parseInt(savedStage);
        } catch (NumberFormatException e) {
            currentScore = 0;
            currentStageIndex = 0;
        }
    }

    public static void resetCurrentProgress() {
        currentScore = 0;
        currentStageIndex = 0;
        saveCurrentProgress(0, 0);
    }

    private static Properties loadAllData() {
        Properties props = new Properties();
        File file = new File(SAVE_FILE);
        if (file.exists()) {
            try (FileInputStream in = new FileInputStream(file)) {
                props.load(in);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return props;
    }

    public static String getCurrentUser() {
        return currentUser;
    }

    public static long getCurrentScore() {
        return currentScore;
    }

    public static int getCurrentStageIndex() {
        return currentStageIndex;
    }

    public static int getSavedStageIndex() {
        return currentStageIndex;
    }

    public static void setCurrentScore(long score) {
        currentScore = score;
    }

    public static void setCurrentStageIndex(int stageIndex) {
        currentStageIndex = stageIndex;
    }

    public static void resetSessionProgress() {
        currentScore = 0;
        currentStageIndex = 0;
    }
}