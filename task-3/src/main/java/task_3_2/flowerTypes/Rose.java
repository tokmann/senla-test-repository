package task_3_2.flowerTypes;

import task_3_2.Flower;

public class Rose extends Flower {
    private boolean hasThorns;

    public Rose(String color, double price, boolean hasThorns) {
        super("Роза", price, color);
        this.hasThorns = hasThorns;
    }

    @Override
    public String toString() {
        return super.toString() + (hasThorns ? " (с шипами)" : " (без шипов)");
    }
}
