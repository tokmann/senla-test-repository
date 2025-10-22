package task_3_2;

public class Packaging {
    private String type;
    private double price;

    public Packaging(String type, double price) {
        this.type = type;
        this.price = price;
    }

    public double getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return "Упаковка - " +
                "тип: '" + type + '\'' +
                ", цена: " + price;
    }
}
