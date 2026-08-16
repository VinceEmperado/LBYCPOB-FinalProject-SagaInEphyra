package combat;

public class DamagePacket {
    private double amount;

    public DamagePacket() {
        this.amount = 0.0;
    }

    public DamagePacket(double amount) {
        this.amount = amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getAmount() {
        return amount;
    }
}