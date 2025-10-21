package task_3_3.CarDetails;

import task_3_3.Interfaces.IProductPart;

public class CarBody implements IProductPart {

    private final String carBodyType;

    public CarBody(String carBodyType) {
        this.carBodyType = carBodyType;
        System.out.println("Создан кузов типа: " + carBodyType);
    }

    public String getType() {
        return carBodyType;
    }
}
