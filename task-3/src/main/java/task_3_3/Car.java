package task_3_3;

import task_3_3.carDetails.CarBody;
import task_3_3.carDetails.Chassis;
import task_3_3.carDetails.Engine;
import task_3_3.interfaces.IProduct;
import task_3_3.interfaces.IProductPart;

public class Car implements IProduct {

    private CarBody body;
    private Chassis chassis;
    private Engine engine;

    @Override
    public void installFirstPart(IProductPart productPart) {
        this.body = (CarBody) productPart;
        System.out.println("Установлен кузов: " + body.getType());
    }

    @Override
    public void installSecondPart(IProductPart productPart) {
        this.chassis = (Chassis) productPart;
        System.out.println("Установлено шасси: " + chassis.getChassisType());
    }

    @Override
    public void installThirdPart(IProductPart productPart) {
        this.engine = (Engine) productPart;
        System.out.println("Установлен двигатель: " + engine.getModel());
    }

    public void showCar() {
        System.out.println("\n=== АВТОМОБИЛЬ ===");
        System.out.println("Кузов: " + body.getType());
        System.out.println("Шасси: " + chassis.getChassisType());
        System.out.println("Двигатель: " + engine.getModel());
        System.out.println("========================");
    }
}
