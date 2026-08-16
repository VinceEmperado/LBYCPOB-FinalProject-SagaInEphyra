package combat;

public class DamagePacket {
    private double amount;
    private double sourceX;
    private double sourceY;

    public DamagePacket(double amount) {
        this.amount = amount;
    }

    public DamagePacket(double amount, double sourceX, double sourceY) {
        this.amount = amount;
        this.sourceX = sourceX;
        this.sourceY = sourceY;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getSourceX() {
        return sourceX;
    }

    public void setSourceX(double sourceX) {
        this.sourceX = sourceX;
    }

    public double getSourceY() {
        return sourceY;
    }

    public void setSourceY(double sourceY) {
        this.sourceY = sourceY;
    }

}