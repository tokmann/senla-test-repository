package task_3_3.carBuilders;

import task_3_3.carDetails.Engine;
import task_3_3.interfaces.ILineStep;
import task_3_3.interfaces.IProductPart;

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
