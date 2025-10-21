package task_3_3.CarBuilders;

import task_3_3.CarDetails.Engine;
import task_3_3.Interfaces.ILineStep;
import task_3_3.Interfaces.IProductPart;

public class EngineInstaller implements ILineStep {

    private final String engineModel;

    public EngineInstaller(String engineModel) {
        this.engineModel = engineModel;
    }

    @Override
    public IProductPart buildProductPart() {
        System.out.println(">>> Происходит создание и установка двигателя...");
        return new Engine(engineModel);
    }
}
