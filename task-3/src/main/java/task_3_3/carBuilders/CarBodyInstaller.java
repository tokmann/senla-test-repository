package task_3_3.carBuilders;

import task_3_3.carDetails.CarBody;
import task_3_3.interfaces.ILineStep;
import task_3_3.interfaces.IProductPart;

public class CarBodyInstaller implements ILineStep {

    private final String bodyType;

    public CarBodyInstaller(String bodyType) {
        this.bodyType = bodyType;
    }

    @Override
    public IProductPart buildProductPart() {
        System.out.println(">>> Происходит создание и установка кузова...");
        return new CarBody(bodyType);
    }
}
