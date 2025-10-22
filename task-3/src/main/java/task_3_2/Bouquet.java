package task_3_2;

import java.util.ArrayList;
import java.util.List;

public class Bouquet {
    private List<Flower> flowers;
    private Packaging packaging;
    private String name;

    public Bouquet(String name) {
        this.name = name;
        this.flowers = new ArrayList<>();
    }

    public void addFlower(Flower flower) {
        flowers.add(flower);
    }

    public void setPackaging(Packaging packaging) {
        this.packaging = packaging;
    }

    public double calculateTotalPrice() {
        double flowerCost = flowers.stream()
                .mapToDouble(Flower::getPrice)
                .sum();

        double packagingCost = (packaging != null) ? packaging.getPrice() : 0;

        return flowerCost + packagingCost;
    }

    public void displayBouquetInfo() {
        System.out.println("=== " + name + " ===");
        System.out.println("Цветы в букете:");
        flowers.forEach(flower -> System.out.println(" * " + flower));

        if (packaging != null) {
            System.out.println(" * " + packaging);
        }

        System.out.println("Общая стоимость: " + calculateTotalPrice() + "руб.");
    }

    public int getFlowerCount() {
        return flowers.size();
    }
}
