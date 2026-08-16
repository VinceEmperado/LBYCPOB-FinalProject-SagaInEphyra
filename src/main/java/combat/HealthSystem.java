package combat;

public class HealthSystem {
    private final double maxHealth;
    private double currentHealth;
    private boolean isInvulnerable = false;

    public HealthSystem(double maxHealth) {
        this.maxHealth = maxHealth;
        this.currentHealth = maxHealth;
    }

    public void takeDamage(double amount) {
        if (isInvulnerable || currentHealth <= 0) return;
        currentHealth = Math.max(0, currentHealth - amount);
    }

    public void heal(double amount) {
        if (currentHealth <= 0) return;
        currentHealth = Math.min(maxHealth, currentHealth + amount);
    }

    public boolean isDead() {
        return currentHealth <= 0;
    }

    public double getCurrentHealth() {
        return currentHealth;
    }

    public double getMaxHealth() {
        return maxHealth;
    }

    public double getHealthPercentage() {
        return maxHealth > 0 ? currentHealth / maxHealth : 0.0;
    }

    public void setInvulnerable(boolean invulnerable) {
        this.isInvulnerable = invulnerable;
    }

    public boolean isInvulnerable() {
        return isInvulnerable;
    }

    public void reset() {
        this.currentHealth = maxHealth;
        this.isInvulnerable = false;
    }
}