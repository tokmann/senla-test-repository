package task_3_3.CarBuilders;

import task_3_3.CarDetails.CarBody;
import task_3_3.CarDetails.Engine;
import task_3_3.Interfaces.ILineStep;
import task_3_3.Interfaces.IProductPart;

public class CarBodyInstaller implements ILineStep {

    @Override
    public IProductPart buildProductPart() {
        System.out.println(">>> Происходит создание и установка кузова...");
        return new CarBody("Кузов седан");
    }
}
