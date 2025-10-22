package task_3_2.flowerTypes;

import task_3_2.Flower;

public class Chrysanthemum extends Flower {
    private boolean isLarge;

    public Chrysanthemum(String color, double price, boolean isLarge) {
        super("Хризантема", price, color);
        this.isLarge = isLarge;
    }

    @Override
    public String toString() {
        return super.toString() + (isLarge ? " (крупная)" : " (мелкая)");
    }
}
