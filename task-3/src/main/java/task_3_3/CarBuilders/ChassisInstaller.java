package task_3_3.CarBuilders;

import task_3_3.CarDetails.Chassis;
import task_3_3.Interfaces.ILineStep;
import task_3_3.Interfaces.IProductPart;

public class ChassisInstaller implements ILineStep {

    @Override
    public IProductPart buildProductPart() {
        System.out.println(">>> Происходит создание и установка шасси автомобиля...");
        return new Chassis("Рамное шасси");
    }
}
