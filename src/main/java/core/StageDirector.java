package core;

import combat.PatternSpawner;
import entities.PlayerCharacter;
import entities.boss.*;
import entities.enemies.*;
import pools.ItemPool;

import java.util.function.Consumer;

public class StageDirector {

    private final PatternSpawner patternSpawner;
    private final PlayerCharacter player;
    private final Consumer<EnemyController> onEnemyChanged;
    private final ItemPool itemPool;

    // 0: Stage 1 Boss (Clawdia)
    // 1: Stage 1 Stage Boss (Kanaloa)
    // 2: Stage 2 Boss (Finana)
    // 3: Stage 2 Stage Boss (Thalasaa)
    // 4: Final Stage Boss (Skana)
    private int currentStageIndex = 0;
    private EnemyController currentEnemy;
    private boolean allStagesCleared = false;

    public StageDirector(PatternSpawner patternSpawner, PlayerCharacter player, ItemPool itemPool, Consumer<EnemyController> onEnemyChanged) {
        this.patternSpawner = patternSpawner;
        this.player = player;
        this.itemPool = itemPool;
        this.onEnemyChanged = onEnemyChanged;
        loadStageBoss(currentStageIndex);
    }

    public void update() {
        if (currentEnemy != null && currentEnemy.isDead()) {
            // Drop loot at defeated boss position before advancing
            if (itemPool != null) {
                itemPool.spawnBossLoot(currentEnemy.getX(), currentEnemy.getY());
            }
            advanceToNextBoss();
        }
    }

    public void advanceToNextBoss() {
        currentStageIndex++;
        if (currentStageIndex > 4) {
            allStagesCleared = true;
            AudioManager.getInstance().playBGM("/audio/bgm/victory.mp3", false);
            return;
        }
        loadStageBoss(currentStageIndex);
    }

    private void loadStageBoss(int index) {
        switch (index) {
            case 0 -> {
                currentEnemy = new Clawdia(780, 150, patternSpawner, player);
                AudioManager.getInstance().playBGM("/audio/bgm/stage1_clawdia.mp3", true);
            }
            case 1 -> {
                currentEnemy = new Kanaloa(780, 150, patternSpawner, player);
                AudioManager.getInstance().playBGM("/audio/bgm/stage1_kanaloa.mp3", true);
            }
            case 2 -> {
                currentEnemy = new Finana(780, 150, patternSpawner, player);
                AudioManager.getInstance().playBGM("/audio/bgm/stage2_finana.mp3", true);
            }
            case 3 -> {
                currentEnemy = new Thalasaa(780, 150, patternSpawner, player);
                AudioManager.getInstance().playBGM("/audio/bgm/stage2_thalasaa.mp3", true);
            }
            case 4 -> {
                currentEnemy = new Skana(780, 150, patternSpawner, player);
                AudioManager.getInstance().playBGM("/audio/bgm/final_skana.mp3", true);
            }
            default -> {
                allStagesCleared = true;
                AudioManager.getInstance().playBGM("/audio/bgm/victory.mp3", false);
                return;
            }
        }

        if (onEnemyChanged != null) {
            onEnemyChanged.accept(currentEnemy);
        }
    }

    public EnemyController getCurrentEnemy() {
        return currentEnemy;
    }

    public int getCurrentStageIndex() {
        return currentStageIndex;
    }

    public void setCurrentStageIndex(int currentStageIndex) {
        this.currentStageIndex = currentStageIndex;
        if (this.currentStageIndex > 4) {
            this.allStagesCleared = true;
            AudioManager.getInstance().playBGM("/audio/bgm/victory.mp3", false);
        } else {
            this.allStagesCleared = false;
            loadStageBoss(this.currentStageIndex);
        }
    }

    public boolean isAllStagesCleared() {
        return allStagesCleared;
    }

    public String getCurrentStageTitle() {
        return switch (currentStageIndex) {
            case 0 -> "STAGE 1: Clawdia (Crab)";
            case 1 -> "STAGE 1 STAGE BOSS: Kanaloa (Octopus)";
            case 2 -> "STAGE 2: Finana (Dolphin)";
            case 3 -> "STAGE 2 STAGE BOSS: Thalasaa (Shark)";
            case 4 -> "FINAL STAGE: Skana (Orca)";
            default -> "VICTORY";
        };
    }
}