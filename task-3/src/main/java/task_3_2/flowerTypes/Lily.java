package task_3_2.flowerTypes;

import task_3_2.Flower;

public class Lily extends Flower {
    private int petalCount;

    public Lily(String color, double price, int petalCount) {
        super("Лилия", price, color);
        this.petalCount = petalCount;
    }

    @Override
    public String toString() {
        return super.toString() + " - " + petalCount + " лепестков";
    }
}
