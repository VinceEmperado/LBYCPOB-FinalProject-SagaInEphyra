package core;

public class ScoreManager {

    private long currentScore;
    private long highScore;
    private double multiplier;
    private static final long GRAZE_POINTS = 10;
    private static final double GRAZE_MULTIPLIER_BONUS = 0.05;

    public ScoreManager() {
        this.currentScore = 0;
        this.highScore = 0;
        this.multiplier = 1.0;
    }

    public void addScore(long basePoints) {
        if (basePoints <= 0) return;

        long pointsEarned = Math.round(basePoints * multiplier);
        currentScore += pointsEarned;

        if (currentScore > highScore) {
            highScore = currentScore;
        }
    }

    public void registerGraze() {
        addScore(GRAZE_POINTS);
        incrementMultiplier(GRAZE_MULTIPLIER_BONUS);
    }

    public void setScore(long score) {
        this.currentScore = Math.max(0, score);
        if (this.currentScore > this.highScore) {
            this.highScore = this.currentScore;
        }
    }

    public void setMultiplier(double multiplier) {
        this.multiplier = Math.max(1.0, multiplier);
    }

    public void incrementMultiplier(double amount) {
        this.multiplier = Math.max(1.0, this.multiplier + amount);
    }

    public void resetMultiplier() {
        this.multiplier = 1.0;
    }

    public void resetScore() {
        this.currentScore = 0;
        this.multiplier = 1.0;
    }

    public long getScore() { return currentScore; }
    public long getHighScore() { return highScore; }
    public double getMultiplier() { return multiplier; }
}