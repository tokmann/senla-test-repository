package task_3_2;

public abstract class Flower {
    protected String name;
    protected double price;
    protected String color;

    public Flower(String name, double price, String color) {
        this.name = name;
        this.price = price;
        this.color = color;
    }

    public double getPrice() {
        return price;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }


    @Override
    public String toString() {
        return "Цветок - " +
                "название: '" + name + '\'' +
                ", цена: " + price +
                ", цвет: '" + color + '\'';
    }
}
