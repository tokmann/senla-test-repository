package task_3_2.flowerTypes;

import task_3_2.Flower;

public class Tulip extends Flower {
    private String pattern;

    public Tulip(String color, double price, String pattern) {
        super("Тюльпан", price, color);
        this.pattern = pattern;
    }

    @Override
    public String toString() {
        return pattern.isEmpty() ? super.toString() :
                super.toString() + " [" + pattern + "]";
    }
}
